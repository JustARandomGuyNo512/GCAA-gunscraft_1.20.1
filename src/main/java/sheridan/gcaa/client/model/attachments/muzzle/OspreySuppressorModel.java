package sheridan.gcaa.client.model.attachments.muzzle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.MuzzleFlashRendererModel;
import sheridan.gcaa.client.model.attachments.statistic.MuzzleCollection1;
import sheridan.gcaa.client.model.attachments.statistic.MuzzleCollection2;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class OspreySuppressorModel extends MuzzleFlashRendererModel implements IAttachmentModel {

    private final ModelPart model;
    private final ModelPart muzzle;
    private final ResourceLocation texture = MuzzleCollection2.TEXTURE;

    public OspreySuppressorModel() {
        model = MuzzleCollection2.get("osprey_suppressor");
        muzzle = model.getChild("osprey_suppressor_muzzle");
    }

    @Override
    public ModelPart getRoot() {
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