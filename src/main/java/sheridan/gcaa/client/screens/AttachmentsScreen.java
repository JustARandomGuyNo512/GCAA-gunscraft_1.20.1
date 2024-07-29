package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.client.screens.containers.AttachmentMenu;

import java.lang.module.ResolutionException;

@OnlyIn(Dist.CLIENT)
public class AttachmentsScreen extends AbstractContainerScreen<AttachmentMenu> {
    private static final ResourceLocation INVENTORY = new ResourceLocation(GCAA.MODID, "textures/gui/screen/inventory.png");
    private boolean isDraggingModel = false;
    private boolean isRollingModel = false;
    private float modelRX;
    private float modelRY;
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
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
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
            dragStartX = (float) pMouseX;
            dragStartY = (float) pMouseY;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isRollingModel) {
            modelRY = tempModelRY + (float) (pMouseX - dragStartX);
            modelRX = tempModelRX + (float) (pMouseY - dragStartY);
            RenderEvents.setAttachmentScreenModelRot(-modelRX, modelRY);
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int p_97814_) {
        if (isRollingModel) {
            isRollingModel = false;
            tempModelRY = modelRY % 360;
            tempModelRX = modelRX % 360;
        }
        return super.mouseReleased(mx, my, p_97814_);
    }

    private boolean isMouseInModelArea(double mx, double my) {
        return !(mx >= this.leftPos && mx <= this.leftPos + 176 && my >= this.topPos + 160 && my <= this.topPos + 241);
    }
}
