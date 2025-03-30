package sheridan.gcaa.items.gun;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.data.IJsonSyncable;
import sheridan.gcaa.items.gun.calibers.Caliber;

import java.util.*;

public class GunProperties implements IJsonSyncable {
    public static final String ADS_SPEED = "ads_speed";
    public static final String MIN_SPREAD = "min_spread";
    public static final String MAX_SPREAD = "max_spread";
    public static final String SHOOT_SPREAD = "shoot_spread";
    public static final String SPREAD_RECOVER = "spread_recover";
    public static final String RECOIL_PITCH = "recoil_pitch";
    public static final String RECOIL_YAW = "recoil_yaw";
    public static final String RECOIL_PITCH_CONTROL = "recoil_pitch_control";
    public static final String RECOIL_YAW_CONTROL = "recoil_yaw_control";
    public static final String WALKING_SPREAD_FACTOR = "walking_spread_factor";
    public static final String SPRINTING_SPREAD_FACTOR = "sprinting_spread_factor";
    public static final String FIRE_SOUND_VOL = "fire_sound_vol";
    public static final String AGILITY = "agility";
    public static final Set<String> PROPERTIES;
    public final static float MIN_WEIGHT = 5f;
    public final static float MAX_WEIGHT = 40f;

    public float adsSpeed;
    public float minSpread;
    public float maxSpread;
    public float shootSpread;
    public float spreadRecover;
    public float fireSoundVol;
    public int fireDelay;
    public int reloadLength;
    public int fullReloadLength;
    public int magSize;
    public float recoilPitch;
    public float recoilYaw;
    public float recoilPitchControl;
    public float recoilYawControl;
    public float weight;
    public float agility;
    public float walkingSpreadFactor = 1.3f;
    public float sprintingSpreadFactor = 1.6f;
    public List<IGunFireMode> fireModes;
    public RegistryObject<SoundEvent> fireSound;
    public RegistryObject<SoundEvent> suppressedSound;
    public Caliber caliber;
    public Map<String, PropertyExtension> extensions = new HashMap<>();

    public GunProperties(float adsSpeed, float minSpread, float maxSpread, float shootSpread, float spreadRecover, float fireSoundVol, int fireDelay, int reloadLength, int fullReloadLength,
                         int magSize, float recoilPitch, float recoilYaw, float recoilPitchControl, float recoilYawControl, float weight, List<IGunFireMode> fireModes,
                         RegistryObject<SoundEvent> fireSound, @Nullable RegistryObject<SoundEvent> suppressedSound, Caliber caliber) {
        this.adsSpeed = adsSpeed;
        this.fireDelay = fireDelay;
        this.minSpread = minSpread;
        this.shootSpread = shootSpread;
        this.fireSoundVol = fireSoundVol;
        this.maxSpread = maxSpread;
        this.spreadRecover = spreadRecover;
        this.reloadLength = reloadLength;
        this.fullReloadLength = fullReloadLength;
        this.magSize = magSize;
        this.recoilPitch = recoilPitch;
        this.recoilYaw = recoilYaw;
        this.recoilPitchControl = recoilPitchControl;
        this.recoilYawControl = recoilYawControl;
        this.fireModes = fireModes;
        this.fireSound = fireSound;
        this.suppressedSound = suppressedSound;
        this.caliber = caliber;
        this.weight = Mth.clamp(weight, MIN_WEIGHT, MAX_WEIGHT);
        this.agility = 1f;
    }

    public GunProperties() {}

    public GunProperties addExtension(PropertyExtension extension) {
        if (extension != null && !extensions.containsKey(extension.getName())) {
            extensions.put(extension.getName(), extension);
        }
        return this;
    }
    /**
     * get the rate of fire in rounds per minute, this is not accurate.
     * <br>
     * the fire delay will be calculated based on the 200 tps(updates in every 5ms) looping rate (fireDelay = 60000 / rpm / 5).
     * <br>
     * so if you put the rpm to 600, the fire delay will be 20 and real rpm ≈ 600, but if you put 650, the fire
     * delay is 18, and real rpm ≈ 666.
     * */
    public static int toRPM(int rpm) {
        float ms = 60000f / rpm;
        return (int) (ms / 5);
    }

    public GunProperties setMoveSpreadFactor(float walkingSpreadFactor, float sprintingSpreadFactor) {
        this.walkingSpreadFactor = walkingSpreadFactor;
        this.sprintingSpreadFactor = sprintingSpreadFactor;
        return this;
    }

    public int getRPM() {
        return 60000 / (fireDelay * 5);
    }

