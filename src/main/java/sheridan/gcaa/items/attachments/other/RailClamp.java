package sheridan.gcaa.items.attachments.other;

import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Set;

public class RailClamp extends SubSlotProvider {
    Set<String> common = Set.of(
            "gcaa:red_dot",
            "gcaa:kobra_sight",
            "gcaa:holographic",
            "gcaa:acog",
            "gcaa:okp7_b");
    Set<String> rifle = Set.of(
            "gcaa:red_dot",
            "gcaa:kobra_sight",
            "gcaa:holographic",
            "gcaa:acog",
            "gcaa:okp7_b",
            "gcaa:scope_x10"
    );
    private final AttachmentSlot commonSlot = new AttachmentSlot("rail_clamp_scope", common);
    private final AttachmentSlot rifleSlot = new AttachmentSlot("rail_clamp_scope", rifle);
    public RailClamp() {
        super(0.35f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun) {
        if (gun.getGunType() == IGun.GunType.RIFLE) {
            parent.addChild(rifleSlot.copy());
        } else if (gun.getGunType() == IGun.GunType.SMG) {
            parent.addChild(commonSlot.copy());
        } else if (gun.getGun() == ModItems.BERETTA_686.get()) {
            parent.addChild(commonSlot.copy());
        }
    }
}
