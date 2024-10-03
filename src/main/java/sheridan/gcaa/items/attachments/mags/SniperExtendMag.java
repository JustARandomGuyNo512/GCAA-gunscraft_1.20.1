package sheridan.gcaa.items.attachments.mags;

import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.attachments.Mag;

public class SniperExtendMag extends Mag {
    public SniperExtendMag() {
        super(10);
        addSpecialCapacityFor(ModItems.AWP.get(), 15);
    }
}
