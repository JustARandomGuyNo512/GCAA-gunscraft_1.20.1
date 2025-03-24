package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;

public class NewRecoilData {
    private static final Map<IGun, NewRecoilData> RECOIL_DATA_REGISTER = new HashMap<>();
    //public static final ImpulseScriptParser IMPULSE_SCRIPT_PARSER = new ImpulseScriptParser();
    public static final String TRANS_X = "X", TRANS_Y = "Y", TRANS_Z = "Z", ROT_X = "RX", ROT_Y = "RY", ROT_Z = "RZ";
    public static final String BACK = "Back", UP_ROT = "UpRot", RANDOM_X = "RandomX", RANDOM_Y = "RandomY", SHAKE = "Shake",
            PITCH_CONTROL = "PControl", YAW_CONTROL = "YControl", P_RATE = "PRate", Y_RATE = "YRate", RANDOM = "Random";
    public static final Map<String, String> GLOBAL_VARIABLES = new ConcurrentHashMap<>();
    public Map<String, String> variables = new ConcurrentHashMap<>();
    public Map<String, Track[]> data = new Object2ObjectArrayMap<>();
    private Random rand;
    public Param back, upRot, randomX, randomY, shake, xRotCenter, yRotCenter;
    public Track[] X = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] Y = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] Z = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] RX = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] RY = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] RZ = new Track[] {Track.empty(this), Track.empty(this), Track.empty(this)};
    public Track[] ALL = new Track[] {X[0], X[1], X[2], Y[0], Y[1], Y[2], Z[0], Z[1], Z[2], RX[0], RX[1], RX[2], RY[0], RY[1], RY[2], RZ[0], RZ[1], RZ[2]};
    private String lastIdentity;
    public float pitchControl, yawControl, pRate, yRate, random, directionX, directionY, shakeVal;

    public static final NewRecoilData _TEST_ = new NewRecoilData("1", "0.5", "0.25", "0.25", "0.1");

    public NewRecoilData(String back, String upRot, String randomX, String randomY, String shake)  {
        this(back, upRot, randomX, randomY, shake, "0", "0");
    }

    public NewRecoilData(String back, String upRot, String randomX, String randomY, String shake, String xRotCenter, String yRotCenter)  {
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
        this.xRotCenter = Param.of(xRotCenter);
        this.yRotCenter = Param.of(yRotCenter);
        rand = new Random();
        initDefaultVariables();
    }

    public void onShoot(ItemStack itemStack, IGun gun, float pitchControl, float yawControl,
                        float pRate, float yRate, float directionX, float directionY) {
        String identity = gun.getIdentity(itemStack);
        if (!identity.equals(lastIdentity)) {
            updateMass(itemStack, gun);
            lastIdentity = identity;
        }
        this.pitchControl = pitchControl;
        this.yawControl = yawControl;
        this.pRate = pRate;
        this.yRate = yRate;
        this.random = (float) rand.nextDouble();
        this.directionX = directionX;
        this.directionY = directionY;
        this.shakeVal = (float) rand.nextGaussian();
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
        x += (X[0].val() + X[1].val() + X[2].val() + yRotCenter.val() * Math.sin(Math.toRadians(ry)));
        y += (Y[0].val() + Y[1].val() + Y[2].val() + xRotCenter.val() * Math.sin(Math.toRadians(rx)));
        z += (Z[0].val() + Z[1].val() + Z[2].val());
        poseStack.translate(x, y, z);
    }

    public void update() {
        for (Track t : ALL) {
            t.update();
        }
    }

    public void updateVariables() {
        variables.clear();
        initDefaultVariables();
//        for (Track t : ALL) {
//            try {
//                if (t.spring != null) {
//                    variables.put(t.spring.name() + ".val", () -> t.spring.getPosition());
//                    if (t.spring instanceof SteadyStateSpring steadyStateSpring) {
//                        variables.put(t.spring.name() + ".steady", steadyStateSpring::getSteady);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

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

        public void parseScript() throws Exception {
            valueSupplier = ImpulseScriptGenerator.createDoubleSupplier(data, rawScript);
        }

        public static Track empty(NewRecoilData data) {
            return new Track(null, 1, data);
        }
    }

    public Track get(String type, int index) {
        return data.get(type)[index];
    }

    public String getScriptMapping(String key) {
        String orDefault = GLOBAL_VARIABLES.getOrDefault(key, null);
        return orDefault == null ? variables.getOrDefault(key, null) : orDefault;
    }

    public static double lerp(double pDelta, double pStart, double pEnd) {
        return pStart + pDelta * (pEnd - pStart);
    }

    private void initDefaultVariables() {
        variables.put(BACK, "recoilDataInstance.back.val()");
        variables.put(UP_ROT, "recoilDataInstance.upRot.val()");
        variables.put(RANDOM_X, "recoilDataInstance.randomX.val() * recoilDataInstance.directionX");
        variables.put(RANDOM_Y, "recoilDataInstance.randomY.val() * recoilDataInstance.directionY");
        variables.put(SHAKE, "recoilDataInstance.shake.val() * recoilDataInstance.shakeVal");
        variables.put(PITCH_CONTROL, "recoilDataInstance.pitchControl");
        variables.put(YAW_CONTROL, "recoilDataInstance.yawControl");
        variables.put(P_RATE, "recoilDataInstance.pRate");
        variables.put(Y_RATE, "recoilDataInstance.yRate");
        variables.put(RANDOM, "recoilDataInstance.random");
    }

    static {
        GLOBAL_VARIABLES.put("ABS", "java.lang.Math.abs");
        GLOBAL_VARIABLES.put("MIN", "java.lang.Math.min");
        GLOBAL_VARIABLES.put("MAX", "java.lang.Math.max");
        GLOBAL_VARIABLES.put("SIN", "java.lang.Math.sin");
        GLOBAL_VARIABLES.put("COS", "java.lang.Math.cos");
        GLOBAL_VARIABLES.put("SQRT", "java.lang.Math.sqrt");
        GLOBAL_VARIABLES.put("RADIANS", "java.lang.Math.toRadians");
        GLOBAL_VARIABLES.put("POW", "java.lang.Math.pow");
        GLOBAL_VARIABLES.put("LERP", "sheridan.gcaa.client.animation.recoilAnimation.NewRecoilData.lerp");
    }
}
