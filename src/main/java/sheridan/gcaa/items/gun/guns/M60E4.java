package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.MG;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class M60E4 extends MG {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_762X51MM,14.5f, 8f, 7.5f, 13f, 1f)
                    .setAmmunition(ModItems.AMMO_7_62X51MM.get());

    public M60E4() {
        super(new GunProperties(2.2f, 1.3f, 3.7f, 0.9f, 0.14f,
                4.4f, 22, getTicks(6f), getTicks(7), 100,
                3.5f, 0.75f, 0.1f, 0.11f, 35f, List.of(Auto.AUTO),
                ModSounds.M60E4_FIRE, null, caliber));
    }
}
