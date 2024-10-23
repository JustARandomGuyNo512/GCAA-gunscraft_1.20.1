package sheridan.gcaa.items.attachments.akStuff;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Set;

public class AKRailBracket extends SubSlotProvider {
    private final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("rail_bracket_scope", Set.of("gcaa:red_dot", "gcaa:holographic", "gcaa:acog")));

    public AKRailBracket() {
        super(0.2f);
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {}

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {}

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root) {
        parent.addChild(root.getChild("rail_bracket_scope").copy());
    }
}
