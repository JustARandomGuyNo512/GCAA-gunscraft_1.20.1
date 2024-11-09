package sheridan.gcaa.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.screens.componets.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.AmmunitionModifyMenu;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class AmmunitionModifyScreen extends AbstractContainerScreen<AmmunitionModifyMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(GCAA.MODID, "textures/gui/screen/ammunition_modify.png");
    private static final ResourceLocation AMMO_PROGRESS_EMPTY = new ResourceLocation(GCAA.MODID, "textures/gui/component/ammo_progress_empty.png");
    private static final ResourceLocation AMMO_PROGRESS_FILLED = new ResourceLocation(GCAA.MODID, "textures/gui/component/ammo_progress_filled.png");
    private static final ResourceLocation MODIFY_AMMUNITION = new ResourceLocation(GCAA.MODID, "textures/gui/component/modify_ammunition.png");
    private static final int PAGE_SIZE = 16;
    private AmmunitionModifyMenu menu;
    private OptionalImageButton applyBtn;
    private SimpleContainer ammoLeft;
    private SimpleContainer resAmmo;
    private ArrayList<ArrayList<IAmmunitionMod>> allAmmunitionMods = new ArrayList<>();
    private int page = 0;


    public AmmunitionModifyScreen(AmmunitionModifyMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 276;
        this.height = 166;
        this.imageWidth = 276;
        this.imageHeight = 166;
        this.menu = pMenu;
        ammoLeft = pMenu.ammo;
        resAmmo = pMenu.res;
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        gridlayout.defaultCellSetting().padding(4, 4, 4, 4);
        applyBtn = new OptionalImageButton(this.leftPos + 140, this.topPos + 62, 16, 16, 0, 0, 0, MODIFY_AMMUNITION, 16, 16,  (btn) -> {});
        rowHelper.addChild(applyBtn);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.minecraft != null && this.minecraft.player != null) {
            update();
        } else {
            onClose();
        }
    }

    private void update() {
        if (ammoLeft.getItem(0) == ItemStack.EMPTY) {
            applyBtn.setPrevented(true);
            applyBtn.setPreventedTooltipStr("tooltip.btn.need_put_ammo");
        } else if (!(ammoLeft.getItem(0).getItem() instanceof IAmmunition)) {
            applyBtn.setPrevented(true);
            applyBtn.setPreventedTooltipStr("tooltip.btn.not_suitable");
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            pGuiGraphics.blit(AMMO_PROGRESS_EMPTY, startX + 104, startY + 39,  0,0, 84, 24, 84, 24);
            pGuiGraphics.blit(BACKGROUND, startX, startY,  0,0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());
        }
    }
}
