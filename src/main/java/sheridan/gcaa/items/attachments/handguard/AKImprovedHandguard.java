package sheridan.gcaa.items.attachments.handguard;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.Handguard;

import java.util.Set;

public class AKImprovedHandguard extends Handguard {
    private static final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("handguard_grip", Set.of("gcaa:vertical_grip", "gcaa:gp_25", "gcaa:laser_sight", "gcaa:flashlight", "gcaa:slant_grip")).lower())
            .addChild(new AttachmentSlot("handguard_sight", Set.of("gcaa:red_dot", "gcaa:kobra_sight", "gcaa:canted_sight_set", "gcaa:holographic", "gcaa:horizontal_laser_sight", "gcaa:okp7_b")).upper())
            .addChild(new AttachmentSlot("handguard_left", Set.of("gcaa:micro_laser_sight", "gcaa:micro_flashlight")).lower())
            .addChild(new AttachmentSlot("handguard_right", Set.of("gcaa:micro_laser_sight", "gcaa:micro_flashlight")).lower());

    public AKImprovedHandguard() {
        super(root, 0.05f, 0.05f, 0.9f);
    }
}
