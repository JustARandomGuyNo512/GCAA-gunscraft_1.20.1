package sheridan.gcaa.items.attachments.handguard;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.proxies.utils.BinaryExclusiveProxy;
import sheridan.gcaa.items.attachments.ForwardSlotBlocker;
import sheridan.gcaa.items.attachments.Handguard;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.handguard.data.ARHandguardSlots;
import sheridan.gcaa.items.gun.IGun;

import java.util.Stack;

public class ARLightHandguard extends Handguard {
    public ARLightHandguard() {
        super(ARHandguardSlots.HANDGUARD_ROOT, 0, 0, 1.1f, true);
    }

    @Override
    public void childTryAttach(ItemStack stack, IGun gun, IAttachment child, AttachmentSlot childSlot, Stack<AttachmentSlot> path, AttachResult prevResult) {
        if (!prevResult.isPassed()) {
            return;
        }
        if ("handguard_grip".equals(childSlot.slotName)) {
            if (child instanceof ForwardSlotBlocker) {
                AttachmentSlot handguard_front = childSlot.getParent().getChild("handguard_front");
                if (handguard_front != null && !handguard_front.isEmpty()) {
                    prevResult.setPassed(false);
                    prevResult.setMessageGetter(BinaryExclusiveProxy.getMessage(handguard_front.getAttachmentId()));
                }
            }
        } else if ("handguard_front".equals(childSlot.slotName)) {
            AttachmentSlot handguard_grip = childSlot.getParent().getChild("handguard_grip");
            if (handguard_grip != null && !handguard_grip.isEmpty()) {
                String attachmentId = handguard_grip.getAttachmentId();
                IAttachment attachment = AttachmentsRegister.get(attachmentId);
                if (attachment instanceof ForwardSlotBlocker) {
                    prevResult.setPassed(false);
                    prevResult.setMessageGetter(BinaryExclusiveProxy.getMessage(attachmentId));
                }
            }
        }
    }
}
