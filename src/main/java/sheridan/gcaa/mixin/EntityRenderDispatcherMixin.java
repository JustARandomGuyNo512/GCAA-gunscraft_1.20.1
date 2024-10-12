package sheridan.gcaa.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sheridan.gcaa.common.HeadBox;
import sheridan.gcaa.common.config.CommonConfig;


@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject( method = "renderHitbox", at = @At("HEAD"))
    private static void hitBoxRenderMixin(PoseStack poseStack, VertexConsumer p_114443_, Entity entity, float p_114445_, CallbackInfo ci) {
        if (!CommonConfig.enableHeadShot.get() || !HeadBox.contains(entity.getType())) {
            return;
        }
        poseStack.pushPose();
        try {
            HeadBox box = HeadBox.getBox(entity.getType());
            if (box != null) {
                AABB aabb = box.createAABB(entity).move(-entity.getX(), -entity.getY(), -entity.getZ());
                LevelRenderer.renderLineBox(poseStack, p_114443_, aabb, 0.0F, 1F, 0F, 1.0F);
            }
        } catch (Exception ignored){}
        poseStack.popPose();
    }

}
