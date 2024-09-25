package sheridan.gcaa.items.gun.guns;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.List;

public class G19 extends Gun {
    private static final Caliber caliber =
            new Caliber(new ResourceLocation(GCAA.MODID, "9x19mm"), 6f, 4, 3.5f, 5.5f);

    public G19() {
        super(new GunProperties(4.2f, 0.5f, 1.8f, 0.5f, 0.2f,
                2.5f, GunProperties.toRPM(500), getTicks(2.1f), getTicks(2.9f),
                15, 1f, 1f, 0.1f, 0.1f, 5,
                List.of(Semi.SEMI), ModSounds.G19_FIRE, null, caliber));
    }

    @Override
    public boolean isPistol() {
        return true;
    }

    @Override
    public boolean canUseWithShield() {
        return true;
    }
}
