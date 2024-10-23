package sheridan.gcaa.items.attachments;

import sheridan.gcaa.attachmentSys.AttachmentSlot;

public interface ISubSlotProvider {
    void appendSlots(AttachmentSlot parent, AttachmentSlot root);
}
