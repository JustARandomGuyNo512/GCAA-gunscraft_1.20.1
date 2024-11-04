package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class Mk47 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_762X39MM,8f, 5f, 6.5f, 10f)
                    .setAmmunition(ModItems.AMMO_7_62X39MM.get());

    public Mk47() {
        super(new GunProperties(3.7f, 0.85f, 2.5f, 0.8f, 0.16f,
                3.5f, GunProperties.toRPM(700), getTicks(2.85f), getTicks(3.55f), 30,
                2f, 0.85f, 0.1f, 0.1f, 14, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.MK47_FIRE, null, caliber));
    }
}
