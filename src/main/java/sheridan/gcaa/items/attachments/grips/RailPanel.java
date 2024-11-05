package sheridan.gcaa.items.attachments.grips;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class RailPanel extends Attachment {
    public RailPanel() {
        super(0.12f);
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        super.onAttach(player, stack, gun, data);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (p) -> p + 0.05f);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (p) -> p + 0.05f);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        super.onDetach(player, stack, gun, data);
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (p) -> p - 0.05f);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (p) -> p - 0.05f);
    }
}
