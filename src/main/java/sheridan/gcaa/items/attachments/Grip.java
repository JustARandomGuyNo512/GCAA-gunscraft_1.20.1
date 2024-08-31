package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class Grip extends Attachment implements IArmReplace{
    private final float pitchRecoilControlIncRate;
    private final float yawRecoilControlIncRate;

    public Grip(float pitchRecoilControlIncRate, float yawRecoilControlIncRate) {
        this.pitchRecoilControlIncRate = pitchRecoilControlIncRate;
        this.yawRecoilControlIncRate = yawRecoilControlIncRate;
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + pitchRecoilControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + yawRecoilControlIncRate);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - pitchRecoilControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - yawRecoilControlIncRate);
    }

    @Override
    public boolean replaceArmRender(boolean mainHand) {
        return !mainHand;
    }

    @Override
    public int orderForArmRender(boolean mainHand) {
        return mainHand ? 0 : 1;
    }
}
