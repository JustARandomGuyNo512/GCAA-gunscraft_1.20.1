package sheridan.gcaa.items.gun.guns;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.AutoShotgun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.calibers.CaliberGauge12;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.items.gun.propertyExtensions.AutoShotgunExtension;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Xm1014 extends AutoShotgun {
    private static final CaliberGauge12 caliber =
            (CaliberGauge12) new CaliberGauge12(Caliber.CALIBER_12_GAUGE, 3.5f, 2, 3.75f, 6.25f, 8)
                    .modifySpread(1.85f)
                    .setAmmunition(ModItems.AMMO_12GAUGE.get())
                    .setPenetration(0.35f);

    public Xm1014() {
        super(new GunProperties(3.8f, 1.6f, 3.7f, 1.1f, 0.2f,
                4.5f, GunProperties.toRPM(300), 0, 0, 7,
                4.2f, 1.5f, 0.2f, 0.15f, 14,
                List.of(Semi.SEMI), ModSounds.XM1014_FIRE, ModSounds.XM1014_FIRE_SUPPRESSED, caliber),
                new AutoShotgunExtension(getTicks(0.4f), getTicks(1), getTicks(0.7f),
                        getTicks(0.75f), getTicks(0.4f), 1, getTicks(0.35f)));
    }
}
