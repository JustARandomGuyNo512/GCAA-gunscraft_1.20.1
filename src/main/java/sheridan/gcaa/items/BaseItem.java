package sheridan.gcaa.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class BaseItem extends Item {
    public static final Map<Enchantment, Integer> EMPTY_ENCHANTMENT_MAP = new HashMap<>();
    public BaseItem(Properties properties) {
        super(properties);
    }
    public BaseItem() {
        super(new Properties().stacksTo(64));
    }
}
