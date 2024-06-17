package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.GunProperties;

public class Akm extends Gun{
    public Akm() {
        super(new GunProperties().setCanHoldInOneHand(false));
    }
}
