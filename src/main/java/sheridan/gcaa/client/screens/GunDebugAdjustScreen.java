package sheridan.gcaa.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.guns.IGun;

public class GunDebugAdjustScreen extends Screen {
    public GunDebugAdjustScreen() {
        super(Component.literal("Gun Debug Adjust Screen"));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        Font font = Minecraft.getInstance().font;
        pGuiGraphics.drawString(font, "Gun Debug Adjust Screen", (this.width - font.width("Gun Debug Adjust Screen")) / 2, 10, 0xFFFFFF);

    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        rowHelper.addChild(Button.builder(Component.literal("close"), (p_280814_) -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }).width(50).build(), 2, gridlayout.newCellSettings().paddingTop(50));
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void tick() {
        super.tick();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                DisplayData displayData = GunModelRegistry.getDisplayData(gun);
            } else {
                Minecraft.getInstance().setScreen(null);
            }
        }
    }


}
