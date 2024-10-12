package sheridan.gcaa.attachmentSys;

import net.minecraft.world.item.ItemStack;
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
}
