package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Ak12Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.png");
    private ModelPart barrel, mag, stock, body, dust_cover, handguard, grip, muzzle, slide, safety, IS, bullet;
    private ModelPart rail;
    private AnimationDefinition shoot;

    public Ak12Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/ak12/ak12.animation.json"));
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        mag = gun.getChild("mag").meshing();
        stock = gun.getChild("stock").meshing();
        body = gun.getChild("body").meshing();
        dust_cover = gun.getChild("dust_cover").meshing();
        handguard = gun.getChild("handguard").meshing();
        grip = gun.getChild("grip").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        slide = gun.getChild("slide").meshing();
        safety = gun.getChild("safety").meshing();
        IS = gun.getChild("IS").meshing();
        bullet = mag.getChild("bullet").meshing();
        rail = handguard.getChild("rail").meshing();
        shoot = animations.get("shoot");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        bullet.visible = context.shouldBulletRender();
        context.renderIf(IS, vertexConsumer, context.notContainsScope());
        context.renderIf(muzzle, vertexConsumer, context.notHasMuzzle());
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        context.renderIf(stock, vertexConsumer, context.notHasStock());
        rail.visible = false;
        context.render(vertexConsumer, barrel, handguard, body, safety, grip, slide, dust_cover);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderMagAttachmentIf(mag, !context.notHasMag());
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        defaultAssaultRifleAnimation(context, shoot);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        root.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        mag.resetPose();
        slide.resetPose();
        camera.resetPose();
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_mag".equals(modelSlotName) || super.hasSlot(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        if (name.equals("s_mag")) {
            handleGunTranslate(poseStack);
            mag.translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name);
    }
}
