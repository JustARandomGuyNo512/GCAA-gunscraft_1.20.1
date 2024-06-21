package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.GunProperties;

public interface IGun {
    GunProperties getGunProperties();
    @Deprecated
    default boolean canHoldInOneHand() {return false;}
}
