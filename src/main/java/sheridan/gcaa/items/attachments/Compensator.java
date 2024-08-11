package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class Compensator extends Attachment{
    private final float pitchRecoilLowerRate;
    private final float yawRecoilLowerRate;
    private final float pitchRecoilControlIncRate;
    private final float yawRecoilControlIncRate;

    public Compensator(float pitchRecoilLowerRate, float yawRecoilLowerRate, float pitchRecoilControlIncRate, float yawRecoilControlIncRate) {
        this.pitchRecoilLowerRate = pitchRecoilLowerRate;
        this.yawRecoilLowerRate = yawRecoilLowerRate;
        this.pitchRecoilControlIncRate = pitchRecoilControlIncRate;
        this.yawRecoilControlIncRate = yawRecoilControlIncRate;
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_COMPENSATOR);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate - pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate - yawRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + pitchRecoilControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + yawRecoilControlIncRate);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setMuzzleFlash(data, Gun.MUZZLE_STATE_NORMAL);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH, data, (prevRate) -> prevRate + pitchRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW, data, (prevRate) -> prevRate + yawRecoilLowerRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - pitchRecoilControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - yawRecoilControlIncRate);
    }
}