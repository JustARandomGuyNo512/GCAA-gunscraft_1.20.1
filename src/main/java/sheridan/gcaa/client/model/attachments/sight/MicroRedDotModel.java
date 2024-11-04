package sheridan.gcaa.client.model.attachments.sight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class MicroRedDotModel extends SightModel{
    private final ModelPart model;
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ResourceLocation texture = StatisticModel.SIGHTS1.texture;
    private final ModelPart low;

    public MicroRedDotModel() {
        model = StatisticModel.SIGHTS1.get("red_dot_pistol");
        crosshair = model.getChild("crosshair_pistol");
        body = model.getChild("red_dot_pistol_body");
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("sights1").getChild("red_dot_pistol").meshing();
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
        if (context.useLowQuality()) {
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
            return;
        }
        SightViewRenderer.renderRedDot(context.isEffectiveSight(attachmentRenderEntry), 0.025f, context, texture, StatisticModel.RED_DOT_CROSSHAIR, crosshair, body);
    }

    @Override
    public ModelPart getRoot() {
        return model;
    }

}
