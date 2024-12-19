package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class HkG28 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_762X51MM,16f, 9f, 8f, 14f, 1f)
                    .setAmmunition(ModItems.AMMO_7_62X51MM.get());

    public HkG28() {
        super(new GunProperties(3.2f, 0.75f, 2.6f, 1.2f, 0.16f,
                4f, GunProperties.toRPM(500), getTicks(2.55f), getTicks(3.65f), 10,
                4f, 1f, 0.1f, 0.1f, 20, List.of(Semi.SEMI),
                ModSounds.HK_G28_FIRE, null, caliber));
    }
}
