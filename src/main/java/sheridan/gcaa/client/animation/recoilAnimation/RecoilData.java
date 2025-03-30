package sheridan.gcaa.client.animation.recoilAnimation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import sheridan.gcaa.data.IDataPacketGen;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;
import java.util.regex.Pattern;

public class RecoilData implements IDataPacketGen {
    private static Pattern VARIABLE_PATTEN = Pattern.compile("(\\w+\\.\\w+)");
    private static final Map<IGun, RecoilData> RECOIL_DATA_REGISTER = new HashMap<>();
    private static final Map<String, String> TRACK_VARIABLE_NAME_MAPPING = new HashMap<>();
    public static final String TRANS_X = "X", TRANS_Y = "Y", TRANS_Z = "Z", ROT_X = "RX", ROT_Y = "RY", ROT_Z = "RZ";
    public static final String BACK = "Back", UP_ROT = "UpRot", RANDOM_X = "RandomX", RANDOM_Y = "RandomY", SHAKE = "Shake",
            PITCH_CONTROL = "PControl", YAW_CONTROL = "YControl", P_RATE = "PRate", Y_RATE = "YRate", RANDOM = "Random";
    public static final Map<String, String> GLOBAL_VARIABLES = new ConcurrentHashMap<>();
    public Map<String, String> variables = new ConcurrentHashMap<>();
    private final Random rand;
    public Param back, upRot, randomX, randomY, shake, xRotCenter, yRotCenter;
    public Track[] ALL = new Track[] {
            Track.empty(this), Track.empty(this), Track.empty(this),
            Track.empty(this), Track.empty(this), Track.empty(this),
            Track.empty(this), Track.empty(this), Track.empty(this),
            Track.empty(this), Track.empty(this), Track.empty(this),
            Track.empty(this), Track.empty(this), Track.empty(this),
            Track.empty(this), Track.empty(this), Track.empty(this)};
    private String lastIdentity;
    public float pitchControl, yawControl, pRate, yRate, random, directionX, directionY, shakeVal;

    public RecoilData(String back, String upRot, String randomX, String randomY, String shake)  {
        this(back, upRot, randomX, randomY, shake, "0", "0");
    }

