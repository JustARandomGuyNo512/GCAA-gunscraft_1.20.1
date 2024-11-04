package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class M249 extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_556X45MM,7f, 4.5f, 6f, 14.25f)
                    .setAmmunition(ModItems.AMMO_5_56X45MM.get());

    public M249() {
        super(new GunProperties(2.5f, 1.1f, 2.9f, 0.85f, 0.14f,
                3.5f, GunProperties.toRPM(750), getTicks(5.25f), getTicks(6.6f), 150,
                1.6f, 0.6f, 0.1f, 0.1f, 30, List.of(Auto.AUTO),
                ModSounds.M249_FIRE, null, caliber));
    }

    @Override
    public boolean isFreeBlot() {
        return true;
    }
}
