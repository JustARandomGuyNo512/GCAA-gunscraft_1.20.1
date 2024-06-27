package sheridan.gcaa.items.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.client.AttachmentSlot;

public interface ISubSlotProvider {
    @OnlyIn(Dist.CLIENT)
    AttachmentSlot getSubSlot();
}
