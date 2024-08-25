package sheridan.gcaa.items.gun;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class HandActionGunProperties extends GunProperties{

    public HandActionGunProperties(float adsSpeed, float minSpread, float maxSpread, float shootSpread, float spreadRecover, float fireSoundVol, int fireDelay, int reloadLength, int fullReloadLength, int magSize, float recoilPitch, float recoilYaw, float recoilPitchControl, float recoilYawControl, float weight, List<IGunFireMode> fireModes, RegistryObject<SoundEvent> fireSound, RegistryObject<SoundEvent> suppressedSound, Caliber caliber) {
        super(adsSpeed, minSpread, maxSpread, shootSpread, spreadRecover, fireSoundVol, fireDelay, reloadLength, fullReloadLength, magSize, recoilPitch, recoilYaw, recoilPitchControl, recoilYawControl, weight, fireModes, fireSound, suppressedSound, caliber);
    }
}
