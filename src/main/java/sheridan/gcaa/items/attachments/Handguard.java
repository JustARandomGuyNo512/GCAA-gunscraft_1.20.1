package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;

public class Handguard extends SubSlotProvider{
    private final AttachmentSlot root;
    private final float recoilPitchControlIncRate;
    private final float yawPitchControlIncRate;

    public Handguard(AttachmentSlot root, float recoilPitchControlIncRate, float yawPitchControlIncRate)    {
        this.root = root;
        this.recoilPitchControlIncRate = recoilPitchControlIncRate;
        this.yawPitchControlIncRate = yawPitchControlIncRate;
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate + recoilPitchControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate + yawPitchControlIncRate);
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        GunProperties properties = gun.getGunProperties();
        properties.setPropertyRateIfHas(GunProperties.RECOIL_PITCH_CONTROL, data, (prevRate) -> prevRate - recoilPitchControlIncRate);
        properties.setPropertyRateIfHas(GunProperties.RECOIL_YAW_CONTROL, data, (prevRate) -> prevRate - yawPitchControlIncRate);
    }

    @Override
    public void appendSlots(AttachmentSlot parent) {
        for (Map.Entry<String, AttachmentSlot> entry : root.getChildren().entrySet()) {
            parent.addChild(AttachmentSlot.copyAll(entry.getValue()));
        }
    }
}
