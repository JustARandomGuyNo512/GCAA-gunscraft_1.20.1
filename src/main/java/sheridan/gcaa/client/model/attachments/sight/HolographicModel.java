package sheridan.gcaa.client.model.attachments.sight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.CommonSightModel;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class HolographicModel extends CommonSightModel {
    private final ModelPart crosshair;
    private final ModelPart body, min_z_dis;
    private final ResourceLocation texture = StatisticModel.SIGHTS1.texture;

    public HolographicModel() {
        super(StatisticModel.SIGHTS1.get("holographic"));
        crosshair = root.getChild("crosshair_holo");
        body = root.getChild("holographic_body");
        min_z_dis = root.getChild("min_z_dis_holo");
    }

    @Override
    public void handleCrosshairTranslation(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        crosshair.translateAndRotate(poseStack);
    }

    @Override
    public ModelPart getCrosshair() {
        return crosshair;
    }

    @Override
    protected void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        SightViewRenderer.renderRedDot(context.isEffectiveSight(attachmentRenderEntry), context, texture, StatisticModel.HOLOGRAPHIC_CROSSHAIR, crosshair, body);
    }

    @Override
    protected ModelPart getMinZDis() {
        return min_z_dis;
    }

}
