package sheridan.gcaa.entities.industrial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.client.screens.containers.BulletCraftingMenu;
import sheridan.gcaa.entities.ModEntities;

import javax.annotation.Nullable;
import java.util.Iterator;

public class BulletCraftingBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
    private final BlockPos pos;
    protected NonNullList<ItemStack> items;
    protected final ContainerData dataAccess;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};

    public BulletCraftingBlockEntity( BlockPos pPos, BlockState pBlockState) {
        super(ModEntities.BULLET_CRAFTING.get(), pPos, pBlockState);
        this.pos = pPos;
        this.items = NonNullList.withSize(17, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            public int get(int index) {
                return 0;
            }

            public void set(int index, int value) {

            }

            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.literal("");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory) {
        return new BulletCraftingMenu(pContainerId, pInventory, this, this.dataAccess).setBlockPos(this.pos);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
    }

    public int @NotNull [] getSlotsForFace(@NotNull Direction pSide) {
        if (pSide == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return pSide == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return this.canPlaceItem(pIndex, pItemStack);
    }

    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        if (pDirection == Direction.DOWN && pIndex == 1) {
            return pStack.is(Items.WATER_BUCKET) || pStack.is(Items.BUCKET);
        } else {
            return true;
        }
    }

    public void fillStackedContents(@NotNull StackedContents pHelper) {

        for (ItemStack itemstack : this.items) {
            pHelper.accountStack(itemstack);
        }

    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, this.items);
    }
    @Override
    public int getContainerSize() {
        return items.size();
    }

    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.items.iterator();
        ItemStack itemstack;
        do {
            if (!var1.hasNext()) {
                return true;
            }
            itemstack = var1.next();
        } while(itemstack.isEmpty());
        return false;
    }

    public @NotNull ItemStack getItem(int pIndex) {
        return (ItemStack)this.items.get(pIndex);
    }

    public @NotNull ItemStack removeItem(int pIndex, int pCount) {
        return ContainerHelper.removeItem(this.items, pIndex, pCount);
    }

    public @NotNull ItemStack removeItemNoUpdate(int pIndex) {
        return ContainerHelper.takeItem(this.items, pIndex);
    }

    @Override
    public void setItem(int pIndex, @NotNull ItemStack pStack) {
        this.items.set(pIndex, pStack);
        if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    public boolean stillValid(@NotNull Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}
