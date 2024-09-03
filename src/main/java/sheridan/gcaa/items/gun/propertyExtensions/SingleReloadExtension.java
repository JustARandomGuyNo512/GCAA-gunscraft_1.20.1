package sheridan.gcaa.items.gun.propertyExtensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.PropertyExtension;

public class SingleReloadExtension extends PropertyExtension {
    public static final String NAME = new ResourceLocation(GCAA.MODID, "single_reload_extension").toString();
    public static final String ENTER_DELAY = "enter_delay";
    public static final String SINGLE_RELOAD_LENGTH = "single_reload_length";
    public static final String EXIT_DELAY = "exit_delay";

    public int enterDelay;
    public int singleReloadLength;
    public int exitDelay;

    public SingleReloadExtension(int enterDelay, int singleReloadLength, int exitDelay)  {
        super(NAME);
        this.enterDelay = enterDelay;
        this.singleReloadLength = singleReloadLength;
        this.exitDelay = exitDelay;
    }

    @Override
    public CompoundTag getExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag extensionData = new CompoundTag();
        extensionData.putInt(ENTER_DELAY, enterDelay);
        extensionData.putInt(SINGLE_RELOAD_LENGTH, singleReloadLength);
        extensionData.putInt(EXIT_DELAY, exitDelay);
        return extensionData;
    }

    @Override
    public boolean hasRateProperty(String name) {
        return false;
    }
}
