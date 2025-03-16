package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.IGun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRecoilData {
    private static final Map<IGun, NewRecoilData> RECOIL_DATA_REGISTER = new HashMap<>();
    public static final String TRANS_X = "X", TRANS_Y = "Y", TRANS_Z = "Z", ROT_X = "RX", ROT_Y = "RY", ROT_Z = "RZ";
    public static final String BACK = "Back", UP_ROT = "UpRot", RANDOM_X = "RandomX", RANDOM_Y = "RandomY", SHAKE = "Shake",
            PITCH_CONTROL = "PControl", YAW_CONTROL = "YControl", P_RATE = "PRate", Y_RATE = "YRate", RANDOM = "Random";
    public static final int MAX_TRACK_SIZE = 3;
    public static Map<String, Variable> GLOBAL_VARIABLES = new Object2ObjectArrayMap<>();
    public Map<String, List<Track>> data = new Object2ObjectArrayMap<>();
    public Map<String, Variable> localVariables = new Object2ObjectArrayMap<>();
    public Param back, upRot, randomX, randomY, shake;
    public List<Track> X = new ArrayList<>();
    public List<Track> Y = new ArrayList<>();
    public List<Track> Z = new ArrayList<>();
    public List<Track> RX = new ArrayList<>();
    public List<Track> RY = new ArrayList<>();
    public List<Track> RZ = new ArrayList<>();
    private final List<Track> ALL = new ArrayList<>();
    private String lastIdentity;
    private float pitchControl, yawControl, pRate, yRate, random;

    public static final NewRecoilData __TEST__ = new NewRecoilData("1", "0.5", "0.25", "0.25", "0.1");

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
    }

    protected void updateMass(ItemStack itemStack, IGun gun) {
        float weight = gun.getWeight(itemStack);
        for (Track track : ALL) {
            track.spring.setMass(weight);
        }
    }

    public void apply(PoseStack poseStack) {
        float rx = 0, ry = 0, rz = 0, x = 0, y = 0, z = 0;
        for (Track track : RX) {
            rx += track.val();
        }
        for (Track track : RY) {
            ry += track.val();
        }
        for (Track track : RZ) {
            rz += track.val();
        }
        poseStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, rz));
        for (Track track : X) {
            x += track.val();
        }
        for (Track track : Y) {
            y += track.val();
        }
        for (Track track : Z) {
            z += track.val();
        }
        poseStack.translate(x, y, z);
    }

    public void update() {
        for (Track track : ALL) {
            track.update();
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

        Track(MassDampingSpring spring, int flag, NewRecoilData data) {
            this.spring = spring;
            this.flag = flag;
            this.data = data;
        }
        public double val() {
            return spring.getPosition() * flag;
        }

        public void update() {
            spring.update();
        }

        public void applyImpulse() {

        }

        public void readScript(String script) {

        }
    }

    public void add(String type, MassDampingSpring spring, int flag) {
        if (data.get(type).size() < MAX_TRACK_SIZE) {
            data.get(type).add(new Track(spring, flag, this));
            ALL.add(new Track(spring, flag, this));
        }
    }

    public void remove(String type, int index) {
        Track remove = data.get(type).remove(index);
        ALL.remove(remove);
    }

    public Track get(String type, int index) {
        return data.get(type).get(index);
    }

    public void addLocalVariable(String type, Variable variable) {
        localVariables.put(type, variable);
    }

    public Variable getVariable(String key) {
        Variable variable = GLOBAL_VARIABLES.get(key);
        if (variable == null) {
            return localVariables.get(key);
        }
        return variable;
    }

    public interface Variable {
        double get(NewRecoilData data);
    }

    public static class SteadyVariable implements Variable {
        public SteadyStateSpring steadyStateSpring;

        public SteadyVariable(SteadyStateSpring steadyStateSpring)  {
            this.steadyStateSpring = steadyStateSpring;
        }

        @Override
        public double get(NewRecoilData data) {
            return steadyStateSpring.getProgress();
        }
    }

    static {
        GLOBAL_VARIABLES.put(BACK, (data) -> data.back.val());
        GLOBAL_VARIABLES.put(UP_ROT, (data) -> data.upRot.val());
        GLOBAL_VARIABLES.put(RANDOM_X, (data) -> data.randomX.val());
        GLOBAL_VARIABLES.put(RANDOM_Y, (data) -> data.randomY.val());
        GLOBAL_VARIABLES.put(SHAKE, (data) -> data.shake.val());
        GLOBAL_VARIABLES.put(PITCH_CONTROL, (data) -> data.pitchControl);
        GLOBAL_VARIABLES.put(YAW_CONTROL, (data) -> data.yawControl);
        GLOBAL_VARIABLES.put(P_RATE, (data) -> data.pRate);
        GLOBAL_VARIABLES.put(Y_RATE, (data) -> data.yRate);
        GLOBAL_VARIABLES.put(RANDOM, (data) -> data.random);
    }
}
