package sheridan.gcaa.client.animation.recoilAnimation;

import com.google.gson.JsonObject;
import sheridan.gcaa.data.IJsonSyncable;

import java.util.*;

public class MassDampingSpring implements IJsonSyncable {
    static Set<String> names;
    protected String name = "A1";
    public Param mass;          // 质量
    public Param stiffness;     // 刚度
    public Param dampingForward; // 向前（后坐时）的阻尼
    public Param dampingBackward; // 向后（复位时）的阻尼
    public double position;            // 位置
    public double velocity;            // 速度
    public static final double STOP_THRESHOLD = 1e-6; // 静止阈值

    public MassDampingSpring(String mass, String stiffness, String dampingForward, String dampingBackward) {
        this.mass = Param.of(mass);
        this.stiffness = Param.of(stiffness);
        this.dampingForward = Param.of(dampingForward);
        this.dampingBackward = Param.of(dampingBackward);
        this.position = 0.0;
        this.velocity = 0.0;
    }


    public void update() {
        double currentDamping = velocity > 0 ? getDampingForward() : getDampingBackward();
        double acceleration = (- getStiffness() * position - currentDamping * velocity) / getMass();
        velocity += acceleration;
        position += velocity;
    }

    public MassDampingSpring setName(String name) {
        if (names.contains(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        return this;
    }

    public Object copy() {
        return new MassDampingSpring(mass.strVal(), stiffness.strVal(), dampingForward.strVal(), dampingBackward.strVal()).setName(name);
    }

    public String name() {
        return name;
    }

    public void applyImpulse(double force) {
        velocity += force / getMass();
    }

    public double getPosition() {
        return position;
    }

    public List<String> getValueNames() {
        return new ArrayList<>(List.of("mass", "stiffness", "dampingForward", "dampingBackward"));
    }

    public Map<String, String> getValueMap() {
        Map<String, String> map = new HashMap<>();
        map.put("mass", mass.strVal());
        map.put("stiffness", stiffness.strVal());
        map.put("dampingForward", dampingForward.strVal());
        map.put("dampingBackward", dampingBackward.strVal());
        return map;
    }

    public String genJavaNewCode() {
        return "new MassDampingSpring(\"" + mass.strVal() + "\", \"" + stiffness.strVal() + "\", \"" + dampingForward.strVal() + "\", \"" + dampingBackward.strVal() + "\")";
    }

    public void updateByMap(Map<String, String> valueMap) {
        mass = Param.of(valueMap.getOrDefault("mass", "0"));
        stiffness = Param.of(valueMap.getOrDefault("stiffness", "0"));
        dampingForward = Param.of(valueMap.getOrDefault("dampingForward", "0"));
        dampingBackward = Param.of(valueMap.getOrDefault("dampingBackward", "0"));
    }

    public void clear() {
        velocity = 0;
        position = 0;
    }

    public void setMass(double mass) {
        this.mass.setValue(mass);
    }

    public double getMass() {
        return mass.val();
    }

    public double getStiffness() {
        return stiffness.val();
    }

    public double getDampingForward() {
        return dampingForward.val();
    }

    public double getDampingBackward() {
        return dampingBackward.val();
    }

    static {
        names = Set.of(
                "A1", "A2", "A3", "A4", "A5", "A6",
                "B1", "B2", "B3", "B4", "B5", "B6",
                "C1", "C2", "C3", "C4", "C5", "C6");
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty("class", getClass().getName());
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("mass", mass.strVal());
        jsonObject.addProperty("stiffness", stiffness.strVal());
        jsonObject.addProperty("dampingForward", dampingForward.strVal());
        jsonObject.addProperty("dampingBackward", dampingBackward.strVal());
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        name = jsonObject.get("name").getAsString();
        mass = Param.of(jsonObject.get("mass").getAsString());
        stiffness = Param.of(jsonObject.get("stiffness").getAsString());
        dampingForward = Param.of(jsonObject.get("dampingForward").getAsString());
        dampingBackward = Param.of(jsonObject.get("dampingBackward").getAsString());
    }
}
