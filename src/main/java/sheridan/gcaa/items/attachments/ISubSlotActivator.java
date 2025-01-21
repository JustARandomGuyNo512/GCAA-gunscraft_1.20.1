package sheridan.gcaa.items.attachments;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

public interface ISubSlotActivator {
    void unlockOrLockSlots(AttachmentSlot slot, AttachmentSlot root, IGun gun);
}
