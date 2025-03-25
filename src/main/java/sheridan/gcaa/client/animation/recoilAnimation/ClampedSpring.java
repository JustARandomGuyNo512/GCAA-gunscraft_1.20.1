package sheridan.gcaa.client.animation.recoilAnimation;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public class ClampedSpring extends MassDampingSpring{
    public Param upperLimit;
    public Param lowerLimit;

    public ClampedSpring(String mass, String stiffness, String dampingForward, String dampingBackward, String upperLimit, String lowerLimit)  {
        super(mass, stiffness, dampingForward, dampingBackward);
        this.upperLimit = Param.of(upperLimit);
        this.lowerLimit = Param.of(lowerLimit);
    }

    @Override
    public double getStiffness() {
        double boundary = position > 0 ? upperLimit.val() : lowerLimit.val();
        if (Math.abs(position) > boundary) {
            double exceedRatio = Math.abs(position) / boundary;
            return stiffness.val() * Math.max(1, exceedRatio * exceedRatio);
        }
        return stiffness.val();
    }

    @Override
    public List<String> getValueNames() {
        List<String> valueNames = super.getValueNames();
        valueNames.add("upperLimit");
        valueNames.add("lowerLimit");
        return valueNames;
    }

    @Override
    public Map<String, String> getValueMap() {
        Map<String, String> valueMap = super.getValueMap();
        valueMap.put("upperLimit", upperLimit.strVal());
        valueMap.put("lowerLimit", lowerLimit.strVal());
        return valueMap;
    }

    @Override
    public Object copy() {
        return new ClampedSpring(mass.strVal(), stiffness.strVal(), dampingForward.strVal(), dampingBackward.strVal(), upperLimit.strVal(), lowerLimit.strVal()).setName(name);
    }

    @Override
    public void updateByMap(Map<String, String> valueMap) {
        super.updateByMap(valueMap);
        upperLimit = Param.of(valueMap.getOrDefault("upperLimit", "0"));
        lowerLimit = Param.of(valueMap.getOrDefault("lowerLimit", "0"));
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        super.writeData(jsonObject);
        jsonObject.addProperty("upperLimit", upperLimit.strVal());
        jsonObject.addProperty("lowerLimit", lowerLimit.strVal());
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        upperLimit = Param.of(jsonObject.get("upperLimit").getAsString());
        lowerLimit = Param.of(jsonObject.get("lowerLimit").getAsString());
    }

    @Override
    public String genJavaNewCode() {
        return String.format("new ClampedSpring(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")", mass.strVal(), stiffness.strVal(),
                dampingForward.strVal(), dampingBackward.strVal(), upperLimit.strVal(), lowerLimit.strVal());
    }

    @Override
    public void clear() {
        super.clear();
    }
}
