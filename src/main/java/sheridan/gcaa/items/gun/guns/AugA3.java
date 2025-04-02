package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class AugA3 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_556X45MM,6.5f, 4f, 6f, 14f, 0.9f)
                    .setAmmunition(ModItems.AMMO_5_56X45MM.get());

    public AugA3() {
        super(new GunProperties(4f, 0.6f, 2.1f, 0.72f, 0.2f,
                3.5f, GunProperties.toRPM(750), getTicks(2.8f), getTicks(3.25f), 30,
                1.75f, 0.4f, 0.14f, 0.14f, 13f, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.AUG_A3_FIRE, null, caliber));
    }
}
