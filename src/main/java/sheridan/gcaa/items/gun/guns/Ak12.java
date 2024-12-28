package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class Ak12 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_545X39MM,6f, 4f, 5.5f, 11.5f, 0.8f)
                    .setAmmunition(ModItems.AMMO_5_45X39MM.get());

    public Ak12() {
        super(new GunProperties(4f, 0.6f, 2.15f, 0.65f, 0.18f,
                3f, GunProperties.toRPM(650), getTicks(2.6f), getTicks(3.05f), 30,
                1.6f, 0.4f, 0.15f, 0.12f, 11.5f, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.AK12_FIRE, null, caliber));
    }
}
