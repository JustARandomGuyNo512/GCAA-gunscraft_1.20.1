package sheridan.gcaa.items.guns;

import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.items.guns.calibers.Caliber9x19mm;
import sheridan.gcaa.items.guns.fireModes.Semi;

import java.util.List;

public class G19 extends Gun {
    public G19() {
        super(new GunProperties(List.of(Semi.SEMI), Caliber9x19mm.INSTANCE).setFireDelay(30));
    }
}
