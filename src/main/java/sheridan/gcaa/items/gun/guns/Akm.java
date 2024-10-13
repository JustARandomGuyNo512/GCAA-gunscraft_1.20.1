package sheridan.gcaa.items.gun.guns;


import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;


public class Akm extends Gun {
    private static final Caliber caliber =
            new Caliber(new ResourceLocation(GCAA.MODID, "7.62x39mm"),8f, 5f, 6f, 10.8f);

    public Akm() {
        super(new GunProperties(3.5f, 1f, 2.8f, 1f, 0.15f,
                3.2f, GunProperties.toRPM(600), getTicks(2.55f), getTicks(3.65f), 30,
                2.5f, 0.8f, 0.1f, 0.1f, 15, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.AKM_FIRE, null, caliber));
    }
}
