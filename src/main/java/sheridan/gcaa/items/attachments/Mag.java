package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.Map;

public class Mag extends Attachment{
    protected Map<IGun, Integer> specialCapacity = new HashMap<>();
    int capacity;

    public Mag(int capacity, float weight) {
        super(weight);
        this.capacity = Math.max(capacity, 0);
    }

    protected void addSpecialCapacityFor(IGun gun, int capacity) {
        specialCapacity.put(gun, capacity);
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        super.onAttach(stack, gun, data);
        if (specialCapacity.containsKey(gun)) {
            properties.setMagSize(data, specialCapacity.get(gun));
        } else {
            properties.setMagSize(data, capacity);
        }
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        gun.getGunProperties().resetMagSize(data);
        super.onDetach(stack, gun, data);
        int ammoLeft = gun.getAmmoLeft(stack);
        gun.setAmmoLeft(stack, Math.min(ammoLeft, gun.getMagSize(stack)));
    }
}
