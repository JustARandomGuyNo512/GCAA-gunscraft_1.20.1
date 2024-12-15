package sheridan.gcaa.client.screens.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;
import sheridan.gcaa.industrial.AmmunitionRecipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.ArrayList;
import java.util.List;

public class BulletCraftingMenu extends AbstractContainerMenu {
    private BlockPos blockPos = new BlockPos(0, 0, 0);
    public Inventory playerInventory;
    public final ContainerData data;
    public Container container;
    public SimpleContainer displaySlots = new SimpleContainer(21);
    public List<DisplaySlot> displaySlotList = new ArrayList<>();
    /** 右侧展示Slot */
    public static class DisplaySlot extends Slot {
        public DisplaySlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }
        @Override
        public boolean mayPlace(@NotNull ItemStack pStack) {
            return false;
        }
        @Override
        public boolean mayPickup(@NotNull Player pPlayer) {
            return false;
        }
    }
    /** 工作区Slot*/
    public static class CraftingSlot extends Slot {
        private final ContainerData containerData;
        public CraftingSlot(Container pContainer, int pSlot, int pX, int pY, ContainerData containerData) {
            super(pContainer, pSlot, pX, pY);
            this.containerData = containerData;
        }
        @Override
        public boolean mayPlace(@NotNull ItemStack pStack) {
            // 只能加材料
            int id = this.containerData.get(BulletCraftingBlockEntity.CRAFTING_BULLET_ID);
            if (id == -123456789) {
                return false;
            } else {
                Item item = Item.byId(id);
                if (item instanceof Ammunition ammunition) {
                    AmmunitionRecipe recipe = RecipeRegister.getRecipe(ammunition);
                    if (recipe != null) {
                       return recipe.getIngredients().containsKey(pStack.getItem());
                    } else {
                        return false;
                    }
                }
            }
            return super.mayPlace(pStack);
        }
    }
    /** 生成结果区Slot*/
    public static class ResultSlot extends Slot {
        public ResultSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }
        @Override
        public boolean mayPlace(@NotNull ItemStack pStack) {
            return false;
        }
    }

    // 客户端调用的
    public BulletCraftingMenu(int i, Inventory inventory) {
        super(ModContainers.BULLET_CRAFTING_MENU.get(), i);
        playerInventory = inventory;
        this.data = new SimpleContainerData(16);
        this.addDataSlots(this.data);
        this.container = new SimpleContainer(17);
        initContainer(this.container);
    }
     // 服务器调用的
    public BulletCraftingMenu(int i, Inventory inventory, Container container, ContainerData data) {
        super(ModContainers.BULLET_CRAFTING_MENU.get(), i);
        playerInventory = inventory;
        this.container = container;
        this.data = data;
        initContainer(this.container);
        this.addDataSlots(this.data);
    }

    /**
     * @description 初始化物品栏-添加物品进入界面UI格子
     */
    public void initContainer(Container container) {
        for (int m = 0; m < 3; m++) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, (m + 1) * 9 + j, 8 + j * 18, 84 + m * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.addSlot(new CraftingSlot(container, i * 4 + j, 8 + j * 18, 10 + i * 18, this.data));
            }
        }
        this.addSlot(new ResultSlot(container, 16, 152, 36));
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 3; j++) {
                DisplaySlot displaySlot = new DisplaySlot(displaySlots, i * 3 + j, 186 + j * 22, 24 + i * 17);
                this.addSlot(displaySlot);
                displaySlotList.add(displaySlot);
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D;
    }
    public BulletCraftingMenu setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
        return this;
    }
}
