package sheridan.gcaa.items.attachments.handguard;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotActivator;
import java.util.List;


public class Mp5RailHandguard extends SubSlotActivator {
    public Mp5RailHandguard() {
        super(0.8f);
    }

    public List<AttachmentSlot> getLinkedSlots(AttachmentSlot root) {
        return List.of(root.getChild("handguard_grip"), root.getChild("handguard_right"), root.getChild("handguard_left"));
    }
}
