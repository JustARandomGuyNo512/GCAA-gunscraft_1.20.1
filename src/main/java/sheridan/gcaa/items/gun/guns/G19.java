package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Pistol;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class G19 extends Pistol {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_9MM, 6f, 4, 3.5f, 5.5f, 0.6f)
                    .setAmmunition(ModItems.AMMO_9X19MM.get());

    public G19() {
        super(new GunProperties(4.4f, 0.5f, 1.8f, 0.5f, 0.2f,
                2.5f, GunProperties.toRPM(1000), getTicks(2f), getTicks(2.15f),
                15, 1f, 1f, 0.1f, 0.1f, 5,
                List.of(Semi.SEMI), ModSounds.G19_FIRE, null, caliber));
    }

}
