package sheridan.gcaa.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sheridan.gcaa.Clients;

@Mixin(RenderTarget.class)
public abstract class RenderTargetMixin {

    @Inject(at = @At("HEAD"), method = "bindWrite")
    private void onBind(boolean p_83948_, CallbackInfo ci) {
        Clients.prevRenderTarget = (RenderTarget) (Object) this;
    }
}
