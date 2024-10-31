package sheridan.gcaa.items.attachments.arStuff;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.attachments.SubSlotProvider;
import sheridan.gcaa.items.gun.IGun;

import java.util.Set;

public class ARStockTube extends SubSlotProvider {
    private final AttachmentSlot root = AttachmentSlot.root()
            .addChild(new AttachmentSlot("stock_tube", Set.of("gcaa:ctr_stock")));

    public ARStockTube() {
        super(0.75f);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root) {
        parent.addChild(this.root.getChild("stock_tube").copy());
    }
}
