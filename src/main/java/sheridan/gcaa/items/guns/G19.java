package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.items.guns.calibers.Caliber7_62x39mm;
import sheridan.gcaa.items.guns.calibers.Caliber9x19mm;
import sheridan.gcaa.items.guns.fireModes.Auto;
import sheridan.gcaa.items.guns.fireModes.Semi;
import sheridan.gcaa.sounds.ModSounds;

import java.util.Arrays;
import java.util.List;

public class G19 extends Gun {
    public G19() {
        super(new GunProperties(0f, 0f, GunProperties.getRPM(500), 0,
                0, 15, 1f, 1f, 0.1f, 0.1f, List.of(Semi.SEMI),
                ModSounds.G19_FIRE, null, Caliber9x19mm.INSTANCE));
    }
}
