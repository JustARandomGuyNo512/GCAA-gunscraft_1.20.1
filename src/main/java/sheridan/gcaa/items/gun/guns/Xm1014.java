package sheridan.gcaa.items.gun.guns;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.AutoShotgun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.CaliberGauge12;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.items.gun.propertyExtensions.AutoShotgunExtension;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class Xm1014 extends AutoShotgun {
    private static final CaliberGauge12 caliber =
            new CaliberGauge12(new ResourceLocation(GCAA.MODID, "12_gauge"), 3.5f, 2, 3.75f, 6.25f, 8)
                    .modifySpread(1.85f);

    public Xm1014() {
        super(new GunProperties(3.8f, 1.4f, 3.6f, 1.1f, 0.22f,
                4.5f, GunProperties.toRPM(280), 0, 0, 7,
                3.0f, 0.9f, 0.2f, 0.15f, 14,
                List.of(Semi.SEMI), ModSounds.XM1014_FIRE, ModSounds.XM1014_FIRE_SUPPRESSED, caliber),
                new AutoShotgunExtension(getTicks(0.4f), getTicks(1), getTicks(0.8f),
                        getTicks(0.75f), getTicks(0.4f), 1, getTicks(0.35f)));
    }
}
