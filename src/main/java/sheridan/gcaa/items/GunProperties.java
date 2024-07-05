package sheridan.gcaa.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Commons;
import sheridan.gcaa.items.guns.ICaliber;
import sheridan.gcaa.items.guns.IGun;
import sheridan.gcaa.items.guns.IGunFireMode;

import java.util.List;

public class GunProperties{
    public float baseDamage;
    public float adsSpeed;
    public int fireDelay;
    public int reloadLength;
    public int fullReloadLength;
    public int magSize;
    public final List<IGunFireMode> fireModes;

    public ICaliber getCaliber() {
        return caliber;
    }

    private final ICaliber caliber;

    public GunProperties(List<IGunFireMode> fireModes, ICaliber caliber) {
        this.fireModes = fireModes;
        this.caliber = caliber;
    }

    public GunProperties setReloadLength(int reloadLength) {
        this.reloadLength = reloadLength;
        return this;
    }

    public GunProperties setFullReloadLength(int fullReloadLength) {
        this.fullReloadLength = fullReloadLength;
        return this;
    }

    public GunProperties setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
        return this;
    }

    public GunProperties setAdsSpeed(float adsSpeed) {
        this.adsSpeed = adsSpeed;
        return this;
    }

    public GunProperties setFireDelay(int fireDelay) {
        this.fireDelay = fireDelay;
        return this;
    }

    /**
     * set the rate of fire in rounds per minute, this is not accurate.
     * <br>
     * the fire delay will be calculated based on the 200 tps looping rate (fireDelay = 60000 / rpm / 5).
     * <br>
     * so if you set the rpm to 600, the fire delay will be 20 and real rpm is 600, but if you put 650, the fire
     * delay is 18, and real rpm is 666.
     * */
    public GunProperties setRPM(int rpm) {
        float ms = 60000f / rpm;
        return setFireDelay((int) (ms / 5));
    }

    public CompoundTag getInitialData() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("date", Commons.SERVER_START_TIME);
        tag.putFloat("base_damage", baseDamage);
        tag.putFloat("ads_speed", adsSpeed);
        tag.putInt("fire_delay", fireDelay);
        tag.putInt("reload_length", reloadLength);
        tag.putInt("full_reload_length", fullReloadLength);
        tag.putInt("mag_size", magSize);
        tag.putString("muzzle_flash", "normal");
        return tag;
    }

    public GunProperties get() {
        return this;
    }


}
