package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class SightViewRenderer {

    public static void renderRedDot(boolean effective, GunRenderContext context, ResourceLocation bodyTexture, ResourceLocation crosshairTexture, ModelPart crosshair, ModelPart... bodyParts) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(bodyTexture));
        for (ModelPart part : bodyParts) {
            context.render(part, vertexConsumer);
        }
    }

    public static void renderScope() {

    }
}
