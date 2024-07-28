package sheridan.gcaa.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.screens.containers.AttachmentMenu;

import java.lang.module.ResolutionException;

@OnlyIn(Dist.CLIENT)
public class AttachmentsScreen extends AbstractContainerScreen<AttachmentMenu> {
    private static final ResourceLocation INVENTORY = new ResourceLocation(GCAA.MODID, "textures/gui/screen/inventory.png");

    public AttachmentsScreen(AttachmentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 440;
        this.height = 245;
        this.imageWidth = 440;
        this.imageHeight = 245;
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            pGuiGraphics.blit(INVENTORY, startX, startY,  0,0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());
        }
    }
}
