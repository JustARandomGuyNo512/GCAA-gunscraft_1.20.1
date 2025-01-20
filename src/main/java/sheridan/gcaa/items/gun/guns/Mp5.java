package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Burst;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class Mp5 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_9MM,6f, 4.5f, 4.5f, 5.5f, 0.6f)
                    .setAmmunition(ModItems.AMMO_9X19MM.get());

    public Mp5() {
        super(new GunProperties(3.8f, 0.55f, 2.9f, 0.6f, 0.15f,
                3f, GunProperties.toRPM(800), getTicks(2.2f), getTicks(3.15f), 30,
                1.5f, 0.4f, 0.15f, 0.12f, 11.3f, Arrays.asList(Semi.SEMI, new Burst(3), Auto.AUTO),
                ModSounds.MP5_FIRE, null, caliber));
    }
}
