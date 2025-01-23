package sheridan.gcaa.attachmentSys.proxies;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.attachments.Handguard;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

public class M4a1AttachmentProxy extends AttachmentSlotProxy {
    private final AttachmentSlot gasBlock, handguard;

    public M4a1AttachmentProxy(AttachmentSlot root) {
        super(root);
        gasBlock = root.getChild("gas_block");
        handguard = root.getChild("handguard");
    }

    @Override
    public IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        if (prevSlot == handguard && attachment instanceof Handguard handguard && handguard.isLongHandguard()) {
            if (gasBlock.isEmpty()) {
                return new IAttachment.AttachResult(false, () -> {
                    String message = Component.translatable("tooltip.action_res.require_attach").getString();
                    String[] strings = gasBlock.getAcceptedAttachments().toArray(new String[0]);
                    if (strings.length > 0) {
                        IAttachment attachment1 = AttachmentsRegister.get(strings[0]);
                        if (attachment1 != null) {
                            message = message.replace("$name", Component.translatable(attachment1.get().getDescriptionId()).getString());
                        }
                    }
                    return message;
                });
            }
        }
        return attachment.canAttach(stack, gun, root, prevSlot);
    }

    @Override
    public IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        if (prevSlot == gasBlock && !handguard.isEmpty()) {
            IAttachment attachment1 = AttachmentsRegister.get(handguard.getAttachmentId());
            if (attachment1 instanceof Handguard handguard1 && handguard1.isLongHandguard()) {
                return new IAttachment.AttachResult(false, () -> {
                    String message = Component.translatable("tooltip.action_res.require_detach").getString();
                    message = message.replace("$name", Component.translatable(handguard1.get().getDescriptionId()).getString());
                    return message;
                });
            }
        }
        return attachment.canDetach(stack, gun, root, prevSlot);
    }
}
