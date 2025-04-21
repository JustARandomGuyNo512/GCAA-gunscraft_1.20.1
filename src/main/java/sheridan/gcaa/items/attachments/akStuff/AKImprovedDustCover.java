package sheridan.gcaa.items.attachments.akStuff;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Set;

public class AKImprovedDustCover extends SubSlotProvider {

    private final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("dust_cover_scope", Set.of("gcaa:red_dot", "gcaa:kobra_sight", "gcaa:holographic","gcaa:elcan", "gcaa:acog", "gcaa:okp7_b")).upper());

    public AKImprovedDustCover() {
        super(0.4f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun) {
        parent.addChild(this.root.getChild("dust_cover_scope").copy());
    }

}
