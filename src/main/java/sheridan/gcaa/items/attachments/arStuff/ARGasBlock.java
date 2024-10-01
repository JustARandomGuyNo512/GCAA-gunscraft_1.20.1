package sheridan.gcaa.items.attachments.arStuff;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.IGun;

public class ARGasBlock extends Attachment {
    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {}

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {}
}
