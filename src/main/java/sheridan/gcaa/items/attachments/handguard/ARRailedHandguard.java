package sheridan.gcaa.items.attachments.handguard;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.Handguard;

import java.util.Set;

public class ARRailedHandguard extends Handguard {
    private static final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("handguard_grip", Set.of("gcaa:vertical_grip", "gcaa:gp_25")).lower())
            .addChild(new AttachmentSlot("handguard_scope", Set.of("gcaa:red_dot", "gcaa:holographic")).upper())
            .addChild(new AttachmentSlot("handguard_left", Set.of("")).lower())
            .addChild(new AttachmentSlot("handguard_right", Set.of("")).lower());

    public ARRailedHandguard() {
        super(root, 0, 0);
    }
}
