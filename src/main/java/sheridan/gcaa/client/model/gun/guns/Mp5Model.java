package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.AutoMagPositionModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.ModItems;

@OnlyIn(Dist.CLIENT)
public class Mp5Model extends AutoMagPositionModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.png");
    private ModelPart charge, body, stock, stock_ar, handguard, handguard_rail, mag, slide, safety, bullet;

    public Mp5Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mp5/mp5.animation.json"));
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        safety = gun.getChild("safety").meshing();
        body = gun.getChild("body").meshing();
        slide = gun.getChild("slide").meshing();
        mag = gun.getChild("mag").meshing();
        handguard = gun.getChild("handguard").meshing();
        handguard_rail = gun.getChild("handguard_railed").meshing();
        stock = gun.getChild("stock").meshing();
        stock_ar = gun.getChild("stock_ar").meshing();
        charge = gun.getChild("charge").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        bullet.visible = context.shouldBulletRender();
        boolean stockIsArTube = context.attachmentIs("stock", ModItems.AR_STOCK_TUBE.get());
        if (stockIsArTube) {
            context.render(stock_ar, vertexConsumer);
        } else {
            context.renderIf(this.stock, vertexConsumer, context.notHasStock());
        }
        handguard_rail.visible = context.has("handguard");
        handguard.visible = !handguard_rail.visible;
        context.render(vertexConsumer, safety, charge, body, handguard, handguard_rail, mag, slide);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        super.renderAttachmentsModel(context);
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {

    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        mag.resetPoseAll();
        camera.resetPoseAll();
        slide.resetPose();
        charge.resetPose();
    }

    @Override
    protected ModelPart getMag() {
        return mag;
    }
}
