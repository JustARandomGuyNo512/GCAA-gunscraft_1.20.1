package sheridan.gcaa.items.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.AttachmentSlot;

public interface ISubSlotProvider {
    void appendSlots(AttachmentSlot parent);
}
