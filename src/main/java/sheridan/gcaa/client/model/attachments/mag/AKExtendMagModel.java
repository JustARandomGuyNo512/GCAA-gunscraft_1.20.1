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
public class AKExtendMagModel implements IAttachmentModel {
    private final ModelPart ak_exp_mag, ak_bullet;

    public AKExtendMagModel() {
        ak_exp_mag = StatisticModel.MAG_COLLECTION1.get("ak_exp_mag");
        ak_bullet = ak_exp_mag.getChild("ak_bullet").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        ak_exp_mag.copyFrom(pose);
        ak_bullet.visible = context.shouldBulletRender();
        context.render(ak_exp_mag, context.getBuffer(RenderType.entityCutout(StatisticModel.MAG_COLLECTION1.texture)));
        ak_exp_mag.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return ak_exp_mag;
    }
}
