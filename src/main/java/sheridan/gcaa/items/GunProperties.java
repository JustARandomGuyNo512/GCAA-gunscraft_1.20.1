package sheridan.gcaa.items;

public class GunProperties {
    private float baseDamage;
    private float adsSpeed;
    private int fireDelay;
    private boolean canHoldInOneHand;

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

    public GunProperties setCanHoldInOneHand(boolean canHoldInOneHand) {
        this.canHoldInOneHand = canHoldInOneHand;
        return this;
    }

    public boolean getCanHoldInOneHand() {
        return canHoldInOneHand;
    }

}
