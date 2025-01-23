package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.FontUtils;

import java.util.List;
import java.util.Map;

public class Handguard extends SubSlotProvider{
    private final AttachmentSlot root;
    private final float recoilPitchControlIncRate;
    private final float yawPitchControlIncRate;
    private final boolean isLongHandguard;

    public Handguard(AttachmentSlot root, float recoilPitchControlIncRate, float yawPitchControlIncRate, float weight, boolean isLongHandguard) {
        super(weight);
        this.root = root;
        this.recoilPitchControlIncRate = recoilPitchControlIncRate;
        this.yawPitchControlIncRate = yawPitchControlIncRate;
        this.isLongHandguard = isLongHandguard;
    }

    public Handguard(AttachmentSlot root, float recoilPitchControlIncRate, float yawPitchControlIncRate, float weight) {
        this(root, recoilPitchControlIncRate, yawPitchControlIncRate, weight, false);
    }

    @Override
    public void onAttach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + recoilPitchControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + yawPitchControlIncRate);
        super.onAttach(player, stack, gun, data);
    }

    @Override
    public void onDetach(Player player, ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - recoilPitchControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - yawPitchControlIncRate);
        super.onDetach(player, stack, gun, data);
    }

    @Override
    public void appendSlots(AttachmentSlot parent, AttachmentSlot root, IGun gun) {
        for (Map.Entry<String, AttachmentSlot> entry : this.root.getChildren().entrySet()) {
            parent.addChild(AttachmentSlot.copyAll(entry.getValue()));
        }
    }

    @Override
    public List<Component> getEffectsInGunModifyScreen() {
        List<Component> effectsInGunModifyScreen = super.getEffectsInGunModifyScreen();
        if (recoilPitchControlIncRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_control_pitch", recoilPitchControlIncRate, recoilPitchControlIncRate > 0));
        if (yawPitchControlIncRate != 0) effectsInGunModifyScreen.add(FontUtils.effectTip("recoil_control_horizontal", yawPitchControlIncRate, yawPitchControlIncRate > 0));
        return effectsInGunModifyScreen;
    }

    public boolean isLongHandguard() {
        return isLongHandguard;
    }
}
