package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.PumpActionShotgun;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.calibers.CaliberGauge12;
import sheridan.gcaa.items.gun.fireModes.HandAction;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.items.gun.propertyExtensions.SingleReloadExtension;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class M870 extends PumpActionShotgun {
    private static final CaliberGauge12 caliber =
            (CaliberGauge12) new CaliberGauge12(Caliber.CALIBER_12_GAUGE, 4, 2, 4f, 6f, 8)
                    .modifySpread(1.75f)
                    .setAmmunition(ModItems.AMMO_12GAUGE.get());

    public M870() {
        super(new GunProperties(3.7f, 1.3f, 3.5f, 1.2f, 0.25f,
                4f, GunProperties.toRPM(50), 0, 0, 6,
                3.5f, 1f, 0.15f, 0.1f, 10.3f,
                List.of(HandAction.HAND_ACTION), ModSounds.M870_FIRE, ModSounds.M870_FIRE_SUPPRESSED, caliber),
                new HandActionExtension("pump_action","pump_action", getTicks(0.2f), getTicks(0.65f), 3, true),
                new SingleReloadExtension(getTicks(0.4f), getTicks(0.65f), getTicks(0.4f), 1, getTicks(0.35f)));
    }


    @Override
    public boolean shootCreateBulletShell() {
        return false;
    }
}
