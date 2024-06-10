package sheridan.gcaa.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.utils.Quaternions;
import sheridan.gcaa.items.guns.IGun;

@Mixin(ItemRenderer.class)
public class RenderItemMixin {

    // all model, ground, gui, other
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void Other(ItemStack itemStackIn, ItemDisplayContext transformTypeIn, boolean p_115146_, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BakedModel p_115151_, CallbackInfo ci) {
        if (itemStackIn != null && itemStackIn.getItem() instanceof IGun gun) {
            IGunModel model = GunModelRegistry.getModel(gun);
            if (model != null) {
                poseStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                model.render(new GunRenderContext(
                        bufferIn,
                        poseStackIn,
                        itemStackIn,
                        gun,
                        transformTypeIn,
                        false,
                        combinedLightIn,
                        combinedOverlayIn
                ));
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", cancellable = true)
    public void FirstAndThirdPersonAndEntity(LivingEntity livingEntityIn, ItemStack itemStackIn, ItemDisplayContext transformTypeIn, boolean leftHand, PoseStack poseStackIn, MultiBufferSource bufferIn, Level level, int combinedLightIn, int combinedOverlayIn, int p_174252_, CallbackInfo ci) {
        if (livingEntityIn instanceof Player) {
            if (itemStackIn != null && itemStackIn.getItem() instanceof IGun gun) {
                IGunModel model = GunModelRegistry.getModel(gun);
                if (model != null) {
                    poseStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                    model.render(new GunRenderContext(
                            bufferIn,
                            poseStackIn,
                            itemStackIn,
                            gun,
                            transformTypeIn,
                            !leftHand,
                            combinedLightIn,
                            combinedOverlayIn
                    ));
                    ci.cancel();
                }
            }
        }
    }

}
