package sheridan.gcaa.client.model.attachments.sight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class RedDotModel extends SightModel{
    private final ModelPart model;
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ResourceLocation texture = StatisticModel.SIGHTS1.texture;

    public RedDotModel() {
        model = StatisticModel.SIGHTS1.get("red_dot_rifle");
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
        SightViewRenderer.renderRedDot(context.isEffectiveSight(attachmentRenderEntry), 0.0315f, context, texture, StatisticModel.RED_DOT_CROSSHAIR, crosshair, body);
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return model;
    }

}
