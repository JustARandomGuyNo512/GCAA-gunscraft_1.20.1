package sheridan.gcaa.client.screens.componets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class OptionalImageButton extends ImageButton {
    private boolean prevented;
    private TooltipController controller;
    private boolean mouseDown;

    public OptionalImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress);
    }

    public void enableIf(boolean condition) {
        this.active = this.visible = condition;
    }

    public boolean isPrevented() {
        return prevented;
    }

    public void setPrevented(boolean prevented) {
        this.prevented = prevented;
        if (controller != null) {
            controller.get(this);
        }
    }

    public OptionalImageButton setTooltipController(TooltipController controller) {
        this.controller = controller;
        return this;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (prevented) {
            pGuiGraphics.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        } else {
            if (mouseDown) {
                pGuiGraphics.setColor(27 / 255f, 161 / 255f, 226 / 255f, 1);
            }
        }
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.setColor(1, 1, 1, 1);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        mouseDown = super.mouseClicked(pMouseX, pMouseY, pButton);
        return mouseDown;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        mouseDown = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    public interface TooltipController {
        Tooltip get(OptionalImageButton btn);
    }

    @Override
    public void onPress() {
        if (!prevented) {
            super.onPress();
        }
    }
}
