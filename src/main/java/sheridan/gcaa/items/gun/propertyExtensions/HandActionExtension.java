package sheridan.gcaa.items.gun.propertyExtensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.PropertyExtension;

public class HandActionExtension extends PropertyExtension {
    public static final String NAME = new ResourceLocation(GCAA.MODID, "hand_action_extension").toString();
    public static final String START_DELAY = "start_delay";
    public static final String LENGTH = "length";

    public String handActionAnimationName;
    public int startDelay;
    public int length;

    public HandActionExtension(String handActionAnimationName, int startDelay, int length) {
        super(NAME);
        this.handActionAnimationName = handActionAnimationName;
        this.startDelay = startDelay;
        this.length = length;
    }

    @Override
    public CompoundTag getExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag extensionData = new CompoundTag();
        extensionData.putInt(START_DELAY, startDelay);
        extensionData.putInt(LENGTH, length);
        return extensionData;
    }

    @Override
    public boolean hasRateProperty(String name) {
        return false;
    }
}
