package sheridan.gcaa.items;

public class GunProperties {
    private float baseDamage;
    private float adsSpeed;
    private int fireDelay;
    private int reloadLength;
    private int fullReloadLength;

    public GunProperties() {}



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


}
