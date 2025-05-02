package sheridan.gcaa.items.attachments.other;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Set;

public class CantedSightSet extends SubSlotProvider {
    private final AttachmentSlot root = new AttachmentSlot("canted_scope", Set.of(
            "gcaa:red_dot",
            "gcaa:kobra_sight"));

    public CantedSightSet() {
        super(0.1f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun) {
        parent.addChild(this.root.copy());
    }
}

