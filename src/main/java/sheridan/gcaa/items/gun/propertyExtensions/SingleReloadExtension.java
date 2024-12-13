package sheridan.gcaa.items.gun.propertyExtensions;

import com.google.gson.JsonObject;
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
    public static final String TRIGGER_RELOAD_DELAY = "trigger_reload_delay";

    public int enterDelay;
    public int singleReloadLength;
    public int exitDelay;
    public int singleReloadNum;
    public int triggerReloadDelay;

    public SingleReloadExtension(int enterDelay, int singleReloadLength, int exitDelay, int singleReloadNum, int triggerReloadDelay)     {
        this.enterDelay = enterDelay;
        this.singleReloadLength = singleReloadLength;
        this.exitDelay = exitDelay;
        this.singleReloadNum = singleReloadNum;
        this.triggerReloadDelay = triggerReloadDelay;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty(ENTER_DELAY, enterDelay);
        jsonObject.addProperty(SINGLE_RELOAD_LENGTH, singleReloadLength);
        jsonObject.addProperty(EXIT_DELAY, exitDelay);
        jsonObject.addProperty(SINGLE_RELOAD_NUM, singleReloadNum);
        jsonObject.addProperty(TRIGGER_RELOAD_DELAY, triggerReloadDelay);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        enterDelay = jsonObject.get(ENTER_DELAY).getAsInt();
        singleReloadLength = jsonObject.get(SINGLE_RELOAD_LENGTH).getAsInt();
        exitDelay = jsonObject.get(EXIT_DELAY).getAsInt();
        singleReloadNum = jsonObject.get(SINGLE_RELOAD_NUM).getAsInt();
        triggerReloadDelay = jsonObject.get(TRIGGER_RELOAD_DELAY).getAsInt();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompoundTag putExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag extensionData = new CompoundTag();
        extensionData.putInt(ENTER_DELAY, enterDelay);
        extensionData.putInt(SINGLE_RELOAD_LENGTH, singleReloadLength);
        extensionData.putInt(EXIT_DELAY, exitDelay);
        extensionData.putInt(SINGLE_RELOAD_NUM, singleReloadNum);
        extensionData.putInt(TRIGGER_RELOAD_DELAY, triggerReloadDelay);
        return extensionData;
    }

    @Override
    public boolean hasRateProperty(String name) {
        return false;
    }
}
