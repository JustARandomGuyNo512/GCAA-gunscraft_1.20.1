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
public class ARSuppressorModel extends MuzzleFlashRendererModel implements IAttachmentModel {

    private final ModelPart model;
    private final ModelPart muzzle;
    private final ResourceLocation texture = StatisticModel.MUZZLE_COLLECTION1.texture;

    public ARSuppressorModel() {
        model = StatisticModel.MUZZLE_COLLECTION1.get("ar_suppressor").meshing();
        muzzle = model.getChild("ar_suppressor_muzzle");
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return model;
    }

    @Override
    public void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.render(model, context.getBuffer(RenderType.entityCutout(texture)));
    }

    @Override
    public void handleMuzzleTranslate(PoseStack poseStack) {
        muzzle.translateAndRotate(poseStack);
    }

    @Override
    public void renderMuzzleFlash(GunRenderContext context) {
        defaultRenderMuzzleFlash(context, 1);
    }
}