    /**
     * get the initial data tag when the gun data initialize or handle
     * <br>
     * 1.when a new gun ItemStack instance is created, this method will be called to get the initial data tag.
     * <br>
     * 2.when an existing gun ItemStack instance handled by a player, server will check "date" (latest server run time),
     * if the "date" is not equal to the current server run time, this method will be called to create a new data tag,
     * and this tag will be updated by gun's attachments, recalculated to make sure the gun's data is correct.
     * */
    public CompoundTag getInitialData() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(ADS_SPEED, 1.0f);
        tag.putFloat(MIN_SPREAD, 1.0f);
        tag.putFloat(MAX_SPREAD, 1.0f);
        tag.putFloat(SHOOT_SPREAD, 1.0f);
        tag.putFloat(SPREAD_RECOVER, 1.0f);
        tag.putInt("fire_delay", fireDelay);
        tag.putInt("reload_length", reloadLength);
        tag.putInt("full_reload_length", fullReloadLength);
        tag.putInt("mag_size", magSize);
        tag.putFloat(RECOIL_PITCH, 1.0f);
        tag.putFloat(RECOIL_YAW, 1.0f);
        tag.putFloat(RECOIL_PITCH_CONTROL, 1.0f);
        tag.putFloat(RECOIL_YAW_CONTROL, 1.0f);
        tag.putString("muzzle_flash", Gun.MUZZLE_STATE_NORMAL);
        tag.putFloat("weight", weight);
        tag.putFloat(WALKING_SPREAD_FACTOR, 1.0f);
        tag.putFloat(SPRINTING_SPREAD_FACTOR, 1.0f);
        tag.putFloat(FIRE_SOUND_VOL, 1.0f);
        tag.putFloat(AGILITY, 1.0f);
        for (PropertyExtension extension : extensions.values()) {
            CompoundTag extensionTag = extension.putExtendInitialData(tag);
            if (extensionTag != null) {
                tag.put(extension.getName(), extensionTag);
            }
        }
        return tag;
    }

    public void setPropertyRateIfHas(String propertyName, CompoundTag propertiesTag, RateSetter rateSetter) {
        float prevRate = getPropertyRate(propertyName, propertiesTag);
        if (Float.isNaN(prevRate)) {
            return;
        }
        float res = rateSetter.getRate(prevRate);
        if (!Float.isNaN(res)) {
            propertiesTag.putFloat(propertyName, res);
        }
    }

    public static GunProperties createInstance() {
        return new GunProperties();
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty(ADS_SPEED, adsSpeed);
        jsonObject.addProperty(MIN_SPREAD, minSpread);
        jsonObject.addProperty(MAX_SPREAD, maxSpread);
        jsonObject.addProperty(SHOOT_SPREAD, shootSpread);
        jsonObject.addProperty(SPREAD_RECOVER, spreadRecover);
        jsonObject.addProperty("fire_delay", fireDelay);
        jsonObject.addProperty("reload_length", reloadLength);
        jsonObject.addProperty("full_reload_length", fullReloadLength);
        jsonObject.addProperty("mag_size", magSize);
        jsonObject.addProperty(RECOIL_PITCH, recoilPitch);
        jsonObject.addProperty(RECOIL_YAW, recoilYaw);
        jsonObject.addProperty(RECOIL_PITCH_CONTROL, recoilPitchControl);
        jsonObject.addProperty(RECOIL_YAW_CONTROL, recoilYawControl);
        jsonObject.addProperty("weight", weight);
        jsonObject.addProperty(WALKING_SPREAD_FACTOR, walkingSpreadFactor);
        jsonObject.addProperty(SPRINTING_SPREAD_FACTOR, sprintingSpreadFactor);
        jsonObject.addProperty(FIRE_SOUND_VOL, fireSoundVol);
        jsonObject.addProperty(AGILITY, agility);
        if (!extensions.isEmpty()) {
            JsonObject extensionObject = new JsonObject();
            for (PropertyExtension extension : extensions.values()) {
                JsonObject extensionJson = new JsonObject();
                extension.writeData(extensionJson);
                extensionObject.add(extension.getName(), extensionJson);
            }
            jsonObject.add("extensions", extensionObject);
        }
        if (caliber != null) {
            JsonObject caliberObject = new JsonObject();
            caliber.writeData(caliberObject);
            jsonObject.add("caliber", caliberObject);
        }
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        adsSpeed = jsonObject.get(ADS_SPEED).getAsFloat();
        minSpread = jsonObject.get(MIN_SPREAD).getAsFloat();
        maxSpread = jsonObject.get(MAX_SPREAD).getAsFloat();
        shootSpread = jsonObject.get(SHOOT_SPREAD).getAsFloat();
        spreadRecover = jsonObject.get(SPREAD_RECOVER).getAsFloat();
        fireDelay = jsonObject.get("fire_delay").getAsInt();
        reloadLength = jsonObject.get("reload_length").getAsInt();
        fullReloadLength = jsonObject.get("full_reload_length").getAsInt();
        magSize = jsonObject.get("mag_size").getAsInt();
        recoilPitch = jsonObject.get(RECOIL_PITCH).getAsFloat();
        recoilYaw = jsonObject.get(RECOIL_YAW).getAsFloat();
        recoilPitchControl = jsonObject.get(RECOIL_PITCH_CONTROL).getAsFloat();
        recoilYawControl = jsonObject.get(RECOIL_YAW_CONTROL).getAsFloat();
        weight = jsonObject.get("weight").getAsFloat();
        walkingSpreadFactor = jsonObject.get(WALKING_SPREAD_FACTOR).getAsFloat();
        sprintingSpreadFactor = jsonObject.get(SPRINTING_SPREAD_FACTOR).getAsFloat();
        fireSoundVol = jsonObject.get(FIRE_SOUND_VOL).getAsFloat();
        agility = jsonObject.get(AGILITY).getAsFloat();
        if (!extensions.isEmpty() && jsonObject.has("extensions")) {
            JsonObject extensionObject = jsonObject.get("extensions").getAsJsonObject();
             for (PropertyExtension extension : extensions.values()) {
                 if (extensionObject.has(extension.getName())) {
                     extension.loadData(extensionObject.get(extension.getName()).getAsJsonObject());
                 }
             }
        }
        if (caliber != null && jsonObject.has("caliber")) {
            JsonObject caliberObject = jsonObject.get("caliber").getAsJsonObject();
            caliber.loadData(caliberObject);
        }
    }

    public interface RateSetter {
        float getRate(float prevRate);
    }

    /**
     * Get property rate from properties tag.
     * If the property is not found, return Float.NaN.
     * */
    public float getPropertyRate(String propertyName, CompoundTag propertiesTag) {
        return getPropertyRate(propertyName, propertiesTag, Float.NaN);
    }

    /**
     * Get property rate from properties tag.
     * If the property is not found, return the default value.
     * */
    public float getPropertyRate(String propertyName, CompoundTag propertiesTag, float ifNotHas) {
        if (propertiesTag != null) {
             if (isOriginalRateProperty(propertyName)) {
                 return propertiesTag.getFloat(propertyName);
             }
             PropertyExtension extension = ownsRateProperty(propertyName);
             if (extension != null) {
                 CompoundTag tag = propertiesTag.getCompound(extension.getName());
                 return tag.getFloat(propertyName);
             }
        }
        return ifNotHas;
    }

    public boolean isRateProperty(String propertyName) {
        return isOriginalRateProperty(propertyName) || isExtendRateProperty(propertyName);
    }

    public PropertyExtension ownsRateProperty(String propertyName) {
        for (PropertyExtension extension : extensions.values()) {
            if(extension.hasRateProperty(propertyName)) {
                return extension;
            }
        }
        return null;
    }

    public boolean isExtendRateProperty(String propertyName) {
        return ownsRateProperty(propertyName) != null;
    }

    public boolean isOriginalRateProperty(String propertyName) {
        return GunProperties.PROPERTIES.contains(propertyName);
    }

    public void setMuzzleFlash(CompoundTag propertiesTag, String stateName) {
        propertiesTag.putString("muzzle_flash", stateName);
    }

    public int getMagSize(CompoundTag propertiesTag) {
        return propertiesTag.getInt("mag_size");
    }

    public void setMagSize(CompoundTag propertiesTag, int size) {
        propertiesTag.putInt("mag_size", size);
    }

    public void resetMagSize(CompoundTag propertiesTag) {
        propertiesTag.putInt("mag_size", magSize);
    }

    public boolean hasExtension(String extensionName) {
        return extensions.containsKey(extensionName);
    }

    public PropertyExtension getExtension(String extensionName) {
        return extensions.get(extensionName);
    }

    public PropertyExtension getExtension(PropertyExtension extension) {
        return extensions.get(extension.getName());
    }

    public void setWeight(CompoundTag propertiesTag, float weight) {
        propertiesTag.putFloat("weight", weight);
    }

    public float getWeight(CompoundTag propertiesTag) {
        return propertiesTag.getFloat("weight");
    }

    public void addWeight(CompoundTag propertiesTag, float weight) {
        setWeight(propertiesTag, getWeight(propertiesTag) + weight);
    }

    static {
        PROPERTIES = Set.of(
                ADS_SPEED,
                MIN_SPREAD,
                MAX_SPREAD,
                SHOOT_SPREAD,
                SPREAD_RECOVER,
                RECOIL_PITCH,
                RECOIL_YAW,
                RECOIL_PITCH_CONTROL,
                RECOIL_YAW_CONTROL,
                WALKING_SPREAD_FACTOR,
                SPRINTING_SPREAD_FACTOR,
                FIRE_SOUND_VOL,
                AGILITY
        );
    }
}
