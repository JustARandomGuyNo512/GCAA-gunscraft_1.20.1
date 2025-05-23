package sheridan.gcaa.client.model.attachments.muzzle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.MuzzleFlashRendererModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class AKCompensatorModel extends MuzzleFlashRendererModel implements IAttachmentModel {
    private final ModelPart model;
    private final ModelPart muzzle;
    private final ModelPart low;
    private final ResourceLocation texture = StatisticModel.MUZZLE_COLLECTION1.texture;

    public AKCompensatorModel() {
        model = StatisticModel.MUZZLE_COLLECTION1.get("ak_compensator").meshing();
        muzzle = model.getChild("ak_compensator_muzzle");
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("muzzle_collection").getChild("ak_compensator").meshing();
    }

    @Override
    public void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        if (context.useLowQuality()) {
            low.copyFrom(model);
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
        } else {
            context.render(model, context.getBuffer(RenderType.entityCutout(texture)));
        }
    }

    @Override
    public void handleMuzzleTranslate(PoseStack poseStack) {
        muzzle.translateAndRotate(poseStack);
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return model;
    }

    @Override
    public void renderMuzzleFlash(GunRenderContext context) {
        defaultRenderMuzzleFlash(context, 1);
    }
}
