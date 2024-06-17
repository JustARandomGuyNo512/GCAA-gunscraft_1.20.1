package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.BaseItem;
import sheridan.gcaa.items.GunProperties;

public class Gun extends BaseItem implements IGun {
    private final GunProperties gunProperties;

    public Gun(GunProperties gunProperties) {
        super(new Properties().stacksTo(1));
        this.gunProperties = gunProperties;
    }

    @Override
    public GunProperties getGunProperties() {
        return gunProperties;
    }

    @Override
    public boolean canHoldInOneHand() {
        return gunProperties.getCanHoldInOneHand();
    }
}

