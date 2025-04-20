package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.gun.AKModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.ModItems;

@OnlyIn(Dist.CLIENT)
public class Saiga12kModel extends AKModel {
    private ModelPart mag, exp_mag, exp_mag_bullet, drum, drum_bullet;

    public Saiga12kModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        super(modelPath, animationPath, texture);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        mag = main.getChild("mag");
        bullet = mag.getChild("bullet");
        exp_mag = main.getChild("exp_mag");
        exp_mag_bullet = exp_mag.getChild("exp_mag_bullet");
        drum = main.getChild("drum");
        drum_bullet = drum.getChild("drum_bullet");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        mag.visible = context.notHasMag();
        if (!mag.visible) {
            AttachmentRenderEntry attachmentRenderEntry = context.getAttachmentRenderEntry("s_mag");
            exp_mag.visible = attachmentRenderEntry.attachment == ModItems.SAIGA_12K_EXP_MAG.get();
            drum.visible = !exp_mag.visible;
            drum_bullet.visible = context.shouldBulletRender();
            exp_mag_bullet.visible = context.shouldBulletRender();
        } else {
            exp_mag.visible = drum.visible = false;
        }
        super.renderGunModel(context);
    }
}
