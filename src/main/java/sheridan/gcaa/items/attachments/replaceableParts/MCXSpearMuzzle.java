package sheridan.gcaa.items.attachments.replaceableParts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class MCXSpearMuzzle extends RecoilLowerPart{
    public MCXSpearMuzzle() {
        super(0.5f, 0.07f, 0.07f);
    }

    @Override
    public void onEmpty(ItemStack stack, IGun gun, CompoundTag data) {
        super.onEmpty(stack, gun, data);
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_SUPPRESSOR);
        System.out.println("MCXSpearMuzzle onEmpty");
    }
}
