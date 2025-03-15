package sheridan.gcaa.client.animation.recoilAnimation;

import java.util.List;
import java.util.Map;

public class ClampedSpring extends MassDampingSpring{
    public Param upperLimit;
    public Param lowerLimit;
    //public Param originalStiffness;

    public ClampedSpring(String mass, String stiffness, String dampingForward, String dampingBackward, String upperLimit, String lowerLimit)  {
        super(mass, stiffness, dampingForward, dampingBackward);
        this.upperLimit = Param.of(upperLimit);
        this.lowerLimit = Param.of(lowerLimit);
        //this.originalStiffness = Param.of(stiffness);
    }

    @Override
    public double getStiffness() {
        double boundary = position > 0 ? Math.toRadians(upperLimit.val()) : Math.toRadians(lowerLimit.val());
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
        //valueMap.put("stiffness", originalStiffness.strVal());
        return valueMap;
    }

    @Override
    public void updateByMap(Map<String, String> valueMap) {
        super.updateByMap(valueMap);
        upperLimit = Param.of(valueMap.getOrDefault("upperLimit", "0"));
        lowerLimit = Param.of(valueMap.getOrDefault("lowerLimit", "0"));
        //originalStiffness = Param.of(valueMap.getOrDefault("stiffness", "0"));
    }

    @Override
    public void clear() {
        super.clear();
        //stiffness = originalStiffness;
    }
}
