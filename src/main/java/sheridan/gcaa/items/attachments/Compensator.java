package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.awt.*;
import java.util.List;

public class Compensator extends Attachment{
    private final float pitchRecoilLowerRate;
    private final float yawRecoilLowerRate;
    private final float pitchRecoilControlIncRate;
    private final float yawRecoilControlIncRate;

    public Compensator(float pitchRecoilLowerRate, float yawRecoilLowerRate, float pitchRecoilControlIncRate, float yawRecoilControlIncRate) {
        super(0);
        this.pitchRecoilLowerRate = pitchRecoilLowerRate;
        this.yawRecoilLowerRate = yawRecoilLowerRate;
        this.pitchRecoilControlIncRate = pitchRecoilControlIncRate;
        this.yawRecoilControlIncRate = yawRecoilControlIncRate;
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_COMPENSATOR);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate - pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate - yawRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + pitchRecoilControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + yawRecoilControlIncRate);
        super.onAttach(player, stack, gun, data);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_NORMAL);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate + pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate + yawRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - pitchRecoilControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - yawRecoilControlIncRate);
        super.onDetach(player, stack, gun, data);
    }

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> effectsInGunModifyScreen = super.getEffectsInGunModifyScreen();
        if (pitchRecoilLowerRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_pitch", -pitchRecoilLowerRate, pitchRecoilLowerRate > 0));
        if (yawRecoilLowerRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_horizontal", -yawRecoilLowerRate, yawRecoilLowerRate > 0));
        if (pitchRecoilControlIncRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_control_pitch", pitchRecoilControlIncRate, pitchRecoilControlIncRate > 0));
        if (yawRecoilControlIncRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_control_horizontal", yawRecoilControlIncRate, yawRecoilControlIncRate > 0));
        return effectsInGunModifyScreen;
    }
}
