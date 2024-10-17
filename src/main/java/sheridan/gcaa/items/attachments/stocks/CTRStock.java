package sheridan.gcaa.items.attachments.stocks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class CTRStock extends Attachment {

    public CTRStock() {
        super(1.5f);
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + 0.2f);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + 0.2f);
        properties.setPropertyRateIfHas(GunProperties.ADS_SPEED, data, (prevRate) -> prevRate + 0.1f);
        properties.setPropertyRateIfHas(GunProperties.SPREAD_RECOVER, data, (prevRate) -> prevRate + 0.025f);
        properties.setPropertyRateIfHas(GunProperties.SPRINTING_SPREAD_FACTOR, data, (prevRate) -> prevRate - 0.05f);
        properties.setPropertyRateIfHas(GunProperties.AGILITY, data, (prevRate) -> prevRate + 0.05f);
        properties.addWeight(data, weight);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - 0.2f);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - 0.2f);
        properties.setPropertyRateIfHas(GunProperties.ADS_SPEED, data, (prevRate) -> prevRate - 0.1f);
        properties.setPropertyRateIfHas(GunProperties.SPREAD_RECOVER, data, (prevRate) -> prevRate - 0.025f);
        properties.setPropertyRateIfHas(GunProperties.SPRINTING_SPREAD_FACTOR, data, (prevRate) -> prevRate + 0.05f);
        properties.setPropertyRateIfHas(GunProperties.AGILITY, data, (prevRate) -> prevRate - 0.05f);
        properties.addWeight(data, - weight);
    }
}