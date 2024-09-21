package sheridan.gcaa.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.items.gun.IGun;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Inject(method = "setupAnim*", at = @At("TAIL"))
    public void adjustArmPose(LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        if (pEntity instanceof Player player) {
            player.getCapability(PlayerStatusProvider.CAPABILITY).ifPresent(status -> {
                if (!status.isReloading() && player.getMainHandItem().getItem() instanceof IGun gun &&
                        gun.canUseWithShield() && player.getOffhandItem().getItem() instanceof ShieldItem) {
                    ModelPart rightArm = ((HumanoidModel<?>)(Object)this).rightArm;
                    ModelPart head = ((HumanoidModel<?>)(Object)this).head;
                    rightArm.yRot = -0.1F + head.yRot;
                    rightArm.xRot = (-(float)Math.PI * 0.5F) + head.xRot;
                }
            });
        }
    }
}
