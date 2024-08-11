package sheridan.gcaa.client.model.attachments.muzzle;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.muzzle.statistic.MuzzleCollection1;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AKCompensatorModel implements IAttachmentModel {
    private final ModelPart model;
    private final ResourceLocation texture = MuzzleCollection1.TEXTURE;

    public AKCompensatorModel() {
        model = MuzzleCollection1.get("ak_compensator");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        model.copyFrom(pose);
        context.render(model, context.getBuffer(RenderType.entityCutoutNoCull(texture)));
        model.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return model;
    }
}
