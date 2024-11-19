package sheridan.gcaa.client.screens.containers.providers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.screens.containers.AmmunitionModifyMenu;
import sheridan.gcaa.client.screens.containers.VendingMachineMenu;


public class VendingMachineMenuProvider implements MenuProvider {
    private Level level;
    private BlockPos blockPos;

    public VendingMachineMenuProvider(Level level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.gcaa.xxx");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new VendingMachineMenu(pContainerId, pPlayerInventory, level, blockPos);
    }
}