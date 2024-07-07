package sheridan.gcaa.items.guns;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sheridan.gcaa.capability.PlayerStatusProvider;

import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class ArmPoseHandler implements IClientItemExtensions {

    public static final ArmPoseHandler ARM_POSE_HANDLER = new ArmPoseHandler();

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        AtomicReference<HumanoidModel.ArmPose> pose = new AtomicReference<>(null);
        if (entityLiving instanceof Player player) {
            player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent(status -> {
                if (status.isReloading()) {
                    pose.set(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                } else {
                    pose.set(HumanoidModel.ArmPose.BOW_AND_ARROW);
                }
            });
        }
        return pose.get();
    }
}