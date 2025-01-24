package sheridan.gcaa.client.model.attachments.mag;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class Drum5_45x39Model implements IAttachmentModel {
    private final ModelPart drum, bullet;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/mags/drum5_45x39.png");

    public Drum5_45x39Model() {
        ModelPart root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/mags/drum5_45x39.geo.json")).bakeRoot().getChild("root");
        drum = root.getChild("5_45x39_drum").meshing();
        bullet = drum.getChild("bullet").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        drum.copyFrom(pose);
        bullet.visible = context.shouldBulletRender();
        context.render(drum, context.solid(TEXTURE));
        drum.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return drum;
    }
}
