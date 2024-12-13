package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.screens.components.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.BulletCraftingMenu;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;
import sheridan.gcaa.industrial.Recipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.SelectBulletCraftingPacket;
import sheridan.gcaa.network.packets.c2s.StopBulletCraftingPacket;
import sheridan.gcaa.utils.FontUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BulletCraftingScreen extends AbstractContainerScreen<BulletCraftingMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(GCAA.MODID, "textures/gui/screen/bullet_crafting_screen.png");
    private static final ResourceLocation SELECTED_AMMO_MARK = new ResourceLocation(GCAA.MODID, "textures/gui/component/suitable_slot_mark.png");
    private static final ResourceLocation STOP_BULLET_CRAFTING_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/stop_bullet_crafting_btn.png");
    private final List<Ammunition> searchAmmo = new ArrayList<>();
    private EditBox searchBar;
    private static final int pageSize = 21;
    private int currentPage = 0;
    private Ammunition selectedAmmo = null;
    private Slot selectedSlot;
    private boolean isCrafting = false;
    private Vector3i blockPos = null;
    private boolean isStopBtnDown = false;
    private int stopBtnTick = 0;
    private OptionalImageButton stopBtn;
    private float craftingProgress = 0;

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
        //  停止制造按钮
        stopBtn = new OptionalImageButton(this.leftPos + 107, this.topPos + 66, 16, 16, 0, 0, 0, STOP_BULLET_CRAFTING_BTN, 16, 16,  (btn) -> isStopBtnDown = true);
        stopBtn.visible = false;
        stopBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.bullet_crafting.stop")));
        rowHelper.addChild(stopBtn);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        if (isCrafting) {
            String text = Component.translatable("tooltip.bullet_crafting.crafting").getString();
            text += FontUtils.toPercentageStr(craftingProgress);
            pGuiGraphics.drawString(this.font,text, this.leftPos + 85, this.topPos + 20, 0x00ff00);
        }
        // 进度条 先红 中绿 后蓝
        int color = FastColor.ABGR32.color(255, (int) ((1 - craftingProgress) * 255), (int) (craftingProgress * 255), 0);
        pGuiGraphics.fill(leftPos + 85, topPos + 42, (int) (leftPos + 85 + (61 * craftingProgress)), topPos + 46, color);
        if (selectedSlot!=null) {
           RenderSystem.enableBlend();
           pGuiGraphics.blit(SELECTED_AMMO_MARK, this.leftPos + selectedSlot.x, this.topPos + selectedSlot.y, 0,0, 16,16, 16, 16);
           RenderSystem.disableBlend();
        }
        if (isStopBtnDown) {
            renderStopBulletProgress(pGuiGraphics, this.font);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateAmmo();
        int i = this.menu.data.get(BulletCraftingBlockEntity.IS_CRAFTING);
        isCrafting = i == 1;
        stopBtn.visible = isCrafting;
        int id = this.menu.data.get(BulletCraftingBlockEntity.CRAFTING_BULLET_ID);
        if (id == -123456789) {
            selectedSlot = null;
            selectedAmmo = null;
        } else {
            Item item = Item.byId(id);
            if (item instanceof Ammunition ammunition) {
                selectedAmmo = ammunition;
                for (BulletCraftingMenu.DisplaySlot slot : this.menu.displaySlotList) {
                    if (slot.getItem().getItem() == ammunition){
                        selectedSlot = slot;
                        break;
                    }
                }
            }
        }
        int x = this.menu.data.get(BulletCraftingBlockEntity.POS_X);
        int y = this.menu.data.get(BulletCraftingBlockEntity.POS_Y);
        int z = this.menu.data.get(BulletCraftingBlockEntity.POS_Z);
        this.blockPos = new Vector3i(x, y, z);
        if (isStopBtnDown) {
            stopBtnTick = Math.min(stopBtnTick + 1, 50);
            if (stopBtnTick == 50) {
                isStopBtnDown = false;
                stopBtnTick = 0;
                selectedSlot = null;
                selectedAmmo = null;
                // 通知服务端终止制造
                PacketHandler.simpleChannel.sendToServer(new StopBulletCraftingPacket(blockPos.x, blockPos.y, blockPos.z));
            }
        }
        updateCraftingProgress();
    }

    @Override
    protected void slotClicked(@NotNull Slot pSlot, int pSlotId, int pMouseButton, @NotNull ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        if (isCrafting) return;
        if (pSlot instanceof BulletCraftingMenu.DisplaySlot displaySlot) {
            ItemStack item = displaySlot.getItem();
            if (item.getItem() instanceof Ammunition ammunition) {
                selectedAmmo = ammunition;
                selectedSlot = pSlot;
                int id = this.menu.data.get(BulletCraftingBlockEntity.CRAFTING_BULLET_ID);
                // 已经选中了, 重复点击不发包
                if (Item.getId(selectedAmmo) != id && blockPos != null) {
                    ResourceLocation key = ForgeRegistries.ITEMS.getKey(ammunition);
                    if (key == null) return;
                    PacketHandler.simpleChannel.sendToServer(new SelectBulletCraftingPacket(
                            key.toString(),blockPos.x, blockPos.y, blockPos.z
                    ));
                }
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

    /**更新制造进度条*/
    private void updateCraftingProgress() {
        int prev = this.menu.data.get(BulletCraftingBlockEntity.PREV_TICK);
        int total = this.menu.data.get(BulletCraftingBlockEntity.TOTAL_TICK);
        if (total == 0) {
            craftingProgress = 0;
        } else  {
            craftingProgress = (float) prev / total;
        }
    }

    private void renderStopBulletProgress(GuiGraphics graphics, Font font) {
        int w1 = font.width(">");
        int w2 = font.width(".");
        int startX = this.leftPos + 85;
        float progress = stopBtnTick / 40f;
        int count1 = (int) (progress * 60 / w1);
        int count2 = (int) ((1 - progress) * 60 / w2);
        String builder2 = ".".repeat(Math.max(0, count2));
        graphics.drawString(font, ">".repeat(Math.max(0, count1)), startX, this.topPos + 55, 0x00ff00);
        graphics.drawString(font, builder2, startX + 60 - font.width(builder2), this.topPos + 55, 0x00ff00);
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
            list.add(Component.translatable("tooltip.bullet_crafting.detail"));
            return list;
        } else {
            return super.getTooltipFromContainerItem(pStack);
        }
    }
    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        isStopBtnDown = false;
        stopBtnTick = 0;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
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
