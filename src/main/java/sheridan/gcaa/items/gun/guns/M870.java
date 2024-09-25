package sheridan.gcaa.items.gun.guns;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.PumpActionShotgun;
import sheridan.gcaa.items.gun.calibers.CaliberGauge12;
import sheridan.gcaa.items.gun.fireModes.HandAction;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.items.gun.propertyExtensions.SingleReloadExtension;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class M870 extends PumpActionShotgun {
    private static final CaliberGauge12 caliber =
            new CaliberGauge12(new ResourceLocation(GCAA.MODID, "12_gauge"), 4, 2, 3.5f, 5f, 8);

    public M870() {
        super(new GunProperties(3.2f, 1.25f, 3f, 1.2f, 0.2f,
                4f, GunProperties.toRPM(50), 0, 0, 6,
                3.5f, 1f, 0.15f, 0.1f, 12,
                List.of(HandAction.HAND_ACTION), ModSounds.M870_FIRE, ModSounds.M870_FIRE_SUPPRESSED, caliber),
                new HandActionExtension("pump_action", getTicks(0.2f), getTicks(0.65f), 3),
                new SingleReloadExtension(getTicks(0.4f), getTicks(0.65f), getTicks(0.4f), 1, getTicks(0.35f)));
    }

    @Override
    public int getCrosshairType() {
        return 1;
    }

    @Override
    public boolean shootCreateBulletShell() {
        return false;
    }
}
