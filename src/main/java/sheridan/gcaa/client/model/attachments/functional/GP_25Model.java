package sheridan.gcaa.client.model.attachments.functional;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class GP_25Model extends GrenadeLauncherModel {
    public GP_25Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/gp_25/gp_25.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/gp_25/gp_25.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/gp_25/reload_rifle.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/generic/gp_25_reload_ak_rifle.animation.json"),
                StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("gp_25").meshing(), StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture);
    }

    @Override
    protected void posInit(ModelPart root) {
        left_arm_pistol = root.getChild("left_arm");
        left_arm_rifle = root.getChild("left_arm_new");
        body = root.getChild("body").meshing();
        grenade = root.getChild("grenade").meshing();
        grenade_reloading = root.getChild("grenade_reloading").meshing();
        muzzle = root.getChild("muzzle");
    }

    @Override
    protected void afterAnimation() {
        left_arm_pistol.resetPose();
        left_arm_rifle.resetPoseAll();
        grenade_reloading.resetPose();
    }

    @Override
    protected void renderMain(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose, boolean showAnimation, boolean hasGrenade) {
        VertexConsumer vertexConsumer = context.solid(texture);
        context.render(body, vertexConsumer);
        context.renderIf(vertexConsumer, hasGrenade, grenade);
        context.renderIf(vertexConsumer, showAnimation, grenade_reloading);
    }
}
