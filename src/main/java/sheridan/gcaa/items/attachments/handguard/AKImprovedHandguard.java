package sheridan.gcaa.items.attachments.handguard;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.Handguard;

import java.util.Set;

public class AKImprovedHandguard extends Handguard {
    private static final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("handguard_grip", Set.of("gcaa:vertical_grip")))
            .addChild(new AttachmentSlot("handguard_sight", Set.of("gcaa:red_dot", "gcaa:holographic")))
            .addChild(new AttachmentSlot("handguard_left", Set.of("")))
            .addChild(new AttachmentSlot("handguard_right", Set.of("")));

    public AKImprovedHandguard() {
        super(root, 0.05f, 0.05f);
    }
}
