package sheridan.gcaa.attachmentSys.proxies;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.antlr.v4.misc.Utils;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

public class AkmAttachmentSlotProxy extends AttachmentSlotProxy {
    private final AttachmentSlot rail_set;
    private final AttachmentSlot dust_cover;

    public AkmAttachmentSlotProxy(AttachmentSlot root) {
        super(root);
        rail_set = root.searchChild("rail_set");
        dust_cover = root.searchChild("dust_cover");
    }

    @Override
    public IAttachment.AttachResult onAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        if (prevSlot == rail_set) {
            return dust_cover.isEmpty() ? attachment.canAttach(stack, gun, root, prevSlot) :
                    new IAttachment.AttachResult(false, getMessage(dust_cover.getAttachmentId()));
        }
        if (prevSlot == dust_cover) {
            return rail_set.isEmpty() ? attachment.canAttach(stack, gun, root, prevSlot) :
                    new IAttachment.AttachResult(false, getMessage(rail_set.getAttachmentId()));
        }
        return attachment.canAttach(stack, gun, root, prevSlot);
    }

    @Override
    public IAttachment.AttachResult onDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
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
