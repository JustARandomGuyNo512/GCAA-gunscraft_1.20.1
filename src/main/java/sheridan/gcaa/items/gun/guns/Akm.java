package sheridan.gcaa.items.gun.guns;


import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.ProjectileData;
import sheridan.gcaa.items.gun.calibers.Caliber7_62x39mm;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.Arrays;


public class Akm extends Gun {
    public Akm() {
        super(new GunProperties(3f, 1f, 2.8f, 1f, 0.15f, 3.2f, GunProperties.getRPM(600),
                RenderAndMathUtils.secondsToTicks(2.55f), RenderAndMathUtils.secondsToTicks(3.65f), 30,
                2.5f, 0.8f, 0.1f, 0.1f, 15, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.AKM_FIRE, null, new Caliber7_62x39mm(new ProjectileData(8, 5, 6f, 7.2f))));
    }
}
