package sheridan.gcaa.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sheridan.gcaa.client.config.ClientConfig;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GunRenderer;
import sheridan.gcaa.items.gun.IGun;

@Mixin(ItemRenderer.class)
public class RenderItemMixin {

    // all model, ground, gui, other
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void Other(ItemStack itemStackIn, ItemDisplayContext transformTypeIn, boolean leftHand, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BakedModel p_115151_, CallbackInfo ci) {
        if (itemStackIn != null && itemStackIn.getItem() instanceof IGun gun) {
            boolean overrideRender = true;
            if (transformTypeIn == ItemDisplayContext.GUI && ClientConfig.renderVanillaModelInGuiView.get()) {
                overrideRender = false;
            }
            if (transformTypeIn == ItemDisplayContext.GROUND && ClientConfig.renderVanillaModelInGroundView.get()) {
                overrideRender = false;
            }
            if (overrideRender) {
                ci.cancel();
            } else {
                return;
            }
            if (!leftHand) {
                IGunModel model = GunModelRegister.getModel(gun);
                DisplayData displayData = GunModelRegister.getDisplayData(gun);
                GunRenderer.justRenderModel(itemStackIn, transformTypeIn, poseStackIn, bufferIn, combinedLightIn, combinedOverlayIn, gun, model, displayData);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V", cancellable = true)
    public void FirstAndThirdPersonAndEntity(LivingEntity livingEntityIn, ItemStack itemStackIn, ItemDisplayContext transformTypeIn, boolean leftHand, PoseStack poseStackIn, MultiBufferSource bufferIn, Level level, int combinedLightIn, int combinedOverlayIn, int p_174252_, CallbackInfo ci) {
        if (itemStackIn == null) {
            return;
        }
        if (livingEntityIn instanceof Player player) {
            if (leftHand) {
                if (itemStackIn.getItem() instanceof IGun) {
                    ci.cancel();
                }
                if (player.getMainHandItem().getItem() instanceof IGun gun) {
                    if (!(itemStackIn.getItem() instanceof ShieldItem) || !gun.canUseWithShield()) {
                        ci.cancel();
                    }
                }
                return;
            }
            if (itemStackIn.getItem() instanceof IGun gun) {
                IGunModel model = GunModelRegister.getModel(gun);
                DisplayData displayData = GunModelRegister.getDisplayData(gun);
                GunRenderer.renderWithEntity(livingEntityIn, poseStackIn, itemStackIn, transformTypeIn, bufferIn, gun, combinedLightIn, combinedOverlayIn, false, model, displayData);
                ci.cancel();
            }
        }
    }

}
