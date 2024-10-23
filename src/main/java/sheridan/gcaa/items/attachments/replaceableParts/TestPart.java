package sheridan.gcaa.items.attachments.replaceableParts;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.ReplaceableGunPart;
import sheridan.gcaa.items.gun.IGun;

public class TestPart extends ReplaceableGunPart {
    public TestPart() {
        super(1);
    }

    @Override
    public void doSlotOperation(IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {

    }
}
