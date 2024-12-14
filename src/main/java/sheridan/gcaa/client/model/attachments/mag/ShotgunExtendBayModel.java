package sheridan.gcaa.client.model.attachments.mag;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class ShotgunExtendBayModel implements IAttachmentModel {
    private final ModelPart shotgun_exp_mag;

    public ShotgunExtendBayModel() {
        shotgun_exp_mag = StatisticModel.MAG_COLLECTION1.get("shotgun_exp_mag");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        shotgun_exp_mag.copyFrom(pose);
        context.render(shotgun_exp_mag, context.getBuffer(RenderType.entityCutout(StatisticModel.MAG_COLLECTION1.texture)));
        shotgun_exp_mag.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return shotgun_exp_mag;
    }
}
