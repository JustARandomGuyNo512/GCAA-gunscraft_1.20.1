package sheridan.gcaa.client.screens.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.items.ammunition.IAmmunition;

public class AmmunitionModifyMenu extends AbstractContainerMenu {
    public final BlockPos blockPos;
    public Inventory playerInventory;
    public SimpleContainer ammo;
    public SimpleContainer res;

    public static class AmmunitionSlot extends Slot {

        public AmmunitionSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return pStack.getItem() instanceof IAmmunition && super.mayPlace(pStack);
        }
    }

    public static class ResSlot extends Slot {

        public ResSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack pStack) {
            return false;
        }
    }

    public AmmunitionModifyMenu(int i, Inventory inventory, BlockPos pos) {
        super(ModContainers.AMMUNITION_MODIFY_MENU.get(), i);
        playerInventory = inventory;
        blockPos = pos;
        initContainer();
    }

    public AmmunitionModifyMenu(int i, Inventory inventory) {
        super(ModContainers.AMMUNITION_MODIFY_MENU.get(), i);
        playerInventory = inventory;
        blockPos = BlockPos.ZERO;
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
        this.addSlot(new AmmunitionSlot(ammo, 0, 108, 18));

        res = new SimpleContainer(1);
        this.addSlot(new ResSlot(res, 0, 162, 18));
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
