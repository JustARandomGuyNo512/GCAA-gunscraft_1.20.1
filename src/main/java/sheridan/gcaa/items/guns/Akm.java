package sheridan.gcaa.items.guns;


import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.items.guns.calibers.Caliber7_62x39mm;
import sheridan.gcaa.items.guns.fireModes.Auto;
import sheridan.gcaa.items.guns.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.Arrays;


public class Akm extends Gun{
    public Akm() {
        super(new GunProperties(0f, 0f, GunProperties.getRPM(600), RenderAndMathUtils.secondsToTicks(2.55f),
                RenderAndMathUtils.secondsToTicks(3f), 30, 2.5f, 0.8f, 0.1f, 0.1f, 10, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.AKM_FIRE, null, Caliber7_62x39mm.INSTANCE));
    }
}
