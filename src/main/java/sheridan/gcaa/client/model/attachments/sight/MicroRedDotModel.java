package sheridan.gcaa.client.model.attachments.sight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.ISightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.attachments.statistic.Sights1;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class MicroRedDotModel implements IAttachmentModel, ISightModel {
    private final ModelPart model;
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ResourceLocation texture = Sights1.TEXTURE;

    public MicroRedDotModel() {
        model = Sights1.get("red_dot_pistol");
        crosshair = model.getChild("crosshair_pistol");
        body = model.getChild("red_dot_pistol_body");
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
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        model.copyFrom(pose);
        context.translateTo(model);
        SightViewRenderer.renderRedDot(context.isEffectiveSight(attachmentRenderEntry), 0.025f, context, texture, Sights1.RED_DOT_CROSSHAIR, crosshair, body);
        model.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return model;
    }
}