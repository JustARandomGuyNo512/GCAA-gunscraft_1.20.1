package sheridan.gcaa.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sheridan.gcaa.client.MuzzleFlashLightHandler;

@Mixin(BlockLightEngine.class)
public class BlockLightEngineMixin {



    @Inject(method = "getEmission", at= @At("HEAD"), cancellable = true, remap = true)
    private void onGetLightEmission(long pPackedPos, BlockState pState, CallbackInfoReturnable<Integer> cir) {
        if (MuzzleFlashLightHandler.isOverrideLight() && pPackedPos == MuzzleFlashLightHandler.getPackBlockPos()) {
            MuzzleFlashLightHandler.lightUpdateReceived();
            cir.setReturnValue(10);
            cir.cancel();
        }
    }
}
