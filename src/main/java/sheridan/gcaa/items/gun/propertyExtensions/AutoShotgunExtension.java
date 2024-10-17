package sheridan.gcaa.items.gun.propertyExtensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;

public class AutoShotgunExtension extends SingleReloadExtension{
    public static final String NAME = new ResourceLocation(GCAA.MODID, "auto_shotgun_extension").toString();
    public static final String CHAMBER_RELOAD_LENGTH = "chamber_reload_length";
    public int chamberReloadLength;
    public int triggerChamberReloadDelay;

    public AutoShotgunExtension(int enterDelay, int chamberReloadLength, int triggerChamberReloadDelay, int singleReloadLength,
                                int exitDelay, int singleReloadNum, int triggerReloadDelay) {
        super(enterDelay, singleReloadLength, exitDelay, singleReloadNum, triggerReloadDelay);
        this.chamberReloadLength = chamberReloadLength;
        this.triggerChamberReloadDelay = triggerChamberReloadDelay;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompoundTag getExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag tag = super.getExtendInitialData(prevDataTag);
        tag.putInt(CHAMBER_RELOAD_LENGTH, chamberReloadLength);
        return tag;
    }
}
