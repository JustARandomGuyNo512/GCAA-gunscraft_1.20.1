package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.Pistol;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Charge;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Python357 extends Pistol {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_357_MAGNUM, 9.5f, 6f, 5f, 6.6f, 0.65f)
                    .setAmmunition(ModItems.AMMO_357MAGNUM.get());

    public Python357() {
        super(new GunProperties(4.8f, 0.4f, 2.6f, 1f, 0.3f,
                3.6f, GunProperties.toRPM(200), getTicks(3.2f), getTicks(3.2f),
                6, 1.5f, 1.2f, 0.12f, 0.1f, 6,
                List.of(new Charge(3, "double_action", false)), ModSounds.PYTHON_357_FIRE, null, caliber));
    }

    @Override
    public boolean shootCreateBulletShell() {
        return false;
    }
}
