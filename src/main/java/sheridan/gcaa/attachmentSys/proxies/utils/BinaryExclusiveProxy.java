package sheridan.gcaa.attachmentSys.proxies.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

import java.util.ArrayList;
import java.util.List;

public class BinaryExclusiveProxy extends AttachmentSlotProxy {
    private static final Exclusive DEFAULT_EXCLUSIVE = (prevSlot, other, prevAttachment, otherAttachment) -> true;
    private final AttachmentSlot A;
    private final AttachmentSlot B;
    private final List<Exclusive> exclusives = new ArrayList<>();

    public BinaryExclusiveProxy(AttachmentSlot root, String AName, String BName) {
        super(root);
        A = root.searchChild(AName);
        B = root.searchChild(BName);
    }

    public BinaryExclusiveProxy addExclusive(Exclusive exclusive) {
        this.exclusives.add(exclusive);
        return this;
    }

    public BinaryExclusiveProxy addDefaultExclusive() {
        this.exclusives.add(DEFAULT_EXCLUSIVE);
        return this;
    }

    @Override
    public IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        if (prevSlot == A) {
            return B.isEmpty() ? attachment.canAttach(stack, gun, root, prevSlot) :
                    handleExclusive(prevSlot, B, attachment, AttachmentsRegister.get(B.getAttachmentId())) ?
                            new IAttachment.AttachResult(false, getMessage(B.getAttachmentId())) :
                            attachment.canAttach(stack, gun, root, prevSlot);
        }
        if (prevSlot == B) {
            return A.isEmpty() ? attachment.canAttach(stack, gun, root, prevSlot) :
                    handleExclusive(prevSlot, A, attachment, AttachmentsRegister.get(A.getAttachmentId())) ?
                            new IAttachment.AttachResult(false, getMessage(A.getAttachmentId())):
                            attachment.canAttach(stack, gun, root, prevSlot);
        }
        return attachment.canAttach(stack, gun, root, prevSlot);
    }

    @Override
    public IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        return attachment.canDetach(stack, gun, root, prevSlot);
    }

    private boolean handleExclusive(AttachmentSlot prevSlot, AttachmentSlot other, IAttachment prevAttachment, IAttachment otherAttachment) {
        for (Exclusive exclusive : exclusives) {
            if (exclusive.isExclusive(prevSlot, other, prevAttachment, otherAttachment)) {
                return true;
            }
        }
        return false;
    }

    public interface Exclusive {
        boolean isExclusive(AttachmentSlot prevSlot, AttachmentSlot other, IAttachment prevAttachment, IAttachment otherAttachment);
    }

    public static IAttachment.MessageGetter getMessage(String id) {
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
