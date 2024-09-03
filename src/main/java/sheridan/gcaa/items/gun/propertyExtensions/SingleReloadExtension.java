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
    public static final String SINGLE_RELOAD_NUM = "single_reload_num";

    public int enterDelay;
    public int singleReloadLength;
    public int exitDelay;
    public int singleReloadNum;

    public SingleReloadExtension(int enterDelay, int singleReloadLength, int exitDelay, int singleReloadNum)    {
        super(NAME);
        this.enterDelay = enterDelay;
        this.singleReloadLength = singleReloadLength;
        this.exitDelay = exitDelay;
        this.singleReloadNum = singleReloadNum;
    }

    @Override
    public CompoundTag getExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag extensionData = new CompoundTag();
        extensionData.putInt(ENTER_DELAY, enterDelay);
        extensionData.putInt(SINGLE_RELOAD_LENGTH, singleReloadLength);
        extensionData.putInt(EXIT_DELAY, exitDelay);
        extensionData.putInt(SINGLE_RELOAD_NUM, singleReloadNum);
        return extensionData;
    }

    @Override
    public boolean hasRateProperty(String name) {
        return false;
    }
}
