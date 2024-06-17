package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.GunProperties;

public interface IGun {
    GunProperties getGunProperties();
    boolean canHoldInOneHand();
}
