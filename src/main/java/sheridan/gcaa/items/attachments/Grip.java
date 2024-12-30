package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;

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

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> effectsInGunModifyScreen = super.getEffectsInGunModifyScreen();
        if (pitchRecoilControlIncRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_control_pitch", pitchRecoilControlIncRate, pitchRecoilControlIncRate > 0));
        if (yawRecoilControlIncRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_control_horizontal", yawRecoilControlIncRate, yawRecoilControlIncRate > 0));
        if (agilityIncRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("agility", agilityIncRate, agilityIncRate > 0));
        effectsInGunModifyScreen.add(FontUtils.helperTip(Component.translatable("tooltip.gcaa.grip_effective")));
        return effectsInGunModifyScreen;
    }
}
