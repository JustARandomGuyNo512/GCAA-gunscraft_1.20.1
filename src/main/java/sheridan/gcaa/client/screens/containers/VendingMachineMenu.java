package sheridan.gcaa.client.screens.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.IProduct;

import java.util.Set;

public class VendingMachineMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public final BlockPos blockPos;
    public Inventory playerInventory;
    public SimpleContainer exchange;

    public static class ExchangeSlot extends Slot {

        public ExchangeSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            Set<IProduct> products = ProductsRegister.getProducts(ProductsRegister.EXCHANGE);
            return products.contains(IProduct.of(pStack.getItem()));
        }
    }

    public VendingMachineMenu(int i, Inventory inventory, Level level, BlockPos pos) {
        super(ModContainers.VENDING_MACHINE_MENU.get(), i);
        playerInventory = inventory;
        blockPos = pos;
        initContainer();
        this.access = ContainerLevelAccess.create(level, pos);
    }

    public VendingMachineMenu(int i, Inventory inventory) {
        super(ModContainers.VENDING_MACHINE_MENU.get(), i);
        playerInventory = inventory;
        blockPos = BlockPos.ZERO;
        this.access = ContainerLevelAccess.NULL;
        initContainer();
    }

    private void initContainer() {
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, (i + 1) * 9 + j, 48 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 48 + i * 18, 142));
        }

        exchange = new SimpleContainer(1);
        this.addSlot(new ExchangeSlot(exchange, 0, 96, 35));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39371_, p_39372_) -> {
            this.clearContainer(pPlayer, this.exchange);
        });
    }
}
