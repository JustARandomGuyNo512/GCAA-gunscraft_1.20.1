package sheridan.gcaa.items;

import sheridan.gcaa.items.guns.ICaliber;
import sheridan.gcaa.items.guns.IGunFireMode;

import java.util.List;

public class GunProperties {
    private float baseDamage;
    private float adsSpeed;
    private int fireDelay;
    private int reloadLength;
    private int fullReloadLength;
    private final List<IGunFireMode> fireModes;
    private final ICaliber caliber;

    public GunProperties(List<IGunFireMode> fireModes, ICaliber caliber) {
        this.fireModes = fireModes;
        this.caliber = caliber;
    }

    public int getReloadLength() {
        return reloadLength;
    }

    public GunProperties setReloadLength(int reloadLength) {
        this.reloadLength = reloadLength;
        return this;
    }

    public int getFullReloadLength() {
        return fullReloadLength;
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

    public List<IGunFireMode> getFireModes() {
        return fireModes;
    }

}
