package sheridan.gcaa.client.screens.containers;

import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.items.ammunition.IAmmunition;

public class AmmunitionModifyMenu extends AbstractContainerMenu {
    public final BlockPos blockPos;
    public Inventory playerInventory;
    public SimpleContainer ammo;
    private final ContainerLevelAccess access;

    public static class AmmunitionSlot extends Slot {

        public AmmunitionSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return pStack.getItem() instanceof IAmmunition && super.mayPlace(pStack);
        }
    }

    public AmmunitionModifyMenu(int i, Inventory inventory, Level level, BlockPos pos) {
        super(ModContainers.AMMUNITION_MODIFY_MENU.get(), i);
        playerInventory = inventory;
        blockPos = pos;
        initContainer();
        this.access = ContainerLevelAccess.create(level, pos);
    }

    public AmmunitionModifyMenu(int i, Inventory inventory) {
        super(ModContainers.AMMUNITION_MODIFY_MENU.get(), i);
        playerInventory = inventory;
        blockPos = BlockPos.ZERO;
        this.access = ContainerLevelAccess.NULL;
        initContainer();
    }

    private void initContainer() {
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, (i + 1) * 9 + j, 108 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 108 + i * 18, 142));
        }

        ammo = new SimpleContainer(1);
        this.addSlot(new AmmunitionSlot(ammo, 0, 136, 18));

    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39371_, p_39372_) -> {
            this.clearContainer(pPlayer, this.ammo);
        });
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int id) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D;
    }
}
