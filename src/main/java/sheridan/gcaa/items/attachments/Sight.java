package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.IGun;

public abstract class Sight extends Attachment{

    public Sight(float weight) {
        super(weight);
    }
    
    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        gun.getGunProperties().addWeight(data, weight);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        gun.getGunProperties().addWeight(data, - weight);
    }
}
