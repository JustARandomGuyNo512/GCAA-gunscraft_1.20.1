package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.items.guns.calibers.Caliber9x19mm;
import sheridan.gcaa.items.guns.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;

public class G19 extends Gun {
    public G19() {
        super(new GunProperties(List.of(Semi.SEMI), Caliber9x19mm.INSTANCE).setRPM(500).setFireSound(ModSounds.G19_FIRE).setMagSize(15));
    }
}
