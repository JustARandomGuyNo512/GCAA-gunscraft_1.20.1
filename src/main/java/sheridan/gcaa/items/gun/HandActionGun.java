package sheridan.gcaa.items.gun;

import net.minecraft.world.item.ItemStack;

public class HandActionGun extends Gun{

    public HandActionGun(HandActionGunProperties gunProperties) {
        super(gunProperties);
    }

    public boolean needHandAction(ItemStack itemStack) {
        return false;
    }
}
