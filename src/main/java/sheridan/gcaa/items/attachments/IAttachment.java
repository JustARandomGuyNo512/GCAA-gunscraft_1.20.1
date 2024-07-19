package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.client.AttachmentSlot;
import sheridan.gcaa.items.gun.IGun;

public interface IAttachment {
    @OnlyIn(Dist.CLIENT)
    String canAttach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);

    void onAttach(ItemStack stack, IGun gun, CompoundTag data);

    @OnlyIn(Dist.CLIENT)
    String canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot);

    void onDetach(ItemStack stack, IGun gun, CompoundTag data);

    Attachment get();
}
