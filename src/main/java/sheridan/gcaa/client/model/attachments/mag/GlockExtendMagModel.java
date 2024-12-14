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
public class GlockExtendMagModel implements IAttachmentModel {
    private final ModelPart glock_exp_mag, glock_bullet;
    private final ModelPart low;

    public GlockExtendMagModel() {
        glock_exp_mag = StatisticModel.MAG_COLLECTION1.get("glock_exp_mag");
        glock_bullet = glock_exp_mag.getChild("glock_bullet").meshing();
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("mag_collection1").getChild("glock_exp_mag").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        if (context.useLowQuality()) {
            low.copyFrom(pose);
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
            low.resetPose();
            return;
        }
        glock_exp_mag.copyFrom(pose);
        glock_bullet.visible = context.shouldBulletRender();
        context.render(glock_exp_mag, context.getBuffer(RenderType.entityCutout(StatisticModel.MAG_COLLECTION1.texture)));
        glock_exp_mag.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return glock_exp_mag;
    }
}
