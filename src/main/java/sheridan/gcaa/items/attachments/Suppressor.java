package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

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
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_SUPPRESSOR);
        properties.setPropertyRateIfHas(GunProperties.FIRE_SOUND_VOL, data, (prevRate) -> prevRate - volumeLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate - pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate - yawRecoilLowerRate);
        properties.addWeight(data, weight);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_NORMAL);
        properties.setPropertyRateIfHas(GunProperties.FIRE_SOUND_VOL, data, (prevRate) -> prevRate + volumeLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate + pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate + yawRecoilLowerRate);
        properties.addWeight(data, - weight);
    }

}
