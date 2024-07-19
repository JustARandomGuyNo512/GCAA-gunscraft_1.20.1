package sheridan.gcaa.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sheridan.gcaa.client.animation.recoilAnimation.ICameraShakeHandler;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.items.gun.IGun;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    public boolean isBobbingLevelView;

    @Inject(method = "getFov", at = @At("HEAD"))
    public void onGetFov(Camera pActiveRenderInfo, float pPartialTicks, boolean pUseFOVSetting, CallbackInfoReturnable<Double> cir) {
        isBobbingLevelView = pUseFOVSetting;
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void onBobbingView(PoseStack pPoseStack, float pPartialTicks, CallbackInfo ci) {
        if (isBobbingLevelView) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof IGun gun) {
                ICameraShakeHandler shakeHandler = RecoilCameraHandler.INSTANCE.getCameraShakeHandler();
                if (shakeHandler != null) {
                    if (shakeHandler.shake(pPartialTicks, pPoseStack, gun, player, player.getMainHandItem())) {
                        ci.cancel();
                    }
                }
            }
        }
    }

}
