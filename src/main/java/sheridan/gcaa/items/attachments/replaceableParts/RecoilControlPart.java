package sheridan.gcaa.items.attachments.replaceableParts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.ReplaceableGunPart;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class RecoilControlPart extends ReplaceableGunPart {
    private final float vertical;
    private final float horizontal;

    public RecoilControlPart(float weight, float vertical, float horizontal)  {
        super(weight);
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    @Override
    public void onOccupied(ItemStack stack, IGun gun, CompoundTag data) {
        super.onOccupied(stack, gun, data);
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (p) -> p - vertical);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (p) -> p - horizontal);
    }

    @Override
    public void onEmpty(ItemStack stack, IGun gun, CompoundTag data) {
        super.onEmpty(stack, gun, data);
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (p) -> p + vertical);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (p) -> p + horizontal);
    }
}
