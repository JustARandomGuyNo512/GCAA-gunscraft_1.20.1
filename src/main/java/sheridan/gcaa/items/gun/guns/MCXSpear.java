package sheridan.gcaa.items.gun.guns;


import net.minecraft.nbt.CompoundTag;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Auto;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;

public class MCXSpear extends Gun {
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_545X39MM,13f, 7.5f, 8f, 14f, 1.1f)
                    .setAmmunition(ModItems.AMMO_5_45X39MM.get());

    public MCXSpear() {
        super(new GunProperties(3f, 0.75f, 2.8f, 0.85f, 0.2f,
                4f, GunProperties.toRPM(800), getTicks(2.4f), getTicks(3.0f), 20,
                3.1f, 0.8f, 0.14f, 0.13f, 14.4f, Arrays.asList(Semi.SEMI, Auto.AUTO),
                ModSounds.MCX_SPEAR_FIRE, ModSounds.MCX_SPEAR_FIRE_SUPPRESSED, caliber));
    }

    @Override
    public CompoundTag getInitialData() {
        CompoundTag initialData = super.getInitialData();
        getGunProperties().setMuzzleFlash(initialData, MUZZLE_STATE_SUPPRESSOR);
        return initialData;
    }

}
