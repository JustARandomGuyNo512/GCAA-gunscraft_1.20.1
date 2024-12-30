package sheridan.gcaa.items.attachments.replaceableParts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.ReplaceableGunPart;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;

public class RecoilLowerPart extends ReplaceableGunPart {
    private final float vertical;
    private final float horizontal;
    public RecoilLowerPart(float weight, float vertical, float horizontal) {
        super(weight);
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    @Override
    public void onOccupied(ItemStack stack, IGun gun, CompoundTag data) {
        super.onOccupied(stack, gun, data);
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (p) -> p + vertical);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (p) -> p + horizontal);
    }

    @Override
    public void onEmpty(ItemStack stack, IGun gun, CompoundTag data) {
        super.onEmpty(stack, gun, data);
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (p) -> p - vertical);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (p) -> p - horizontal);
    }

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> effectsInGunModifyScreen = super.getEffectsInGunModifyScreen();
        if (vertical != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_pitch", -vertical, vertical > 0));
        if (horizontal != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_horizontal", -horizontal, horizontal > 0));
        return effectsInGunModifyScreen;
    }
}
