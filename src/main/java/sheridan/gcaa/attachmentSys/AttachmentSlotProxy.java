package sheridan.gcaa.attachmentSys;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.screens.AttachmentsGuiContext;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.ReplaceableGunPart;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public abstract class AttachmentSlotProxy {
    public final AttachmentSlot root;
    public AttachmentSlotProxy(AttachmentSlot root) {
        this.root = root;
    }

    public abstract IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);
    public abstract IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);

    public static AttachmentSlotProxy getEmptyProxy(AttachmentSlot root) {
        return new AttachmentSlotProxy(root) {
            @Override
            public IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
                return attachment.canAttach(stack, gun, root, prevSlot);
            }

            @Override
            public IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
                return attachment.canDetach(stack, gun, root, prevSlot);
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    public void afterSlotIconRender(AttachmentSlot slot, Vector3f pos, GuiGraphics guiGraphics, Font font, AttachmentsGuiContext guiContext) {
        if (slot == guiContext.getSelected() && AttachmentsGuiContext.showInfoTip) {
            if (!slot.isEmpty()) {
                String attachmentId = slot.getAttachmentId();
                IAttachment attachment = AttachmentsRegister.get(attachmentId);
                if (attachment !=  null) {
                    List<Component> effectsInGunModifyScreen = attachment.getEffectsInGunModifyScreen();
                    drawTooltips(effectsInGunModifyScreen, guiGraphics, font, (int) pos.x, (int) pos.y);
                }
            } else {
                ReplaceableGunPart replaceableGunPart = slot.getReplaceableGunPart();
                if (replaceableGunPart != null) {
                    List<Component> effectsInGunModifyScreen = replaceableGunPart.getEffectsInGunModifyScreen();
                    drawTooltips(effectsInGunModifyScreen, guiGraphics, font, (int) pos.x, (int) pos.y);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawTooltips(List<Component> tooltips, GuiGraphics guiGraphics, Font font, int x, int y) {
        if (tooltips != null && !tooltips.isEmpty()) {
            guiGraphics.setColor(1, 1, 1, 0.75f);
            guiGraphics.renderComponentTooltip(font, tooltips, x, y + font.lineHeight * Math.max(tooltips.size() / 2, 1) + 3);
            guiGraphics.setColor(1, 1, 1, 1);
        }
    }
}
