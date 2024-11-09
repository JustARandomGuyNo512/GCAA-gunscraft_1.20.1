package sheridan.gcaa.client.screens.containers.providers;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.screens.containers.AmmunitionModifyMenu;

public class AmmunitionMenuProvider implements MenuProvider {
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.gcaa.ammunition_modify");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AmmunitionModifyMenu(pContainerId, pPlayerInventory, pPlayer.getOnPos());
    }
}