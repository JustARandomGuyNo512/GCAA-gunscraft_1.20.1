package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;


public class Akm extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_762X39MM,8.5f, 5f, 6f, 10.8f, 0.85f)
                    .setAmmunition(ModItems.AMMO_7_62X39MM.get());

    public Akm() {
        super(new GunProperties(3.5f, 1f, 2.8f, 1f, 0.15f,
                3.2f, GunProperties.toRPM(600), getTicks(2.55f), getTicks(3.65f), 30,
                2.5f, 0.8f, 0.1f, 0.1f, 15, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.AKM_FIRE, null, caliber));
    }
}
