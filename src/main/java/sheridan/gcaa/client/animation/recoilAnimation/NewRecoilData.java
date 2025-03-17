package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public class NewRecoilData {
    private static final Map<IGun, NewRecoilData> RECOIL_DATA_REGISTER = new HashMap<>();
    public static final ImpulseScriptParser IMPULSE_SCRIPT_PARSER = new ImpulseScriptParser();
    public static final String TRANS_X = "X", TRANS_Y = "Y", TRANS_Z = "Z", ROT_X = "RX", ROT_Y = "RY", ROT_Z = "RZ";
    public static final String BACK = "Back", UP_ROT = "UpRot", RANDOM_X = "RandomX", RANDOM_Y = "RandomY", SHAKE = "Shake",
            PITCH_CONTROL = "PControl", YAW_CONTROL = "YControl", P_RATE = "PRate", Y_RATE = "YRate", RANDOM = "Random";
    public Map<String, DoubleSupplier> variables = new Object2ObjectArrayMap<>();
    public Map<String, Track[]> data = new Object2ObjectArrayMap<>();
    public Param back, upRot, randomX, randomY, shake;
    public Track[] X = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] Y = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] Z = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] RX = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] RY = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] RZ = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] ALL = new Track[] {X[0], X[1], X[2], Y[0], Y[1], Y[2], Z[0], Z[1], Z[2], RX[0], RX[1], RX[2], RY[0], RY[1], RY[2], RZ[0], RZ[1], RZ[2]};
    private String lastIdentity;
    private float pitchControl, yawControl, pRate, yRate, random;

    public static final NewRecoilData _TEST_ = new NewRecoilData("1", "0.5", "0.25", "0.25", "0.1");

    public NewRecoilData(String back, String upRot, String randomX, String randomY, String shake)  {
        data.put(TRANS_X, X);
        data.put(TRANS_Y, Y);
        data.put(TRANS_Z, Z);
        data.put(ROT_X, RX);
        data.put(ROT_Y, RY);
        data.put(ROT_Z, RZ);
        this.back = Param.of(back);
        this.upRot = Param.of(upRot);
        this.randomX = Param.of(randomX);
        this.randomY = Param.of(randomY);
        this.shake = Param.of(shake);
        initDefaultVariables();
    }

    public void onShoot(ItemStack itemStack, IGun gun, float pitchControl, float yawControl, float pRate, float yRate) {
        String identity = gun.getIdentity(itemStack);
        if (!identity.equals(lastIdentity)) {
            updateMass(itemStack, gun);
            lastIdentity = identity;
        }
        this.pitchControl = pitchControl;
        this.yawControl = yawControl;
        this.pRate = pRate;
        this.yRate = yRate;
        this.random = (float) Math.random();
        for (Track t : ALL) {
            t.applyImpulse();
        }
    }

    protected void updateMass(ItemStack itemStack, IGun gun) {
        float weight = gun.getWeight(itemStack);
        for (Track t : ALL) {
            t.setMass(weight);
        }
    }

    public void apply(PoseStack poseStack) {
        float rx = 0, ry = 0, rz = 0, x = 0, y = 0, z = 0;
        rx += (RX[0].val() + RX[1].val() + RX[2].val());
        ry += (RY[0].val() + RY[1].val() + RY[2].val());
        rz += (RZ[0].val() + RZ[1].val() + RZ[2].val());
        poseStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, rz));
        x += (X[0].val() + X[1].val() + X[2].val());
        y += (Y[0].val() + Y[1].val() + Y[2].val());
        z += (Z[0].val() + Z[1].val() + Z[2].val());
        poseStack.translate(x, y, z);
    }

    public void update() {
        for (Track t : ALL) {
            t.update();
        }
    }

    public static NewRecoilData get(IGun gun) {
        return RECOIL_DATA_REGISTER.get(gun);
    }

    public static void register(IGun gun, NewRecoilData recoilData) {
        RECOIL_DATA_REGISTER.put(gun, recoilData);
    }

    public static class Track {
        public NewRecoilData data;
        public MassDampingSpring spring;
        public int flag;
        public DoubleSupplier valueSupplier;
        public String rawScript;

        Track(MassDampingSpring spring, int flag, NewRecoilData data) {
            this.spring = spring;
            this.flag = flag;
            this.data = data;
            valueSupplier = () -> 0;
        }
        public double val() {
            return spring == null ? 0 : spring.getPosition() * flag;
        }

        public void update() {
            if (spring != null) {
                spring.update();
            }
        }

        public void setMass(float weight) {
            if (spring != null) {
                spring.setMass(weight);
            }
        }

        public void applyImpulse() {
            if (spring != null) {
                spring.applyImpulse(valueSupplier.getAsDouble());
            }
        }

        public void setRawScript(String rawScript) {
            this.rawScript = rawScript;
        }

        public void parseScript() {
            IMPULSE_SCRIPT_PARSER.parse(rawScript, data);
        }

        public static Track empty(NewRecoilData data) {
            return new Track(null, 1, data);
        }
    }

    public Track get(String type, int index) {
        return data.get(type)[index];
    }

    public void addLocalVariable(String type, DoubleSupplier variable) {
        variables.put(type, variable);
    }

    public DoubleSupplier getVariable(String key) {
        return variables.get(key);
    }

    private void initDefaultVariables() {
        variables.put(BACK, () -> back.val());
        variables.put(UP_ROT, () -> upRot.val());
        variables.put(RANDOM_X, () -> randomX.val());
        variables.put(RANDOM_Y, () -> randomY.val());
        variables.put(SHAKE, () -> shake.val());
        variables.put(PITCH_CONTROL, () -> pitchControl);
        variables.put(YAW_CONTROL, () -> yawControl);
        variables.put(P_RATE, () -> pRate);
        variables.put(Y_RATE, () -> yRate);
        variables.put(RANDOM, () -> random);
    }

}
