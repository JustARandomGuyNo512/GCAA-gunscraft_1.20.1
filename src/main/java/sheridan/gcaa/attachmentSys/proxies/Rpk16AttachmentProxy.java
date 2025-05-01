package sheridan.gcaa.attachmentSys.proxies;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.AttachmentSlotProxy;
import sheridan.gcaa.attachmentSys.proxies.utils.BinaryExclusiveProxy;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

public class Rpk16AttachmentProxy extends AttachmentSlotProxy {
    private final GrenadeExclusiveProxy grenadeExclusiveProxy;
    private final BinaryExclusiveProxy binaryExclusiveProxy;
    //private final AttachmentSlot stock;
    //private final AttachmentSlot stock_tube;

    public Rpk16AttachmentProxy(AttachmentSlot root) {
        super(root);
        grenadeExclusiveProxy = new GrenadeExclusiveProxy(root, "handguard_grip", "handguard_front");
        binaryExclusiveProxy = new BinaryExclusiveProxy(root, Attachment.STOCK, "stock_tube");
        binaryExclusiveProxy.addExclusive((prevSlot, other, prevAttachment, otherAttachment) ->
                !prevSlot.isEmpty() || !other.isEmpty());
        //stock = root.getChild(Attachment.STOCK);
        //stock_tube = root.getChild("stock_tube");
        //stock.setLocked(!stock_tube.isEmpty());
        //stock_tube.setLocked(!stock.isEmpty());
    }

    @Override
    public IAttachment.AttachResult onCanAttach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        IAttachment.AttachResult result = grenadeExclusiveProxy.onCanAttach(attachment, stack, gun, root, prevSlot);
        if (result.isPassed()) {
            return binaryExclusiveProxy.onCanAttach(attachment, stack, gun, root, prevSlot);
        }
        //stock.setLocked(!stock_tube.isEmpty());
        //stock_tube.setLocked(!stock.isEmpty());
        return result;
    }

    @Override
    public IAttachment.AttachResult onCanDetach(IAttachment attachment, ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        IAttachment.AttachResult result = grenadeExclusiveProxy.onCanDetach(attachment, stack, gun, root, prevSlot);
        if (result.isPassed()) {
            return binaryExclusiveProxy.onCanDetach(attachment, stack, gun, root, prevSlot);
        }
        //stock.setLocked(!stock_tube.isEmpty());
        //stock_tube.setLocked(!stock.isEmpty());
        return result;
    }
}
