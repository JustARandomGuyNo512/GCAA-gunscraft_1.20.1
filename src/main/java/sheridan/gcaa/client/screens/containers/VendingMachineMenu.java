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
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.IProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VendingMachineMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public final BlockPos blockPos;
    public Inventory playerInventory;
    public SimpleContainer exchange;
    public SimpleContainer recycle;
    public RecycleSlot recycleSlot;
    public List<HindSlot> playerInventorySlots;
    public ExchangeSlot exchangeSlot;
    public SimpleContainer products;
    public List<ProductSlot> productSlots;
    public ProductSlot productDisplaySlot;

    public static class ExchangeSlot extends Slot {
        public boolean active = true;

        public ExchangeSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            Set<IProduct> products = ProductsRegister.getProducts(ProductsRegister.EXCHANGE);
            return products.contains(IProduct.of(pStack.getItem()));
        }

        @Override
        public boolean isActive() {
            return active;
        }
    }

    public static class RecycleSlot extends Slot {
        public boolean active = true;

        public RecycleSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            Set<IProduct> products = ProductsRegister.getProducts(ProductsRegister.RECYCLE);
            return products.contains(IProduct.of(pStack.getItem()));
        }

        @Override
        public boolean isActive() {
            return active;
        }
    }

    public static class HindSlot extends Slot {
        public boolean active = true;

        public HindSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean isActive() {
            return active;
        }
    }

    public static class ProductSlot extends HindSlot {
        public IProduct product;

        public ProductSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPickup(@NotNull Player pPlayer) {
            return false;
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
        playerInventorySlots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; ++j) {
                HindSlot slot = new HindSlot(playerInventory, (i + 1) * 9 + j, 48 + j * 18, 84 + i * 18);
                this.addSlot(slot);
                playerInventorySlots.add(slot);
            }
        }
        for (int i = 0; i < 9; i++) {
            HindSlot slot = new HindSlot(playerInventory, i, 48 + i * 18, 142);
            this.addSlot(slot);
            playerInventorySlots.add(slot);
        }

        exchange = new SimpleContainer(1);
        exchangeSlot = new ExchangeSlot(exchange, 0, 96, 35);
        this.addSlot(exchangeSlot);

        recycle = new SimpleContainer(1);
        recycleSlot = new RecycleSlot(recycle, 0, 96, 35);
        this.addSlot(recycleSlot);

        products = new SimpleContainer(35);
        productSlots = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                ProductSlot slot = new ProductSlot(products, i * 5 + j, 45 + j * 18, 32 + i * 18);
                this.addSlot(slot);
                productSlots.add(slot);
                slot.active = false;
            }
        }

        productDisplaySlot = new ProductSlot(new SimpleContainer(1), 0, 185, 50);
        this.addSlot(productDisplaySlot);
        productDisplaySlot.active = false;
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
            this.clearContainer(pPlayer, this.recycle);
        });
    }
}
