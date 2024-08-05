package sheridan.gcaa.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnknownAttachment extends NoRepairNoEnchantmentItem{
    public UnknownAttachment() {}

    public static ItemStack get(String id) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        ItemStack stack = new ItemStack(ModItems.UNKNOWN_ATTACHMENT.get(), 1);
        stack.setTag(tag);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        CompoundTag tag = pStack.getTag();
        if (tag == null) {
            pTooltipComponents.add(Component.translatable("tooltip.item.useless"));
        } else {
            String s = Component.translatable("tooltip.item.unknown_attachment_1").getString();
            String s2 = Component.translatable("tooltip.item.unknown_attachment_3").getString();
            String id = tag.getString("id");
            s = s.replace("$id", id);
            s2 = s2.replace("$id", id);
            pTooltipComponents.add(Component.literal(s));
            pTooltipComponents.add(Component.translatable("tooltip.item.unknown_attachment_2"));
            pTooltipComponents.add(Component.literal(s2));
        }
    }
}
