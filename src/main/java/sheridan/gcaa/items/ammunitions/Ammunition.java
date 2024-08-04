package sheridan.gcaa.items.ammunitions;

import sheridan.gcaa.items.NoRepairNoEnchantmentItem;

public class Ammunition extends NoRepairNoEnchantmentItem {
    public static final int MAX_CALIBER_MUN = 10;

    public Ammunition(int maxProvideCount) {
        super(new Properties().defaultDurability(maxProvideCount).setNoRepair());
    }



}
