package sheridan.gcaa.items.ammunitions;

import sheridan.gcaa.items.NoRepairNoEnchantmentItem;

public class Ammunition extends NoRepairNoEnchantmentItem {

    public Ammunition(int capacity) {
        super(new Properties().defaultDurability(capacity).setNoRepair());
    }

}
