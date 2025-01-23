package sheridan.gcaa.attachmentSys.proxies.utils;

import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

public class RequirementProxy extends AttachmentSlotProxy {

    public RequirementProxy(AttachmentSlot root) {
        super(root);
    }

    @Override
    public IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        throw new NotImplementedException();
    }

    @Override
    public IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        throw new NotImplementedException();
    }
}
