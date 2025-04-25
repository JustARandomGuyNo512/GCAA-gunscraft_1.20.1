package sheridan.gcaa.items.gun.propertyExtensions;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;

public class SksReloadExtension extends SingleReloadExtension{
    public static final String NAME = new ResourceLocation(GCAA.MODID, "sks_reload_extension").toString();
    public static final String ENTER_DELAY_EMPTY = "enter_delay_empty";
    public int enterDelayEmpty;
    public SksReloadExtension(int enterDelay, int enterDelayEmpty, int singleReloadLength, int exitDelay, int singleReloadNum, int triggerReloadDelay) {
        super(enterDelay, singleReloadLength, exitDelay, singleReloadNum, triggerReloadDelay);
        this.enterDelayEmpty = enterDelayEmpty;
    }
    @Override
    public void writeData(JsonObject jsonObject) {
        super.writeData(jsonObject);
        jsonObject.addProperty(ENTER_DELAY_EMPTY, enterDelayEmpty);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        enterDelayEmpty = jsonObject.get(ENTER_DELAY_EMPTY).getAsInt();
    }

    @Override
    public CompoundTag putExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag tag = super.putExtendInitialData(prevDataTag);
        tag.putInt(ENTER_DELAY_EMPTY, enterDelayEmpty);
        return tag;
    }
    @Override
    public String getName() {
        return NAME;
    }
}
