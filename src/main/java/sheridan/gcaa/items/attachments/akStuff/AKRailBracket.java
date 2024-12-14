package sheridan.gcaa.items.attachments.akStuff;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotProvider;

import java.util.Set;

public class AKRailBracket extends SubSlotProvider {
    private final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("rail_bracket_scope", Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog", "gcaa:okp7_b")));

    public AKRailBracket() {
        super(0.2f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root) {
        parent.addChild(this.root.getChild("rail_bracket_scope").copy());
    }
}
