package sheridan.gcaa.items.attachments;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

public interface ISubSlotProvider {
    void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun);
}
