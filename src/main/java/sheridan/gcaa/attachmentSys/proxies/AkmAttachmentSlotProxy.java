package sheridan.gcaa.attachmentSys.proxies;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.proxies.utils.BinaryMutuallyExclusiveProxy;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

public class AkmAttachmentSlotProxy extends AttachmentSlotProxy {
    private final BinaryMutuallyExclusiveProxy binaryMutuallyExclusiveProxy;

    public AkmAttachmentSlotProxy(AttachmentSlot root) {
        super(root);
        binaryMutuallyExclusiveProxy = new BinaryMutuallyExclusiveProxy(root, "rail_set", "dust_cover");
    }

    @Override
    public IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return binaryMutuallyExclusiveProxy.onCanAttach(attachment, stack, gun, root, prevSlot);
    }

    @Override
    public IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return attachment.canDetach(stack, gun, root, prevSlot);
    }
}
