package sheridan.gcaa.items.attachments;

import sheridan.gcaa.attachmentSys.AttachmentSlot;

public interface ISubSlotActivator {
    void unlockOrLockSlots(AttachmentSlot slot, AttachmentSlot root);
}
