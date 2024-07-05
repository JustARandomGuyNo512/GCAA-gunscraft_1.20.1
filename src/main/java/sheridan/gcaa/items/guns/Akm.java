package sheridan.gcaa.items.guns;


import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.items.guns.calibers.Caliber7_62x39mm;
import sheridan.gcaa.items.guns.fireModes.Auto;
import sheridan.gcaa.items.guns.fireModes.Semi;

import java.util.Arrays;


public class Akm extends Gun{
    public Akm() {
        super(new GunProperties(Arrays.asList(Semi.SEMI, Auto.AUTO), Caliber7_62x39mm.INSTANCE).setRPM(600));
    }
}
