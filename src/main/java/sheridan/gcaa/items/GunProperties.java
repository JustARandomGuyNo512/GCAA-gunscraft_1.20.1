package sheridan.gcaa.items;

import sheridan.gcaa.items.guns.ICaliber;
import sheridan.gcaa.items.guns.IGunFireMode;

import java.util.List;

public class GunProperties {
    public float baseDamage;
    public float adsSpeed;
    public int fireDelay;
    public int reloadLength;
    public int fullReloadLength;
    public final List<IGunFireMode> fireModes;
    public int magSize;

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


}
