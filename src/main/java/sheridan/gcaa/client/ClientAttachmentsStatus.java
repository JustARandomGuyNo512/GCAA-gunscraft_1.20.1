package sheridan.gcaa.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.AttachmentSlot;

@OnlyIn(Dist.CLIENT)
public class ClientAttachmentsStatus {
    public ItemStack itemStack;
    public AttachmentSlot slots;

    public void update(ItemStack stack, Player player) {

    }
}
