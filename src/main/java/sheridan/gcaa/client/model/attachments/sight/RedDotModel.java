package sheridan.gcaa.client.model.attachments.sight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.attachments.statistic.Sights1;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class RedDotModel extends SightModel{
    private final ModelPart model;
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ResourceLocation texture = Sights1.TEXTURE;

    public RedDotModel() {
        model = Sights1.get("red_dot_rifle");
        crosshair = model.getChild("crosshair_rifle");
        body = model.getChild("red_dot_rifle_body");
    }

    @Override
    public void handleCrosshairTranslation(PoseStack poseStack) {
        crosshair.translateAndRotate(poseStack);
    }

    @Override
    public ModelPart getCrosshair() {
        return crosshair;
    }

    @Override
    protected void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        SightViewRenderer.renderRedDot(context.isEffectiveSight(attachmentRenderEntry), 0.035f, context, texture, Sights1.RED_DOT_CROSSHAIR, crosshair, body);
    }

    @Override
    public ModelPart getRoot() {
        return model;
    }

}
