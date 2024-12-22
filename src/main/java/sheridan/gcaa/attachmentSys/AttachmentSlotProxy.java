package sheridan.gcaa.attachmentSys;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.client.screens.AttachmentsGuiContext;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

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
    public void preSlotIconRender(AttachmentSlot slot, Vector3f pos, GuiGraphics guiGraphics, Font font, AttachmentsGuiContext guiContext) {

    }
}
