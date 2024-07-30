package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.client.screens.containers.AttachmentMenu;

import java.lang.module.ResolutionException;

@OnlyIn(Dist.CLIENT)
public class AttachmentsScreen extends AbstractContainerScreen<AttachmentMenu> {
    private static final ResourceLocation INVENTORY = new ResourceLocation(GCAA.MODID, "textures/gui/screen/inventory.png");
    private static final ResourceLocation DRAG_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/drag_btn.png");
    private static final ResourceLocation RESET_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/reset_btn.png");
    private static final ResourceLocation INSTALL_ATTACHMENT_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/install_attachment_btn.png");
    private static final ResourceLocation UNINSTALL_ATTACHMENT_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/uninstall_attachment_btn.png");
    private static final ResourceLocation SELECTED_SLOT = new ResourceLocation(GCAA.MODID, "textures/gui/component/selected_slot.png");
    private static final ResourceLocation SUITABLE_SLOT_MARK = new ResourceLocation(GCAA.MODID, "textures/gui/component/suitable_slot_mark.png");

    private boolean isDraggingModel = false;
    private boolean isRollingModel = false;
    private float modelRX;
    private float modelRY;
    private float modelX;
    private float modelY;
    private float modelScale = 1;
    private float tempModelRX;
    private float tempModelRY;
    private float dragStartX;
    private float dragStartY;
    public AttachmentsScreen(AttachmentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 176;
        this.height = 241;
        this.imageWidth = 176;
        this.imageHeight = 241;
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        gridlayout.defaultCellSetting().padding(4, 4, 4, 4);
        ImageButton resetBtn = new ImageButton(this.leftPos + 144, 20, 16, 16, 0, 0, 0, RESET_BTN, 16, 16,  (btn) -> resetModel());
        resetBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.reset")));
        rowHelper.addChild(resetBtn);
        ImageButton dragBtn = new ImageButton(this.leftPos + 144, 40, 16, 16, 0, 0, 0, DRAG_BTN, 16, 16,  (btn) -> isDraggingModel = true);
        resetBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.drag")));
        rowHelper.addChild(dragBtn);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            pGuiGraphics.blit(INVENTORY, startX, startY,  0,0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!isDraggingModel && isMouseInModelArea(pMouseX, pMouseY)) {
            isRollingModel = true;
        }
        dragStartX = (float) pMouseX;
        dragStartY = (float) pMouseY;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isDraggingModel) {
            modelX += (pMouseX - dragStartX) * 0.5f;
            modelY += (pMouseY - dragStartY) * 0.5f;
            dragStartX = (float) pMouseX;
            dragStartY = (float) pMouseY;
            RenderEvents.setAttachmentScreenModelPos(-modelX * 0.01f, modelY * 0.01f);
            isRollingModel = false;
        } else if (isRollingModel) {
            modelRY = tempModelRY + (float) (pMouseX - dragStartX);
            modelRX = tempModelRX + (float) (pMouseY - dragStartY);
            RenderEvents.setAttachmentScreenModelRot(-modelRX, -modelRY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isMouseInModelArea(mouseX, mouseY)) {
            modelScale += delta * 0.15f;
            modelScale = Mth.clamp(modelScale, 0.5f, 2f);
            RenderEvents.setAttachmentScreenModelScale(modelScale);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int p_97814_) {
        if (isRollingModel) {
            isRollingModel = false;
            tempModelRY = modelRY % 360;
            tempModelRX = modelRX % 360;
        }
        isDraggingModel = false;
        dragStartX = 0;
        dragStartY = 0;
        return super.mouseReleased(mx, my, p_97814_);
    }

    private boolean isMouseInModelArea(double mx, double my) {
        return !(mx >= this.leftPos && mx <= this.leftPos + 176 && my >= this.topPos + 160 && my <= this.topPos + 241);
    }

    private void resetModel() {
        modelRX = 0;
        modelRY = 0;
        modelX = 0;
        modelY = 0;
        tempModelRX = 0;
        tempModelRY = 0;
        dragStartX = 0;
        dragStartY = 0;
        modelScale = 1;
        isDraggingModel = false;
        isRollingModel = false;
        RenderEvents.resetAttachmentScreenModelState();
    }
}
