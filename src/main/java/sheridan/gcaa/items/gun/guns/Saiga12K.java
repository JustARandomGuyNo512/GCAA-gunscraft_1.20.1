package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.calibers.CaliberGauge12;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class Saiga12K extends Gun {
    private static final CaliberGauge12 caliber =
            (CaliberGauge12) new CaliberGauge12(Caliber.CALIBER_12_GAUGE, 3.5f, 2, 3.6f, 6f, 8)
                    .modifySpread(1.95f)
                    .setAmmunition(ModItems.AMMO_12GAUGE.get())
                    .setPenetration(0.35f);

    public Saiga12K() {
        super(new GunProperties(3.7f, 1.5f, 3.9f, 1.15f, 0.25f,
                4f, GunProperties.toRPM(500), getTicks(2.55f), getTicks(3.4f), 5,
                1.9f, 0.6f, 0.14f, 0.12f, 12.5f, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.SAIGA_12K_FIRE, ModSounds.SAIGA_12K_FIRE_SUPPRESSED, caliber));
    }

    @Override
    public int getCrosshairType() {
        return 1;
    }
}
