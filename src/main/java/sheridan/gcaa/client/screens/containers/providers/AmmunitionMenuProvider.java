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


public class AmmunitionMenuProvider implements MenuProvider {

    private Level level;
    private BlockPos blockPos;

    public AmmunitionMenuProvider(Level level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.gcaa.ammunition_modify");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AmmunitionModifyMenu(pContainerId, pPlayerInventory, level, blockPos);
    }
}