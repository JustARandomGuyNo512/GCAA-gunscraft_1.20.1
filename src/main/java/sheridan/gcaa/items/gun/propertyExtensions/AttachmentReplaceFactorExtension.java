package sheridan.gcaa.items.gun.propertyExtensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.PropertyExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentReplaceFactorExtension extends PropertyExtension {
    public static final String NAME = new ResourceLocation(GCAA.MODID, "attachment_replace_factor_extension").toString();

    private final Map<String, List<PropertyEntry>> replaceFactors = new HashMap<>();
    private final Map<String, Float> weightMap = new HashMap<>();

    public AttachmentReplaceFactorExtension() {
        super(NAME);
    }

    public AttachmentReplaceFactorExtension addReplaceFactor(String slotName, float weight, PropertyEntry... entries) {
        replaceFactors.put(slotName, List.of(entries));
        weightMap.put(slotName, weight);
        return this;
    }

    public void onAttachmentAttached(IGun gun, CompoundTag propertiesTag, String slotName) {
        handleDataChange(gun, propertiesTag, slotName, 1);
    }

    public void onAttachmentDetached(IGun gun, CompoundTag propertiesTag, String slotName) {
        handleDataChange(gun, propertiesTag, slotName, -1);
    }

    private void handleDataChange(IGun gun, CompoundTag propertiesTag, String slotName, int sign) {
        if (replaceFactors.containsKey(slotName)) {
            List<PropertyEntry> entries = replaceFactors.get(slotName);
            GunProperties properties = gun.getGunProperties();
            for (PropertyEntry entry : entries) {
                if (weightMap.containsKey(slotName)) {
                    properties.addWeight(propertiesTag, weightMap.get(slotName) * sign);
                }
                properties.setPropertyRateIfHas(entry.propertyName, propertiesTag, (prevRate) -> prevRate + entry.getReplaceFactor() * sign);
            }
        }
    }

    @Override
    public CompoundTag getExtendInitialData(CompoundTag prevDataTag) {
        return null;
    }

    @Override
    public boolean hasRateProperty(String name) {
        return false;
    }

    public static class PropertyEntry {
        private final String propertyName;
        private final float replaceFactor;

        public PropertyEntry(String propertyName, float replaceFactor) {
            this.propertyName = propertyName;
            this.replaceFactor = replaceFactor;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public float getReplaceFactor() {
            return replaceFactor;
        }

    }
}
