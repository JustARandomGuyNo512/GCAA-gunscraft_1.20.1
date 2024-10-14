package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

public class Grip extends Attachment implements IArmReplace{
    private final float pitchRecoilControlIncRate;
    private final float yawRecoilControlIncRate;
    private final float agilityIncRate;

    public Grip(float pitchRecoilControlIncRate, float yawRecoilControlIncRate, float agilityIncRate, float weight)  {
        super(weight);
        this.pitchRecoilControlIncRate = pitchRecoilControlIncRate;
        this.yawRecoilControlIncRate = yawRecoilControlIncRate;
        this.agilityIncRate = agilityIncRate;
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.addWeight(data, weight);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.addWeight(data, -weight);
    }

    @Override
    public boolean replaceArmRender(boolean mainHand) {
        return !mainHand;
    }

    @Override
    public int orderForArmRender(boolean mainHand) {
        return mainHand ? 0 : 1;
    }

    @Override
    public float getPitchRecoilControlIncRate() {
        return pitchRecoilControlIncRate;
    }

    @Override
    public float getYawRecoilControlIncRate() {
        return yawRecoilControlIncRate;
    }

    @Override
    public float getAgilityIncRate() {
        return agilityIncRate;
    }
}
