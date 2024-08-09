package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.client.events.RenderEvents;
import sheridan.gcaa.client.screens.componets.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.AttachmentsMenu;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.InstallAttachmentsPacket;
import sheridan.gcaa.network.packets.c2s.UninstallAttachmentPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AttachmentsScreen extends AbstractContainerScreen<AttachmentsMenu> {
    private static final ResourceLocation INVENTORY_CLEAR = new ResourceLocation(GCAA.MODID, "textures/gui/screen/inventory_clear.png");
    private static final ResourceLocation INVENTORY_SHOW_SUITABLE = new ResourceLocation(GCAA.MODID, "textures/gui/screen/inventory_show_suitable.png");
    private static final ResourceLocation DRAG_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/drag_btn.png");
    private static final ResourceLocation RESET_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/reset_btn.png");
    private static final ResourceLocation INSTALL_ATTACHMENT_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/install_attachment_btn.png");
    private static final ResourceLocation UNINSTALL_ATTACHMENT_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/uninstall_attachment_btn.png");
    private static final ResourceLocation SELECTED_SLOT = new ResourceLocation(GCAA.MODID, "textures/gui/component/selected_slot.png");
    private static final ResourceLocation SUITABLE_SLOT_MARK = new ResourceLocation(GCAA.MODID, "textures/gui/component/suitable_slot_mark.png");

    private AttachmentsGuiContext context;
    private final AttachmentsMenu menu;
    private OptionalImageButton installBtn;
    private OptionalImageButton uninstallBtn;
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

    public AttachmentsScreen(AttachmentsMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
        ImageButton resetBtn = new ImageButton(this.leftPos + 244, 20, 16, 16, 0, 0, 0, RESET_BTN, 16, 16,  (btn) -> resetModel());
        resetBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.reset")));
        rowHelper.addChild(resetBtn);
        ImageButton dragBtn = new ImageButton(this.leftPos + 244, 40, 16, 16, 0, 0, 0, DRAG_BTN, 16, 16,  (btn) -> isDraggingModel = true);
        dragBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.drag")));
        rowHelper.addChild(dragBtn);
        installBtn = new OptionalImageButton(this.leftPos + 180, 144, 16, 16, 0, 0, 0, INSTALL_ATTACHMENT_BTN, 16, 16,  (btn) -> installAttachment(true));
        uninstallBtn = new OptionalImageButton(this.leftPos + 180, 144, 16, 16, 0, 0, 0, UNINSTALL_ATTACHMENT_BTN, 16, 16,  (btn) -> uninstallAttachment(true));
        installBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.install_attachment")));
        uninstallBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.uninstall_attachment")));
        installBtn.enableIf(false);
        uninstallBtn.enableIf(false);
        rowHelper.addChild(installBtn);
        rowHelper.addChild(uninstallBtn);
        gridlayout.visitWidgets(this::addRenderableWidget);
        if (this.minecraft != null && this.minecraft.player != null) {
            Player player = this.minecraft.player;
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun) {
                AttachmentSlot slot = AttachmentsHandler.INSTANCE.getAttachmentSlots(stack);
                this.context = new AttachmentsGuiContext(slot);
            }
        }
    }

    private void installAttachment(boolean sendPacket) {
        if (context != null && selectedSlot != null && hasPlayer()) {
            AttachmentSlot slot = context.getSelected();
            if (slot != null && selectedSlot.getItem().getItem() instanceof IAttachment attachment) {
                ItemStack stack = this.minecraft.player.getMainHandItem();
                if (stack.getItem() instanceof IGun gun) {
                    String attachRes = attachment.canAttach(stack, gun, context.getRoot(), slot);
                    if (Attachment.PASSED.equals(attachRes)) {
                        if (sendPacket) {
                            String attachmentName = AttachmentsRegister.getStrKey(attachment);
                            PacketHandler.simpleChannel.sendToServer(new InstallAttachmentsPacket(
                                    attachmentName,
                                    slot.slotName,
                                    slot.modelSlotName,
                                    slot.getParent().getId(),
                                    selectedSlot.index
                            ));
                            needUpdate = true;
                        }
                    } else {
                        installBtn.setPrevented(true);
                        installBtn.setPreventedTooltip(attachRes);
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
                    String uninstallRes = attachment.canDetach(stack, gun, context.getRoot(), slot);
                    if (Attachment.PASSED.equals(uninstallRes)) {
                        if (sendPacket) {
                            PacketHandler.simpleChannel.sendToServer(new UninstallAttachmentPacket(slot.getId()));
                            needUpdate = true;
                        }
                    } else {
                        uninstallBtn.setPrevented(true);
                        uninstallBtn.setPreventedTooltip(uninstallRes);
                    }
                }
            }
        }
    }

    private boolean hasPlayer() {
        return this.minecraft != null && this.minecraft.player != null;
    }

    public void updateGuiContext(ListTag attachmentsTag, IGun gun) {
        this.context = new AttachmentsGuiContext(AttachmentsHandler.INSTANCE.getAttachmentSlots(attachmentsTag, gun));
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
                        this.context = new AttachmentsGuiContext(slot);
                        this.gun = gun;
                    }
                    if (context != null) {
                        AttachmentSlot selected = context.getSelected();
                        if (selected != null) {
                            updateSuitableSlots(selected);
                        } else {
                            menu.displaySuitableAttachments.clearContent();
                            suitableSlots.clear();
                        }
                        updateDisplay();
                        updateBtn();
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

    private void updateBtn() {
        AttachmentSlot slot = context.getSelected();
        if (slot != null) {
            installBtn.enableIf(slot.isEmpty() && selectedSlot != null);
            uninstallBtn.enableIf(!installBtn.active && !slot.isEmpty());
            if (installBtn.active) {
                installAttachment(false);
            } else if (uninstallBtn.active) {
                uninstallAttachment(false);
            }
            return;
        }
        installBtn.enableIf(false);
        uninstallBtn.enableIf(false);
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
            suitableSlots.clear();
            for (int i = 0; i < menu.slots.size(); i++) {
                Slot slot = menu.getSlot(i);
                if (!(slot instanceof AttachmentsMenu.DisplaySlot) &&
                        slot.getItem().getItem() instanceof IAttachment attachment && attachments.contains(attachment)) {
                    suitableSlots.add(slot);
                }
            }
        } else {
            suitableSlots.clear();
            display.clearContent();
        }
    }

    private void updateDisplay() {
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);
            if (slot instanceof AttachmentsMenu.DisplaySlot displaySlot) {
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
        if (pSlot instanceof AttachmentsMenu.DisplaySlot) {
            return;
        }
        if (pSlot == selectedSlot) {
            selectedSlot = null;
            return;
        } else {
            if (suitableSlots.contains(pSlot)) {
                selectedSlot = pSlot;
                return;
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
            context.renderIcons(pGuiGraphics);
        }
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        float alphaTick = (System.currentTimeMillis() % 1000) * 0.001f;
        renderSuitableSlotMark(pGuiGraphics, alphaTick);
        renderSelectedSlotMark(pGuiGraphics);
        if (needUpdate) {
            this.renderBackground(pGuiGraphics);
            RenderSystem.enableDepthTest();
            String text = Component.translatable("label.attachments_screen.wait_response").getString();
            Font font = Minecraft.getInstance().font;
            pGuiGraphics.drawString(font, text,
                    (Minecraft.getInstance().getWindow().getGuiScaledWidth() - font.width(text)) / 2,
                    Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2, -1);
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
}
