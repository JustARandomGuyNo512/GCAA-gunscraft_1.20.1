package sheridan.gcaa.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class NoRepairNoEnchantmentItem extends BaseItem{
    public NoRepairNoEnchantmentItem(Properties properties) {
        super(properties);
    }

    public NoRepairNoEnchantmentItem() {
        super();
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
