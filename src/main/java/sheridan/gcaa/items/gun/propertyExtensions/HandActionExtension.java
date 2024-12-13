package sheridan.gcaa.items.gun.propertyExtensions;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.PropertyExtension;

public class HandActionExtension extends PropertyExtension {
    public static final String NAME = new ResourceLocation(GCAA.MODID, "hand_action_extension").toString();
    public static final String START_DELAY = "start_delay";
    public static final String LENGTH = "length";
    public static final String THROW_BULLET_SHELL_DELAY = "throw_bullet_shell_delay";

    public String handActionAnimationName;
    public String adsHandActionAnimationName;
    public int startDelay;
    public int length;
    public int throwBulletShellDelay;
    public boolean allowAds;

    public HandActionExtension(String adsHandActionAnimationName, String handActionAnimationName, int startDelay, int length, int throwBulletShellDelay, boolean allowAds)  {
        this.handActionAnimationName = handActionAnimationName;
        this.adsHandActionAnimationName = adsHandActionAnimationName;
        this.startDelay = startDelay;
        this.length = length;
        this.throwBulletShellDelay = throwBulletShellDelay;
        this.allowAds = allowAds;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty("hand_action_animation_name", handActionAnimationName);
        jsonObject.addProperty("ads_hand_action_animation_name", adsHandActionAnimationName);
        jsonObject.addProperty(START_DELAY, startDelay);
        jsonObject.addProperty(LENGTH, length);
        jsonObject.addProperty(THROW_BULLET_SHELL_DELAY, throwBulletShellDelay);
        jsonObject.addProperty("allow_ads", allowAds);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        handActionAnimationName = jsonObject.get("hand_action_animation_name").getAsString();
        adsHandActionAnimationName = jsonObject.get("ads_hand_action_animation_name").getAsString();
        startDelay = jsonObject.get(START_DELAY).getAsInt();
        length = jsonObject.get(LENGTH).getAsInt();
        throwBulletShellDelay = jsonObject.get(THROW_BULLET_SHELL_DELAY).getAsInt();
        allowAds = jsonObject.get("allow_ads").getAsBoolean();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompoundTag putExtendInitialData(CompoundTag prevDataTag) {
        CompoundTag extensionData = new CompoundTag();
        extensionData.putInt(START_DELAY, startDelay);
        extensionData.putInt(LENGTH, length);
        extensionData.putInt(THROW_BULLET_SHELL_DELAY, throwBulletShellDelay);
        return extensionData;
    }

    @Override
    public boolean hasRateProperty(String name) {
        return false;
    }

}
