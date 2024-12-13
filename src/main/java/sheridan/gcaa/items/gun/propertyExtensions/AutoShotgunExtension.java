package sheridan.gcaa.items.gun.propertyExtensions;

import com.google.gson.JsonObject;
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
    public void writeData(JsonObject jsonObject) {
        super.writeData(jsonObject);
        jsonObject.addProperty(CHAMBER_RELOAD_LENGTH, chamberReloadLength);
        jsonObject.addProperty("trigger_chamber_reload_delay", triggerChamberReloadDelay);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        chamberReloadLength = jsonObject.get(CHAMBER_RELOAD_LENGTH).getAsInt();
        triggerChamberReloadDelay = jsonObject.get("trigger_chamber_reload_delay").getAsInt();
    }

    @Override
    public CompoundTag putExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag tag = super.putExtendInitialData(prevDataTag);
        tag.putInt(CHAMBER_RELOAD_LENGTH, chamberReloadLength);
        return tag;
    }
}
