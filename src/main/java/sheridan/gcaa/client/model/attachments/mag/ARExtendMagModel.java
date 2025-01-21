package sheridan.gcaa.client.model.attachments.mag;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class ARExtendMagModel implements IAttachmentModel {
    private final ModelPart ar_exp_mag;
    private final ModelPart ar_bullet;

    public ARExtendMagModel() {
        ar_exp_mag = StatisticModel.MAG_COLLECTION1.get("ar_exp_mag");
        ar_bullet = ar_exp_mag.getChild("ar_bullet").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        ar_exp_mag.copyFrom(pose);
        ar_bullet.visible = context.shouldBulletRender();
        context.render(ar_exp_mag, context.getBuffer(RenderType.entityCutout(StatisticModel.MAG_COLLECTION1.texture)));
        ar_exp_mag.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return ar_exp_mag;
    }
}
