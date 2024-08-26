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

public class AKImprovedDustCover extends SubSlotProvider {

    private final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("dust_cover_scope", Set.of("gcaa:red_dot", "gcaa:holographic")));

    public AKImprovedDustCover() {

    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {

    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {

    }

    @Override
    public String canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        String res = super.canAttach(stack, gun, root, prevSlot);
        return PASSED.equals(res) ? checkConflict(res, root, root.searchChild("rail_set"), Set.of()) : res;
    }

    @Override
    public void appendSlots(AttachmentSlot parent) {
        parent.addChild(root.getChild("dust_cover_scope").copy());
    }
}