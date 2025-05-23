package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.Pistol;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Fn57 extends Pistol {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_57X28MM, 5f, 4, 5f, 9f, 0.85f)
                    .setAmmunition(ModItems.AMMO_5_7X28MM.get());

    public Fn57() {
        super(new GunProperties(4f, 0.35f, 1.6f, 0.4f, 0.22f,
                3.3f, GunProperties.toRPM(700), getTicks(2f), getTicks(2.15f),
                20, 0.7f, 0.7f, 0.12f, 0.12f, 6,
                List.of(Semi.SEMI), ModSounds.FN57_FIRE, null, caliber));
    }

}

