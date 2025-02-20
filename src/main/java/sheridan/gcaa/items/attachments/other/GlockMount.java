package sheridan.gcaa.items.attachments.other;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;
import java.util.Set;

public class GlockMount extends SubSlotProvider {
    private final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("mount_grip", Set.of("gcaa:micro_laser_sight", "gcaa:micro_flashlight")).lower())
            .addChild(new AttachmentSlot("mount_side", Set.of("gcaa:micro_laser_sight", "gcaa:micro_flashlight")))
            .addChild(new AttachmentSlot("mount_scope", Set.of("gcaa:red_dot")));

    public GlockMount() {
        super(0.25f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun) {
        for (Map.Entry<String, AttachmentSlot> entry : this.root.getChildren().entrySet()) {
            parent.addChild(AttachmentSlot.copyAll(entry.getValue()));
        }
    }
}
