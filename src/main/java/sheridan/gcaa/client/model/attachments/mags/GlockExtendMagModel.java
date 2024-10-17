package sheridan.gcaa.client.model.attachments.mags;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.statistic.MagCollection1;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class GlockExtendMagModel implements IAttachmentModel {
    private final ModelPart glock_exp_mag, glock_bullet;

    public GlockExtendMagModel() {
        glock_exp_mag = MagCollection1.get("glock_exp_mag");
        glock_bullet = glock_exp_mag.getChild("glock_bullet").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        glock_exp_mag.copyFrom(pose);
        glock_bullet.visible = context.shouldBulletRender();
        context.render(glock_exp_mag, context.getBuffer(RenderType.entityCutout(MagCollection1.TEXTURE)));
        glock_exp_mag.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return glock_exp_mag;
    }
}