package sheridan.gcaa.items.attachments;

import sheridan.gcaa.attachmentSys.AttachmentSlot;

public interface ISubSlotActivator {
    void unlockSlots(AttachmentSlot slot);
    void lockSlots(AttachmentSlot slot);
}
