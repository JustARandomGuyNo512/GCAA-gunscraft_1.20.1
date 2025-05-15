package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class Rpk16 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_545X39MM,6.5f, 4f, 6.5f, 12f, 0.8f)
                    .setAmmunition(ModItems.AMMO_5_45X39MM.get());

    public Rpk16() {
        super(new GunProperties(3.5f, 0.55f, 2.35f, 0.65f, 0.16f,
                3f, GunProperties.toRPM(650), getTicks(2.6f), getTicks(3.5f), 30,
                1.6f, 0.45f, 0.14f, 0.12f, 16.7f, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.RPK_16_FIRE, null, caliber));
    }
}

