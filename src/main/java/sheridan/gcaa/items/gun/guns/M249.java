package sheridan.gcaa.items.gun.guns;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class M249 extends Gun {
    private static final Caliber caliber =
            new Caliber(new ResourceLocation(GCAA.MODID, "5.56x45mm"),7f, 4.5f, 6f, 9.5f);

    public M249() {
        super(new GunProperties(2.2f, 1.1f, 2.9f, 0.85f, 0.14f,
                3.5f, GunProperties.toRPM(750), getTicks(5.25f), getTicks(6.6f), 150,
                1.6f, 0.6f, 0.1f, 0.1f, 30, List.of(Auto.AUTO),
                ModSounds.M249_FIRE, null, caliber));
    }

    @Override
    public boolean isFreeBlot() {
        return true;
    }
}
