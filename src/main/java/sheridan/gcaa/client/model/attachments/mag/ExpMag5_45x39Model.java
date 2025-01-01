package sheridan.gcaa.client.model.attachments.mag;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class ExpMag5_45x39Model implements IAttachmentModel {
    private final ModelPart mag;
    private final ModelPart low;
    private final ModelPart bullet;

    public ExpMag5_45x39Model() {
        mag = StatisticModel.MAG_COLLECTION2.get("exp_5_45x39").meshing();
        low = StatisticModel.MAG_COLLECTION2.get("exp_5_45x39_low").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        ModelPart model = context.useLowQuality() ? low : mag;
        model.copyFrom(pose);
        bullet.visible = context.shouldBulletRender();
        context.render(model, context.solid(StatisticModel.MAG_COLLECTION2.texture));
        model.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return mag;
    }
}
