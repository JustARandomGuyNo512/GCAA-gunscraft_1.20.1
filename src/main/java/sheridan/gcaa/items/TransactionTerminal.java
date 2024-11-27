package sheridan.gcaa.items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.client.screens.TransactionTerminalScreen;

public class TransactionTerminal extends NoRepairNoEnchantmentItem {

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pLevel.isClientSide()) {
            Minecraft.getInstance().setScreen(new TransactionTerminalScreen());
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
