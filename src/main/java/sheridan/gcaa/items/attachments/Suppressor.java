package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;

public class Suppressor extends Attachment{
    private final float volumeLowerRate;
    private final float pitchRecoilLowerRate;
    private final float yawRecoilLowerRate;

    public Suppressor(float volumeLowerRate, float pitchRecoilLowerRate, float yawRecoilLowerRate, float weight) {
        super(weight);
        this.volumeLowerRate = Mth.clamp(volumeLowerRate, 0, 1);
        this.pitchRecoilLowerRate = Mth.clamp(pitchRecoilLowerRate, 0, 1);
        this.yawRecoilLowerRate = Mth.clamp(yawRecoilLowerRate, 0, 1);
    }

    public float getVolumeLowerRate() {
        return volumeLowerRate;
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_SUPPRESSOR);
        properties.setPropertyRateIfHas(GunProperties.FIRE_SOUND_VOL, data, (prevRate) -> prevRate - volumeLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate - pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate - yawRecoilLowerRate);
        super.onAttach(player, stack, gun, data);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_NORMAL);
        properties.setPropertyRateIfHas(GunProperties.FIRE_SOUND_VOL, data, (prevRate) -> prevRate + volumeLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate + pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate + yawRecoilLowerRate);
        super.onDetach(player, stack, gun, data);
    }


    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> effectsInGunModifyScreen = super.getEffectsInGunModifyScreen();
        if (volumeLowerRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("fire_sound_vol", -volumeLowerRate, volumeLowerRate > 0));
        if (pitchRecoilLowerRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_pitch", -pitchRecoilLowerRate, pitchRecoilLowerRate > 0));
        if (yawRecoilLowerRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_horizontal", -yawRecoilLowerRate, yawRecoilLowerRate > 0));
        return effectsInGunModifyScreen;
    }
}
