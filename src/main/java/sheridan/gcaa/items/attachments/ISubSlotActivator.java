package sheridan.gcaa.items.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.client.AttachmentSlot;

public interface ISubSlotActivator {
    @OnlyIn(Dist.CLIENT)
    void doUnlock(AttachmentSlot slot);
    @OnlyIn(Dist.CLIENT)
    void doLock(AttachmentSlot slot);
}
