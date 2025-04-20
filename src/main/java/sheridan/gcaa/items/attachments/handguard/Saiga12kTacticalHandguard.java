package sheridan.gcaa.items.attachments.handguard;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotActivator;

import java.util.List;

public class Saiga12kTacticalHandguard  extends SubSlotActivator {
    public Saiga12kTacticalHandguard() {
        super(1.2f);
    }

    public List<AttachmentSlot> getLinkedSlots(AttachmentSlot root) {
        return List.of(
                root.getChild("handguard_grip"),
                root.getChild("handguard_left"),
                root.getChild("handguard_right"),
                root.getChild("handguard_scope"));
    }
}
