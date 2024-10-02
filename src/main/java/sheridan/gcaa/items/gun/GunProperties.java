package sheridan.gcaa.items.gun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.items.gun.calibers.Caliber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GunProperties{
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
    public static final Set<String> PROPERTIES;
    public final static float MIN_WEIGHT = 5f;
    public final static float MAX_WEIGHT = 40f;

    public final float adsSpeed;
    public final float minSpread;
    public final float maxSpread;
    public final float shootSpread;
    public final float spreadRecover;
    public final float fireSoundVol;
    public final int fireDelay;
    public final int reloadLength;
    public final int fullReloadLength;
    public final int magSize;
    public final float recoilPitch;
    public final float recoilYaw;
    public final float recoilPitchControl;
    public final float recoilYawControl;
    public final float weight;
    public final float walkingSpreadFactor = 1.3f;
    public final float sprintingSpreadFactor = 1.6f;
    public final List<IGunFireMode> fireModes;
    public final RegistryObject<SoundEvent> fireSound;
    public final RegistryObject<SoundEvent> suppressedSound;
    public final Caliber caliber;
    public final Map<String, PropertyExtension> extensions = new HashMap<>();

    public GunProperties(float adsSpeed, float minSpread, float maxSpread, float shootSpread, float spreadRecover, float fireSoundVol, int fireDelay, int reloadLength, int fullReloadLength,
                         int magSize, float recoilPitch, float recoilYaw, float recoilPitchControl, float recoilYawControl, float weight, List<IGunFireMode> fireModes,
                         RegistryObject<SoundEvent> fireSound, RegistryObject<SoundEvent> suppressedSound, Caliber caliber) {
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
    }

    public GunProperties addExtension(PropertyExtension extension) {
        if (!extensions.containsKey(extension.name)) {
            extensions.put(extension.name, extension);
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
        tag.putFloat("ads_speed", 1.0f);
        tag.putFloat("min_spread", 1.0f);
        tag.putFloat("max_spread", 1.0f);
        tag.putFloat("shoot_spread", 1.0f);
        tag.putFloat("spread_recover", 1.0f);
        tag.putInt("fire_delay", fireDelay);
        tag.putInt("reload_length", reloadLength);
        tag.putInt("full_reload_length", fullReloadLength);
        tag.putInt("mag_size", magSize);
        tag.putFloat("recoil_pitch", 1.0f);
        tag.putFloat("recoil_yaw", 1.0f);
        tag.putFloat("recoil_pitch_control", 1.0f);
        tag.putFloat("recoil_yaw_control", 1.0f);
        tag.putString("muzzle_flash", Gun.MUZZLE_STATE_NORMAL);
        tag.putFloat("weight", weight);
        tag.putFloat("walking_spread_factor", 1.0f);
        tag.putFloat("sprinting_spread_factor", 1.0f);
        tag.putFloat("fire_sound_vol", 1.0f);
        for (PropertyExtension extension : extensions.values()) {
            CompoundTag extensionTag = extension.getExtendInitialData(tag);
            if (extensionTag != null) {
                tag.put(extension.name, extensionTag);
            }
        }
        return tag;
    }

    public void setPropertyRateIfHas(String propertyName, CompoundTag propertiesTag, RateSetter rateSetter) {
        float prevRate = getPropertyRate(propertyName, propertiesTag);
        if (prevRate != -1) {
            propertiesTag.putFloat(propertyName, rateSetter.getRate(prevRate));
        }
    }

    public interface RateSetter {
        float getRate(float prevRate);
    }

    public float getPropertyRate(String propertyName, CompoundTag propertiesTag) {
        if (propertiesTag != null) {
             if (isOriginalRateProperty(propertyName)) {
                 return propertiesTag.getFloat(propertyName);
             }
             PropertyExtension extension = ownsRateProperty(propertyName);
             if (extension != null) {
                 CompoundTag tag = propertiesTag.getCompound(extension.name);
                 return tag.getFloat(propertyName);
             }
        }
        return -1;
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
        return extensions.get(extension.name);
    }

    public void setWeight(CompoundTag propertiesTag, int weight) {
        propertiesTag.putFloat("weight", weight);
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
                FIRE_SOUND_VOL
        );
    }
}
