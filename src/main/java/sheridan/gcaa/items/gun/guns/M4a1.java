package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class M4a1 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_556X45MM,6.5f, 4f, 6.5f, 14f);

    public M4a1() {
        super(new GunProperties(3.8f, 0.7f, 2.3f, 0.8f, 0.18f,
                3.3f, GunProperties.toRPM(850), getTicks(2.6f), getTicks(3.05f), 30,
                1.8f, 0.5f, 0.12f, 0.12f, 12.5f, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.M4A1_FIRE, null, caliber));
    }
}
