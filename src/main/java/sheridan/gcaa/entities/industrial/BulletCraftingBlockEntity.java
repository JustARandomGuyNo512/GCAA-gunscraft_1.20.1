package sheridan.gcaa.entities.industrial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.client.screens.containers.BulletCraftingMenu;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.industrial.Recipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ammunition.Ammunition;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BulletCraftingBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
    private final BlockPos pos;
    protected NonNullList<ItemStack> items;
    protected final ContainerData dataAccess;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    public static final int IS_CRAFTING = 0;
    private int isCrafting = 0;
    public static final int POS_X = 1;
    public static final int POS_Y = 2;
    public static final int POS_Z = 3;
    private Ammunition currentAmmunition = null;
    public static final int CRAFTING_BULLET_ID = 4;
    public static final int PREV_TICK = 5;
    public static final int TOTAL_TICK = 6;
    public int prevTick;
    public int totalTick;

    public BulletCraftingBlockEntity( BlockPos pPos, BlockState pBlockState) {
        super(ModEntities.BULLET_CRAFTING.get(), pPos, pBlockState);
        this.pos = pPos;
        this.items = NonNullList.withSize(17, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case IS_CRAFTING -> {
                        return isCrafting;
                    }
                    case POS_X -> {
                        return pos.getX();
                    }
                    case POS_Y -> {
                        return pos.getY();
                    }
                    case POS_Z -> {
                        return pos.getZ();
                    }
                    case CRAFTING_BULLET_ID -> {
                        return getCraftingBulletId();
                    }
                    case PREV_TICK -> {
                        return prevTick;
                    }
                    case TOTAL_TICK -> {
                        return totalTick;
                    }
                    default -> {
                        return -1;
                    }
                }
            }

            public void set(int index, int value) {}

            public int getCount() {
                return 16;
            }
        };
    }
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BulletCraftingBlockEntity bulletCraftingBlockEntity) {
        // TODO 进度条监听 -
        if (bulletCraftingBlockEntity.isCrafting == 1) {
            bulletCraftingBlockEntity.prevTick++;
            if (bulletCraftingBlockEntity.prevTick >= bulletCraftingBlockEntity.totalTick) {
                bulletCraftingBlockEntity.prevTick = 0;
                bulletCraftingBlockEntity.isCrafting = 0;
                // TODO: 完成制造, 生成物品
                System.out.println("aaa");
            }
        }
    }

    public void setCraftingBullet(Ammunition ammunition) {
        currentAmmunition = ammunition;
        // 检查能否开始工作
        if (canStartCrafting(ammunition)) {
            // 开始工作
            startBulletCrafting();
        }
    }
    private int getCraftingBulletId() {
        if (currentAmmunition == null) return -1;
        return Item.getId(currentAmmunition);
    }
    /** 能否开始制造工作 */
    private boolean canStartCrafting(Ammunition ammunition) {
        // 判断弹药是否为空
        if (ammunition == null) return false;
        // 获取弹药对应的配方
        Recipe recipe = RecipeRegister.getRecipe(ammunition);
        // 判断配方是否为空
        if (recipe == null || recipe.ingredients.isEmpty()) return false;
        // 获取配方所需的材料
        Map<Item, Integer> ingredients = recipe.getIngredients();
        // 获取材料数量
        int count = ingredients.size();
        // 创建一个Map，用于存储材料及其数量
        Map<Item, Integer> map = new HashMap<>();
        // 遍历物品栏中的物品
        for (ItemStack itemStack : this.items) {
            // 如果所需材料数量为0，则返回true
            if (count == 0) return true;
            // 获取物品
            Item item = itemStack.getItem();
            // 判断物品是否在配方所需材料中
            if (ingredients.containsKey(item)) {
                // 获取所需材料数量
                int needCount;
                // 判断材料是否已经在map中
                if (map.containsKey(item)) {
                    // 如果在，则获取map中的数量
                    needCount = map.get(item);
                }else {
                    // 如果不在，则获取配方中的数量
                    needCount = ingredients.get(item);
                }
                if (needCount == 0) continue;
                // 获取物品的数量
                int haveCount = itemStack.getCount();
                // 判断物品数量是否大于等于所需数量
                if (haveCount >= needCount) {
                    // 如果大于等于，则所需材料数量减1
                    count--;
                    // 将map中的数量置为0
                    map.put(item, 0);
                } else {
                    // 如果小于，则将所需数量减去物品数量
                    map.put(item, needCount - haveCount);
                }
            }
        }
        return count == 0;
    }
    /** 开始制造 */
    private void startBulletCrafting() {
        isCrafting = 1;
        Recipe recipe = RecipeRegister.getRecipe(currentAmmunition);
        totalTick = recipe.craftingTicks;
        // TODO 开始的工作 -
        System.out.println("start");
    }
    /** 停止制造 */
    public void stopBulletCrafting() {
        if (isCrafting == 0) return;
        isCrafting = 0;
        currentAmmunition = null;
        totalTick = 0;
        prevTick = 0;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.literal("Bullet_Crafting");
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
        this.isCrafting = pTag.getInt("isCrafting");
        String str = pTag.getString("currentAmmunition");
        if (!str.equals("key is null")) {
            Item value = ForgeRegistries.ITEMS.getValue(new ResourceLocation(str));
            if (value instanceof Ammunition ammunition) {
                this.currentAmmunition = ammunition;
            }
        }
        this.prevTick = pTag.getInt("prevTick");
        this.totalTick = pTag.getInt("totalTick");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, this.items);
        pTag.putInt("isCrafting", this.isCrafting);
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(currentAmmunition);
        pTag.putString("currentAmmunition", key == null ? "key is null" : key.toString());
        pTag.putInt("prevTick", this.prevTick);
        pTag.putInt("totalTick", this.totalTick);
    }
    @Override
    public int getContainerSize() {
        return items.size();
    }

    public int @NotNull [] getSlotsForFace(@NotNull Direction pSide) {
        if (pSide == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return pSide == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int pIndex, @NotNull ItemStack pItemStack, @Nullable Direction pDirection) {
        return this.canPlaceItem(pIndex, pItemStack);
    }

    public boolean canTakeItemThroughFace(int pIndex, @NotNull ItemStack pStack, @NotNull Direction pDirection) {
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
        return this.items.get(pIndex);
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
        // 每放一次物品的时候检测是否能自动化开始工作
        if (pIndex < 16 && currentAmmunition != null && isCrafting == 0) {
            if (canStartCrafting(currentAmmunition)) {
                startBulletCrafting();
            }
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
