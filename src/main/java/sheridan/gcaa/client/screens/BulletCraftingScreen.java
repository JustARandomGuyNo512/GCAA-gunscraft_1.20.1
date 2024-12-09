package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.screens.containers.BulletCraftingMenu;
import sheridan.gcaa.industrial.Recipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BulletCraftingScreen extends AbstractContainerScreen<BulletCraftingMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(GCAA.MODID, "textures/gui/screen/bullet_crafting_screen.png");
    private static final ResourceLocation SELECTED_AMMO_MARK = new ResourceLocation(GCAA.MODID, "textures/gui/component/suitable_slot_mark.png");
    private final List<Ammunition> searchAmmo = new ArrayList<>();
    private EditBox searchBar;
    private static final int pageSize = 21;
    private int currentPage = 0;
    private Ammunition selectedAmmo = null;
    private Slot selectedSlot;

    public BulletCraftingScreen(BulletCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 256;
        this.height = 166;
        this.imageWidth = 256;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        gridlayout.defaultCellSetting().padding(4, 4, 4, 4);
        int leftPos = (this.width - 256) / 2;
        int topPos = (this.height - 166) / 2;
        //  搜索栏
        searchBar = new EditBox(this.font, leftPos + 180, topPos + 2, 71, 12, Component.literal(""));
        searchBar.setBordered(true);
        searchBar.setFGColor(0x000000);
        rowHelper.addChild(searchBar);
        // 上一页按钮
        Button lastPage = Button.builder(Component.literal("<"), (b) -> pageTurning(false)).size(14, 14).pos(leftPos + 179, topPos + 150).build();
        rowHelper.addChild(lastPage);
        // 下一页按钮
        Button nextPage = Button.builder(Component.literal(">"), (b) -> pageTurning(true)).size(14, 14).pos(leftPos + 240, topPos + 150).build();
        rowHelper.addChild(nextPage);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        if (selectedSlot!=null) {
           RenderSystem.enableBlend();
           pGuiGraphics.blit(SELECTED_AMMO_MARK, this.leftPos + selectedSlot.x, this.topPos + selectedSlot.y, 0,0, 16,16, 16, 16);
           RenderSystem.disableBlend();
        }
    }

    @Override
    protected @NotNull List<Component> getTooltipFromContainerItem(@NotNull ItemStack pStack) {
        if (pStack.getItem() instanceof Ammunition ammunition) {
            ArrayList<Component> list = new ArrayList<>();
            list.add(Component.translatable(ammunition.getDescriptionId()));
            Recipe recipe = RecipeRegister.getRecipe(ammunition);
            for (Map.Entry<Item, Integer> entry : recipe.getIngredients().entrySet()) {
                String name = Component.translatable(entry.getKey().getDescriptionId()).getString();
                list.add(Component.literal(name + " x" + entry.getValue()));
            }
            return list;
        } else {
            return super.getTooltipFromContainerItem(pStack);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateAmmo();
    }

    @Override
    protected void slotClicked(@NotNull Slot pSlot, int pSlotId, int pMouseButton, @NotNull ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        if (pSlot instanceof BulletCraftingMenu.DisplaySlot displaySlot) {
            ItemStack item = displaySlot.getItem();
            if (item.getItem() instanceof Ammunition ammunition) {
                selectedAmmo = ammunition;
                selectedSlot = pSlot;
            }
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        super.renderBackground(guiGraphics);
        int pageNum = (int) Math.ceil((double) searchAmmo.size() / pageSize);
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            guiGraphics.blit(BACKGROUND, startX, startY, 0, 0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());
            // 页数
            guiGraphics.drawCenteredString(font, Component.literal((pageNum == 0 ? 0 : currentPage + 1) + "/" + (pageNum)), startX + 216, startY + 153, 0xffffff);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    /** 更新右侧弹药相关 */
    private void updateAmmo() {
        String value = searchBar.getValue();
        searchAmmo.clear();
        List<Ammunition> ammunitionList = Ammunition.getAll();
        for (Ammunition ammunition : ammunitionList) {
            if (RecipeRegister.getRecipe(ammunition) != null &&
               (value.isEmpty() ||
               Component.translatable(ammunition.getDescriptionId()).getString().contains(value))) {
                // 模糊搜索
                searchAmmo.add(ammunition);
            }
        }
        List<Ammunition> pageAmmo = searchAmmo.subList(
                currentPage * pageSize,
                Math.min((currentPage + 1) * pageSize, searchAmmo.size())
        );
        if (!pageAmmo.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 3; j++) {
                    int index = i * 3 + j;
                    if (index < pageAmmo.size()) {
                        this.menu.displaySlots.setItem(index, new ItemStack(pageAmmo.get(index)));
                    } else {
                        this.menu.displaySlots.setItem(index, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
    private void pageTurning(boolean isNextPage) {
        currentPage = isNextPage ? currentPage + 1 : currentPage - 1;
        currentPage = Mth.clamp(currentPage, 0, searchAmmo.size() / pageSize);
    }
}
