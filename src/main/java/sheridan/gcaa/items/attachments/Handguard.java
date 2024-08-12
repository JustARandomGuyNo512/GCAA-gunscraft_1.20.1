package sheridan.gcaa.items.attachments;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;
import java.util.Set;

public class Handguard extends Attachment implements ISubSlotProvider{
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
    public String canDetach(ItemStack stack, IGun gun, AttachmentSlot root, AttachmentSlot prevSlot) {
        String res = super.canDetach(stack, gun, root, prevSlot);
        boolean detach = PASSED.equals(res);
        if (detach) {
            for (AttachmentSlot child : prevSlot.getChildren().values()) {
                if (!child.isEmpty()) {
                    detach = false;
                    break;
                }
            }
            if (detach) {
                return PASSED;
            } else {
                return "tooltip.action_res.prevented_by_child";
            }
        }
        return res;
    }

    @Override
    public void appendSlots(AttachmentSlot parent) {
        for (Map.Entry<String, AttachmentSlot> entry : root.getChildren().entrySet()) {
            parent.addChild(entry.getValue().copy());
        }
    }
}
