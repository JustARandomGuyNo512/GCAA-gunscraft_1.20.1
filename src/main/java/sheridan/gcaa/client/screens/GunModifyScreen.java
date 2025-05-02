package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.client.screens.components.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.GunModifyMenu;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.InstallAttachmentsPacket;
import sheridan.gcaa.network.packets.c2s.ScreenBindAmmunitionPacket;
import sheridan.gcaa.network.packets.c2s.UninstallAttachmentPacket;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class GunModifyScreen extends AbstractContainerScreen<GunModifyMenu> {
    private static final ResourceLocation INVENTORY_CLEAR = new ResourceLocation(GCAA.MODID, "textures/gui/screen/inventory_clear.png");
    private static final ResourceLocation INVENTORY_SHOW_SUITABLE = new ResourceLocation(GCAA.MODID, "textures/gui/screen/inventory_show_suitable.png");
    private static final ResourceLocation DRAG_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/drag_btn.png");
    private static final ResourceLocation RESET_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/reset_btn.png");
    private static final ResourceLocation ZOOM_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/zoom_btn.png");
    private static final ResourceLocation INSTALL_ATTACHMENT_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/install_attachment_btn.png");
    private static final ResourceLocation REPLACE_GUN_PART_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/replace_gun_part_btn.png");
    private static final ResourceLocation UNINSTALL_ATTACHMENT_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/uninstall_attachment_btn.png");
    private static final ResourceLocation SELECTED_SLOT = new ResourceLocation(GCAA.MODID, "textures/gui/component/selected_slot.png");
    private static final ResourceLocation SUITABLE_SLOT_MARK = new ResourceLocation(GCAA.MODID, "textures/gui/component/suitable_slot_mark.png");
    private static final ResourceLocation AMMO_SELECT_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/ammo_select_btn.png");
    private static final ResourceLocation RENDER_MODE = new ResourceLocation(GCAA.MODID, "textures/gui/component/attachment_slot_render_modes.png");

    private AttachmentsGuiContext context;
    private final GunModifyMenu menu;
    private OptionalImageButton installBtn;
    private OptionalImageButton uninstallBtn;
    private OptionalImageButton ammoSelectBtn;
    private IGun gun;
    private final List<Slot> suitableSlots = new ArrayList<>();
    private Slot selectedSlot;
    private boolean isDraggingModel = false;
    private boolean isRollingModel = false;
    private boolean needUpdate = false;
    private float modelRX;
    private float modelRY;
    private float modelX;
    private float modelY;
    private float modelScale = 1;
    private float tempModelRX;
    private float tempModelRY;
    private float dragStartX;
    private float dragStartY;
    private RenderModeBtn renderModeBtn;

    public GunModifyScreen(GunModifyMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 376;
        this.height = 241;
        this.imageWidth = 376;
        this.imageHeight = 241;
        this.menu = pMenu;
    }

    public AttachmentsGuiContext getContext() {
        return context;
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        gridlayout.defaultCellSetting().padding(4, 4, 4, 4);
        ImageButton resetBtn = new ImageButton(this.leftPos + 244, this.topPos + 20, 16, 16, 0, 0, 0, RESET_BTN, 16, 16,  (btn) -> resetModel());
        resetBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.reset")));
        rowHelper.addChild(resetBtn);
        ImageButton dragBtn = new ImageButton(this.leftPos + 244, this.topPos + 40, 16, 16, 0, 0, 0, DRAG_BTN, 16, 16,  (btn) -> isDraggingModel = true);
        dragBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.drag")));
        rowHelper.addChild(dragBtn);
        ImageButton zoomBtn = new ImageButton(this.leftPos + 244, this.topPos + 60, 16, 16, 0, 0, 0, ZOOM_BTN, 16, 16,  (btn) -> {});
        zoomBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.zoom")));
        rowHelper.addChild(zoomBtn);
        renderModeBtn = new RenderModeBtn(this.leftPos + 244, this.topPos + 80, 16, 16, 64, 16);
        rowHelper.addChild(renderModeBtn);
        installBtn = new OptionalImageButton(this.leftPos + 180, this.topPos + 144, 16, 16, 0, 0, 0, INSTALL_ATTACHMENT_BTN, 16, 16,  (btn) -> installAttachment(true));
        uninstallBtn = new OptionalImageButton(this.leftPos + 180, this.topPos + 144, 16, 16, 0, 0, 0, UNINSTALL_ATTACHMENT_BTN, 16, 16,  (btn) -> uninstallAttachment(true));
        ammoSelectBtn = new OptionalImageButton(this.leftPos + 232, this.topPos + 141, 16, 16, 0, 0, 0, AMMO_SELECT_BTN, 16, 16,  (btn) -> selectAmmo());
        installBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.install_attachment")));
        uninstallBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.uninstall_attachment")));
        ammoSelectBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.ammo_select")));
        installBtn.enableIf(false);
        uninstallBtn.enableIf(false);
        Button info = Button.builder(Component.literal("INFO"),
                (btn) -> {
                    AttachmentsGuiContext.showInfoTip = !AttachmentsGuiContext.showInfoTip;
                    btn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.attachment_icon_info").append(AttachmentsGuiContext.showInfoTip ? "ON" : "OFF")));
                }).tooltip(Tooltip.create(Component.translatable("tooltip.btn.attachment_icon_info").append(AttachmentsGuiContext.showInfoTip ? "ON" : "OFF")))
                .size(25, 16).pos(this.leftPos + 102, this.topPos + 20).build();
        rowHelper.addChild(info);
        rowHelper.addChild(installBtn);
        rowHelper.addChild(uninstallBtn);
        rowHelper.addChild(ammoSelectBtn);
        gridlayout.visitWidgets(this::addRenderableWidget);
        if (this.minecraft != null && this.minecraft.player != null) {
            Player player = this.minecraft.player;
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                AttachmentSlot slot = AttachmentsHandler.INSTANCE.getAttachmentSlots(stack);
                this.context = new AttachmentsGuiContext(gun, slot);
                this.gun = gun;
            }
        }
    }

    private void selectAmmo() {
        PacketHandler.simpleChannel.sendToServer(new ScreenBindAmmunitionPacket());
    }

    private void installAttachment(boolean sendPacket) {
        if (context != null && selectedSlot != null && hasPlayer()) {
            AttachmentSlot slot = context.getSelected();
            if (slot != null && selectedSlot.getItem().getItem() instanceof IAttachment attachment) {
                ItemStack stack = this.minecraft.player.getMainHandItem();
                if (stack.getItem() instanceof IGun gun) {
                    AttachmentSlotProxy proxy = context.getProxy();
                    IAttachment.AttachResult attachRes = proxy.onCanAttach(attachment, stack, gun, context.getRoot(), slot);
                    if (attachRes.isPassed()) {
                        if (sendPacket) {
                            String attachmentName = AttachmentsRegister.getStrKey(attachment);
                            PacketHandler.simpleChannel.sendToServer(new InstallAttachmentsPacket(
                                    attachmentName,
                                    slot.slotName,
                                    slot.modelSlotName,
                                    slot.getParent().getId(),
                                    slot.getReplaceableGunPart() == null ? "NONE" : slot.getReplaceableGunPart().ID,
                                    selectedSlot.index,
                                    slot.getDirection()
                            ));
                            needUpdate = true;
                            selectedSlot = null;
                        }
                        uninstallBtn.reset();
                        installBtn.reset();
                    } else {
                        installBtn.setPrevented(true);
                        installBtn.setPreventedTooltipStr(attachRes.getMessage());
                    }
                }
            }
        }
    }

    private void uninstallAttachment(boolean sendPacket) {
        if (context != null && hasPlayer()) {
            AttachmentSlot slot = context.getSelected();
            ItemStack stack = this.minecraft.player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun && slot != null) {
                IAttachment attachment = AttachmentsRegister.get(slot.getAttachmentId());
                if (attachment != null) {
                    AttachmentSlotProxy proxy = context.getProxy();
                    IAttachment.AttachResult detachRes = proxy.onCanDetach(attachment, stack, gun, context.getRoot(), slot);
                    if (detachRes.isPassed()) {
                        if (sendPacket) {
                            PacketHandler.simpleChannel.sendToServer(new UninstallAttachmentPacket(slot.getId(),
                                    slot.getReplaceableGunPart() == null ?
                                    "NONE" : slot.getReplaceableGunPart().ID));
                            needUpdate = true;
                        }
                        installBtn.reset();
                        uninstallBtn.reset();
                    } else {
                        uninstallBtn.setPrevented(true);
                        uninstallBtn.setPreventedTooltipStr(detachRes.getMessage());
                    }
                }
            }
        }
    }

    private boolean hasPlayer() {
        return this.minecraft != null && this.minecraft.player != null;
    }

    public void updateGuiContext(ListTag attachmentsTag, IGun gun) {
        this.context = new AttachmentsGuiContext(gun, AttachmentsHandler.INSTANCE.getAttachmentSlots(attachmentsTag, gun));
        needUpdate = false;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.minecraft != null && this.minecraft.player != null) {
            Player player = minecraft.player;
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                if (!needUpdate) {
                    if (gun != this.gun) {
                        AttachmentSlot slot = AttachmentsHandler.INSTANCE.getAttachmentSlots(stack);
                        this.gun = gun;
                        this.context = new AttachmentsGuiContext(gun, slot);
                    }
                    if (context != null) {
                        AttachmentSlot selected = context.getSelected();
                        if (selected != null) {
                            updateSuitableSlots(selected);
                        } else {
                            menu.displaySuitableAttachments.clearContent();
                            suitableSlots.clear();
                            selectedSlot = null;
                        }
                        updateDisplay();
                        updateBtn(stack);
                    } else {
                        installBtn.enableIf(false);
                        uninstallBtn.enableIf(false);
                    }
                }
            } else {
                onClose();
            }
        } else {
            onClose();
        }
    }

    private void updateBtn(ItemStack gunStack) {
        AttachmentSlot slot = context.getSelected();
        if (slot != null) {
            installBtn.enableIf(slot.isEmpty() && selectedSlot != null);
            uninstallBtn.enableIf(!installBtn.active && !slot.isEmpty());
            installBtn.setCurrentTexture(slot.getReplaceableGunPart() != null ?
                    REPLACE_GUN_PART_BTN : INSTALL_ATTACHMENT_BTN);
            installBtn.setTooltip(slot.getReplaceableGunPart() == null ?
                    Tooltip.create(Component.translatable("tooltip.btn.install_attachment")) :
                    Tooltip.create(Component.translatable("tooltip.btn.replace_gun_part")));
            if (installBtn.active) {
                installAttachment(false);
            } else if (uninstallBtn.active) {
                uninstallAttachment(false);
            }
            return;
        }
        installBtn.enableIf(false);
        uninstallBtn.enableIf(false);

        ItemStack itemStack = menu.ammoSelector.getItem(0);
        if (itemStack.getItem() instanceof IAmmunition ammunition) {
            boolean isAmmunitionBind = gun.getGun().isAmmunitionBind(gunStack);
            boolean sameAmmunition = ammunition == gun.getGunProperties().caliber.ammunition;
            if (sameAmmunition) {
                if (!isAmmunitionBind) {
                    ammoSelectBtn.setPrevented(false);
                    ammoSelectBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.ammo_select")));
                } else {
                    String selectedAmmunitionTypeID = gun.getSelectedAmmunitionTypeID(gunStack);
                    if (!Objects.equals(selectedAmmunitionTypeID, ammunition.getModsUUID(itemStack))) {
                        ammoSelectBtn.setPrevented(false);
                        ammoSelectBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.ammo_select")));
                    } else {
                        ammoSelectBtn.setPrevented(true);
                        ammoSelectBtn.setPreventedTooltipStr(Component.translatable("tooltip.btn.same_type_ammo").getString());
                    }
                }
            } else {
                ammoSelectBtn.setPrevented(true);
                ammoSelectBtn.setPreventedTooltipStr(Component.translatable("tooltip.btn.ammo_type_error").getString());
            }
        } else {
            ammoSelectBtn.setPrevented(true);
            ammoSelectBtn.setPreventedTooltipStr(Component.translatable("tooltip.btn.need_put_ammo").getString());
        }
    }

    private void updateSuitableSlots(AttachmentSlot selected) {
        Set<String> accepts = selected.getAcceptedAttachments();
        SimpleContainer display = menu.displaySuitableAttachments;
        if (!accepts.isEmpty()) {
            display.clearContent();
            Set<IAttachment> attachments = new HashSet<>();
            for (String key : accepts) {
                IAttachment attachment = AttachmentsRegister.get(key);
                if (attachment != null) {
                    display.addItem(new ItemStack(attachment.get()));
                    attachments.add(attachment);
                }
            }
            Item item = selectedSlot == null ? null : selectedSlot.getItem().getItem();
            if (item instanceof IAttachment attachment) {
                selectedSlot = attachments.contains(attachment) ? selectedSlot : null;
            } else {
                selectedSlot = null;
            }
            suitableSlots.clear();
            for (int i = 0; i < menu.slots.size(); i++) {
                Slot slot = menu.getSlot(i);
                if (!(slot instanceof GunModifyMenu.DisplaySlot) &&
                        slot.getItem().getItem() instanceof IAttachment attachment && attachments.contains(attachment)) {
                    suitableSlots.add(slot);
                }
            }
        } else {
            suitableSlots.clear();
            selectedSlot = null;
            display.clearContent();
        }
    }

    private void updateDisplay() {
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);
            if (slot instanceof GunModifyMenu.DisplaySlot displaySlot) {
                displaySlot.active = displaySlot.hasItem();
            }
        }
    }

    @Override
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        return super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }

    @Override
    protected void slotClicked(@NotNull Slot pSlot, int pSlotId, int pMouseButton, @NotNull ClickType pType) {
        if (pSlot instanceof GunModifyMenu.DisplaySlot) {
            return;
        }
        if (pSlot == selectedSlot) {
            selectedSlot = null;
            return;
        } else {
            if (suitableSlots.contains(pSlot)) {
                selectedSlot = pSlot;
                return;
            } else {
                selectedSlot = null;
            }
        }
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (context != null) {
            context.renderIcons(pGuiGraphics, this.font);
        }
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        float alphaTick = (System.currentTimeMillis() % 1000) * 0.001f;
        renderSuitableSlotMark(pGuiGraphics, alphaTick);
        renderSelectedSlotMark(pGuiGraphics);
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        Player player = Minecraft.getInstance().player;
        if (player != null && !player.isCreative() && !player.isSpectator()) {
           String str = Component.translatable("label.attachments_screen.health").getString()
                   + "" + Math.floor(player.getHealth())+ "/" + Math.floor(player.getMaxHealth());
            pGuiGraphics.drawString(font, str, (int) (width * 0.75), (int) (height * 0.1), -1);
        }
        if (needUpdate) {
            this.renderBackground(pGuiGraphics);
            RenderSystem.enableDepthTest();
            String text = Component.translatable("label.attachments_screen.wait_response").getString();
            Font font = Minecraft.getInstance().font;
            pGuiGraphics.drawString(font, text, (width - font.width(text)) / 2, height / 2, -1);
        }
    }

    private void renderSelectedSlotMark(GuiGraphics pGuiGraphics) {
        if (selectedSlot != null) {
            pGuiGraphics.blit(SELECTED_SLOT, this.leftPos + selectedSlot.x - 3, this.topPos + selectedSlot.y - 3, 0,0, 22,22, 22, 22);
        }
    }

    private void renderSuitableSlotMark(GuiGraphics pGuiGraphics, float alphaTick) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 0.25f + alphaTick);
        for (Slot slot : suitableSlots) {
            pGuiGraphics.blit(SUITABLE_SLOT_MARK, this.leftPos + slot.x, this.topPos + slot.y, 0,0, 16,16, 16, 16);
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            RenderSystem.enableBlend();
            ResourceLocation background = chooseBackground();
            pGuiGraphics.blit(background, startX, startY,  0,0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());
            if (background == INVENTORY_SHOW_SUITABLE) {
                pGuiGraphics.drawString(this.minecraft.font, Component.translatable("label.attachments_screen.suitable"), this.leftPos + 278, this.topPos + 150, 0xffffff);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (context != null) {
            if (!context.onClick((int) pMouseX, (int) pMouseY)) {
                if (!isDraggingModel && isMouseInModelArea(pMouseX, pMouseY)) {
                    isRollingModel = true;
                }
                dragStartX = (float) pMouseX;
                dragStartY = (float) pMouseY;
            } else {
                installBtn.reset();
                uninstallBtn.reset();
            }
        }
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
        return !(mx >= this.leftPos + 100 && mx <= this.leftPos + 276 && my >= this.topPos + 160 && my <= this.topPos + 241);
    }

    private ResourceLocation chooseBackground() {
        return menu.displaySuitableAttachments.isEmpty() ? INVENTORY_CLEAR : INVENTORY_SHOW_SUITABLE;
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

    private class RenderModeBtn extends ImageButton {
        int mode;

        public RenderModeBtn(int pX, int pY, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
            super(pX, pY, pWidth, pHeight, 0, 0, 0, new ResourceLocation(""), pTextureWidth, pTextureHeight, (btn) -> ((RenderModeBtn) btn).onClick());
            mode = AttachmentsGuiContext.getRenderMode();
            updateTooltip();
        }

        private void updateTooltip() {
            switch (mode) {
                case (AttachmentsGuiContext.RENDER_ALL) -> setTooltip(Tooltip.create(Component.translatable("tooltip.attachment_icon_render_mode.render_all")));
                case (AttachmentsGuiContext.RENDER_CHILDREN) -> setTooltip(Tooltip.create(Component.translatable("tooltip.attachment_icon_render_mode.render_children")));
                case (AttachmentsGuiContext.RENDER_EMPTY) -> setTooltip(Tooltip.create(Component.translatable("tooltip.attachment_icon_render_mode.render_empty")));
                case (AttachmentsGuiContext.RENDER_OCCUPIED) -> setTooltip(Tooltip.create(Component.translatable("tooltip.attachment_icon_render_mode.render_occupied")));
            }
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            this.renderTexture(pGuiGraphics, RENDER_MODE, this.getX(), this.getY(), this.xTexStart + mode * 16, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
        }

        private void onClick() {
            mode = (mode + 1) % 4;
            if (context != null) {
                context.setRenderMode(mode);
            }
            updateTooltip();
        }
    }
}
