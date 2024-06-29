package sheridan.gcaa.items.ammunitions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.items.BaseItem;

import java.util.HashMap;
import java.util.Map;

public class Ammunition extends BaseItem {
    public static final Map<Enchantment, Integer> EMPTY_ENCHANTMENT_MAP = new HashMap<>();

    public Ammunition(int maxProvideCount) {
        super(new Properties().defaultDurability(maxProvideCount).setNoRepair());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        return EMPTY_ENCHANTMENT_MAP;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return false;
    }

}
