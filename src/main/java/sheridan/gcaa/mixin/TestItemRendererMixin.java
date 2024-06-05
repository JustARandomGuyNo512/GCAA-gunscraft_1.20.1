package sheridan.gcaa.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class TestItemRendererMixin {

//    @Inject(method = "getFoilBufferDirect", at = @At("HEAD"))
//    private static void beforeGetCompassFoilBufferDirect(MultiBufferSource pBuffer, RenderType pRenderType, boolean pNoEntity, boolean pWithGlint, CallbackInfoReturnable<VertexConsumer> cir) {
//        System.out.println(pRenderType.toString() + "\n\n" + pRenderType + "===================================");
//    }
}
