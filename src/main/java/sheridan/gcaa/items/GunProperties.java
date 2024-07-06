package sheridan.gcaa.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.Commons;
import sheridan.gcaa.items.guns.Gun;
import sheridan.gcaa.items.guns.ICaliber;
import sheridan.gcaa.items.guns.IGunFireMode;

import java.util.List;

public class GunProperties{
    public final float baseDamage;
    public final float adsSpeed;
    public final int fireDelay;
    public final int reloadLength;
    public final int fullReloadLength;
    public final int magSize;
    public final float recoilPitch;
    public final float recoilYaw;
    public final List<IGunFireMode> fireModes;
    public final RegistryObject<SoundEvent> fireSound;
    public final RegistryObject<SoundEvent> suppressedSound;
    public final ICaliber caliber;

    public GunProperties(float baseDamage, float adsSpeed, int fireDelay, int reloadLength, int fullReloadLength,
                         int magSize, float recoilPitch, float recoilYaw, List<IGunFireMode> fireModes,
                         RegistryObject<SoundEvent> fireSound, RegistryObject<SoundEvent> suppressedSound, ICaliber caliber) {
        this.baseDamage = baseDamage;
        this.adsSpeed = adsSpeed;
        this.fireDelay = fireDelay;
        this.reloadLength = reloadLength;
        this.fullReloadLength = fullReloadLength;
        this.magSize = magSize;
        this.recoilPitch = recoilPitch;
        this.recoilYaw = recoilYaw;
        this.fireModes = fireModes;
        this.fireSound = fireSound;
        this.suppressedSound = suppressedSound;
        this.caliber = caliber;
    }
    /**
     * get the rate of fire in rounds per minute, this is not accurate.
     * <br>
     * the fire delay will be calculated based on the 200 tps(updates in every 5ms) looping rate (fireDelay = 60000 / rpm / 5).
     * <br>
     * so if you put the rpm to 600, the fire delay will be 20 and real rpm ≈ 600, but if you put 650, the fire
     * delay is 18, and real rpm ≈ 666.
     * */
    public static int getRPM(int rpm) {
        float ms = 60000f / rpm;
        return (int) (ms / 5);
    }

    /**
     * get the initial data tag when the gun data initialize or update
     * <br>
     * 1.when a new gun ItemStack instance is created, this method will be called to get the initial data tag.
     * <br>
     * 2.when an existing gun ItemStack instance handled by a player, server will check "date" (latest server run time),
     * if the "date" is not equal to the current server run time, this method will be called to create a new data tag,
     * and this tag will be updated by gun's attachments, recalculated to make sure the gun's data is correct.
     * */
    public CompoundTag getInitialData() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("date", Commons.SERVER_START_TIME);
        tag.putFloat("base_damage", baseDamage);
        tag.putFloat("ads_speed", adsSpeed);
        tag.putInt("fire_delay", fireDelay);
        tag.putInt("reload_length", reloadLength);
        tag.putInt("full_reload_length", fullReloadLength);
        tag.putInt("mag_size", magSize);
        tag.putFloat("recoilPitch", recoilPitch);
        tag.putFloat("recoilYaw", recoilYaw);
        tag.putString("muzzle_flash", Gun.MUZZLE_STATE_NORMAL);
        return tag;
    }

    public void setRecoilPitch(CompoundTag propertiesTag, float val) {
        propertiesTag.putFloat("recoil_pitch", val);
    }

    public void setRecoilYaw(CompoundTag propertiesTag, float val) {
        propertiesTag.putFloat("recoil_yaw", val);
    }


}
