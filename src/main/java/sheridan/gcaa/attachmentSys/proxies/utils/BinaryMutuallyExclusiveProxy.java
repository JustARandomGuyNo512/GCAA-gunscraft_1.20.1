package sheridan.gcaa.attachmentSys.proxies.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

public class BinaryMutuallyExclusiveProxy extends AttachmentSlotProxy {
    private final AttachmentSlot A;
    private final AttachmentSlot B;

    public BinaryMutuallyExclusiveProxy(AttachmentSlot root, String AName, String BName) {
        super(root);
        A = root.searchChild(AName);
        B = root.searchChild(BName);
    }

    @Override
    public IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        if (prevSlot == A) {
            return B.isEmpty() ? attachment.canAttach(stack, gun, root, prevSlot) :
                    new IAttachment.AttachResult(false, getMessage(B.getAttachmentId()));
        }
        if (prevSlot == B) {
            return A.isEmpty() ? attachment.canAttach(stack, gun, root, prevSlot) :
                    new IAttachment.AttachResult(false, getMessage(A.getAttachmentId()));
        }
        return attachment.canAttach(stack, gun, root, prevSlot);
    }

    @Override
    public IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return attachment.canDetach(stack, gun, root, prevSlot);
    }

    private IAttachment.MessageGetter getMessage(String id) {
        return () -> {
            String message = Component.translatable("tooltip.action_res.conflict").getString();
            IAttachment attachment = AttachmentsRegister.get(id);
            if (attachment != null) {
                message = message.replace("$id", Component.translatable(attachment.get().getDescriptionId()).getString());
            } else {
                message = message.replace("$id", "-UNKNOWN-");
            }
            return message;
        };
    }
}
