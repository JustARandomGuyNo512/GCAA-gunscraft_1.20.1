package sheridan.gcaa.items.attachments.akStuff;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotProvider;

import java.util.Set;

public class AKImprovedDustCover extends SubSlotProvider {

    private final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("dust_cover_scope", Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")).upper());

    public AKImprovedDustCover() {
        super(0);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root) {
        parent.addChild(this.root.getChild("dust_cover_scope").copy());
    }

}
