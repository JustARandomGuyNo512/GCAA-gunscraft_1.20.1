package sheridan.gcaa.client.model.attachments.mag;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class StatisticMagModel implements IAttachmentModel {
    private final ModelPart mag;
    private final ModelPart bullet;
    private final ResourceLocation TEXTURE;

    public StatisticMagModel(StatisticModel collection, String magName, String bulletName) {
        mag = collection.get(magName);
        mag.meshingAll();
        bullet = mag.getChild(bulletName).meshing();
        TEXTURE = collection.texture;
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        mag.copyFrom(pose);
        bullet.visible = context.shouldBulletRender();
        context.render(mag, context.solid(TEXTURE));
        mag.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return mag;
    }
}
