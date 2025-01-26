package sheridan.gcaa.client.model.attachments.functional;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class M203Model extends GrenadeLauncherModel{
    private ModelPart barrel, head;

    public M203Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/m203/m203.png"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/m203/m203.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/functional/m203/reload.animation.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/generic/m203_reload_m4a1_rifle.animation.json"),
                null, null);
    }

    @Override
    protected void posInit(ModelPart root) {
        body = root.getChild("body").meshing();
        left_arm_rifle = root.getChild("left_arm");
        muzzle = root.getChild("muzzle");
        grenade_reloading = root.getChild("grenade_reloading").meshing();
        barrel = body.getChild("barrel").meshing();
        grenade = barrel.getChild("grenade");
        head = grenade.getChild("head").meshing();
        grenade.getChild("shell").meshing();
    }

    @Override
    protected void afterAnimation() {
        barrel.resetPoseAll();
        left_arm_rifle.resetPoseAll();
        grenade_reloading.resetPose();
    }

    @Override
    protected void renderMain(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose, boolean showAnimation, boolean hasGrenade)  {
        VertexConsumer vertexConsumer = context.solid(texture);
        context.render(body, vertexConsumer);
        head.visible = hasGrenade;
        context.renderIf(vertexConsumer, showAnimation, grenade_reloading);
    }
}