    public RecoilData(String back, String upRot, String randomX, String randomY, String shake, String xRotCenter, String yRotCenter)  {
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
        rx += (ALL[9].val() + ALL[10].val() + ALL[11].val());
        ry += (ALL[12].val() + ALL[13].val() + ALL[14].val());
        rz += (ALL[15].val() + ALL[16].val() + ALL[17].val());
        poseStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, rz));
        x += (ALL[0].val() + ALL[1].val() + ALL[2].val() + yRotCenter.val() * Math.sin(Math.toRadians(ry)));
        y += (ALL[3].val() + ALL[4].val() + ALL[5].val() + xRotCenter.val() * Math.sin(Math.toRadians(rx)));
        z += (ALL[6].val() + ALL[7].val() + ALL[8].val());
        poseStack.translate(x, y, z);
    }

    public void update() {
        for (Track t : ALL) {
            t.update();
        }
    }

    private String getTrackPrefix(int index) {
        String prefix;
        if (index < 3) {
            prefix = "X" + (index % 3 + 1);
        } else if (index < 6) {
            prefix = "Y" + (index % 3 + 1);
        } else if (index < 9) {
            prefix = "Z" + (index % 3+ 1);
        } else if (index < 12) {
            prefix = "RX" + (index % 3+ 1);
        } else if (index < 15) {
            prefix = "RY" + (index % 3+ 1);
        } else {
            prefix = "RZ" + (index % 3+ 1);
        }
        return prefix;
    }

    public void updateVariables(boolean checkIfRecompileScripts) throws RuntimeException {
        variables.clear();
        initDefaultVariables();
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].spring != null) {
                Track track = ALL[i];
                String prefix = getTrackPrefix(i);
                String codeMapping = TRACK_VARIABLE_NAME_MAPPING.get(prefix);
                variables.put(prefix + "_val", "recoilDataInstance." + codeMapping + ".val()");
                if (track.spring instanceof SteadyStateSpring) {
                    variables.put(prefix + "_steady", "((sheridan.gcaa.client.animation.recoilAnimation.SteadyStateSpring)(recoilDataInstance." + codeMapping + ".spring)).getSteady()");
                }
            }
        }
        if (!checkIfRecompileScripts) {
            return;
        }
        for (int i = 0; i < ALL.length; i ++) {
            Track track = ALL[i];
            if (track != null && track.spring != null && track.rawScript != null && !track.rawScript.isBlank()) {
                String script = track.rawScript;
                try {
                    ImpulseScriptGenerator.processScript(script, this);
                } catch (Exception e) {
                    track.valueSupplier = () -> 0;
                    track.rawScript = null;
                    String message = e.getMessage();
                    throw new RuntimeException("The script of track: " + getTrackPrefix(i) + " is not valid now. Please check the script and try again. \n" + message);
                }
            }
        }
    }

    public static RecoilData get(IGun gun) {
        return RECOIL_DATA_REGISTER.get(gun);
    }

    public RecoilData setTrack(int index, Track track) throws Exception {
        ALL[index] = track;
        track.data = this;
        try {
            track.parseScript();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return this;
    }

    public static void register(IGun gun, RecoilData recoilData) {
        RECOIL_DATA_REGISTER.put(gun, recoilData);
    }

    public String genJavaNewCode() {
        StringBuilder str = new StringBuilder("new NewRecoilData(\"" + back.strVal() + "\", \"" + upRot.strVal() + "\", \"" + randomX.strVal() + "\"," +
                " \"" + randomY.strVal() + "\", \"" + shake.strVal() + "\", \"" + xRotCenter.strVal() + "\", \"" + yRotCenter.strVal() + "\")");
        for (int i = 0; i < ALL.length; i++) {
            Track track = ALL[i];
            if (track.spring != null) {
                str.append("\n");
                String s = track.genJavaNewCode();
                str.append(".setTrack(").append(i).append(", ").append(s).append(")");
            }
        }
        str.append(";");
        return str.toString();
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty("back", back.strVal());
        jsonObject.addProperty("upRot", upRot.strVal());
        jsonObject.addProperty("randomX", randomX.strVal());
        jsonObject.addProperty("randomY", randomY.strVal());
        jsonObject.addProperty("shake", shake.strVal());
        jsonObject.addProperty("xRotCenter", xRotCenter.strVal());
        jsonObject.addProperty("yRotCenter", yRotCenter.strVal());
        JsonArray tracks = new JsonArray();
        for (int i = 0; i < ALL.length; i++) {
            Track track = ALL[i];
            if (track.spring != null) {
                JsonObject trackDef = new JsonObject();
                trackDef.addProperty("index", i);
                JsonObject trackData = new JsonObject();
                track.writeData(trackData);
                trackDef.add("track", trackData);
                tracks.add(trackDef);
            }
        }
        jsonObject.add("tracks", tracks);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        back = Param.of(jsonObject.get("back").getAsString());
        upRot = Param.of(jsonObject.get("upRot").getAsString());
        randomX = Param.of(jsonObject.get("randomX").getAsString());
        randomY = Param.of(jsonObject.get("randomY").getAsString());
        shake = Param.of(jsonObject.get("shake").getAsString());
        xRotCenter = Param.of(jsonObject.get("xRotCenter").getAsString());
        yRotCenter = Param.of(jsonObject.get("yRotCenter").getAsString());
        JsonArray tracks = jsonObject.getAsJsonArray("tracks");
        for (JsonElement track : tracks) {
            JsonObject trackDef = track.getAsJsonObject();
            int index = trackDef.get("index").getAsInt();
            ALL[index] = Track.empty(this);
            JsonObject trackData = trackDef.getAsJsonObject("track");
            ALL[index].loadData(trackData);
        }
    }

    public static class Track implements IDataPacketGen{
        public RecoilData data;
        public MassDampingSpring spring;
        public Param flag;
        public DoubleSupplier valueSupplier;
        public String rawScript;

        Track(MassDampingSpring spring, String flag, String rawScript, RecoilData data) {
            this.spring = spring;
            this.flag = Param.of(flag);
            this.data = data;
            this.rawScript = rawScript;
            valueSupplier = () -> 0;
        }

        Track(MassDampingSpring spring, String flag, String rawScript) {
            this(spring, flag, rawScript, null);
        }

        public String genJavaNewCode() {
            return String.format("new Track(%s, \"%s\", %s)",
                    spring == null ? "null" : spring.genJavaNewCode(),
                    flag.strVal(),
                    rawScript == null ? "null" : "\"" + rawScript + "\"");
        }

        public double val() {
            return spring == null ? 0 : spring.getPosition() * flag.val();
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
            this.rawScript = rawScript.trim().replace(" ", "");
        }

        public void parseScript() throws Exception {
            if (rawScript == null || rawScript.isBlank()) {
                return;
            }
            valueSupplier = ImpulseScriptGenerator.createDoubleSupplier(data, rawScript);
        }

        public static Track empty(RecoilData data) {
            return new Track(null, "1", null, data);
        }

        @Override
        public void writeData(JsonObject jsonObject) {
            if (spring != null) {
                JsonObject springObject = new JsonObject();
                spring.writeData(springObject);
                jsonObject.add("spring", springObject);
                jsonObject.addProperty("flag", flag.strVal());
                jsonObject.addProperty("rawScript", rawScript);
            }
        }

        @Override
        public void loadData(JsonObject jsonObject) {
            JsonObject spring = jsonObject.getAsJsonObject("spring");
            String clazz = spring.get("class").getAsString();
            try {
                this.spring = (MassDampingSpring) Class.forName(clazz).getConstructor().newInstance();
            } catch (Exception ignore) {}
            this.spring.loadData(spring);
            this.rawScript = jsonObject.get("rawScript").getAsString();
            try {
                parseScript();
            } catch (Exception ignore) {}
            this.flag = Param.of(jsonObject.get("flag").getAsString());
        }
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

        TRACK_VARIABLE_NAME_MAPPING.put("X1", "ALL[0]");
        TRACK_VARIABLE_NAME_MAPPING.put("X2", "ALL[1]");
        TRACK_VARIABLE_NAME_MAPPING.put("X3", "ALL[2]");
        TRACK_VARIABLE_NAME_MAPPING.put("Y1", "ALL[3]");
        TRACK_VARIABLE_NAME_MAPPING.put("Y2", "ALL[4]");
        TRACK_VARIABLE_NAME_MAPPING.put("Y3", "ALL[5]");
        TRACK_VARIABLE_NAME_MAPPING.put("Z1", "ALL[6]");
        TRACK_VARIABLE_NAME_MAPPING.put("Z2", "ALL[7]");
        TRACK_VARIABLE_NAME_MAPPING.put("Z3", "ALL[8]");
        TRACK_VARIABLE_NAME_MAPPING.put("RX1", "ALL[9]");
        TRACK_VARIABLE_NAME_MAPPING.put("RX2", "ALL[10]");
        TRACK_VARIABLE_NAME_MAPPING.put("RX3", "ALL[11]");
        TRACK_VARIABLE_NAME_MAPPING.put("RY1", "ALL[12]");
        TRACK_VARIABLE_NAME_MAPPING.put("RY2", "ALL[13]");
        TRACK_VARIABLE_NAME_MAPPING.put("RY3", "ALL[14]");
        TRACK_VARIABLE_NAME_MAPPING.put("RZ1", "ALL[15]");
        TRACK_VARIABLE_NAME_MAPPING.put("RZ2", "ALL[16]");
        TRACK_VARIABLE_NAME_MAPPING.put("RZ3", "ALL[17]");
    }
}
