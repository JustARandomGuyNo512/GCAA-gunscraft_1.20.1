package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.RenderTypes;

public class HkG28Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.png");
    private ModelPart barrel, IS_front, handguard, muzzle, stock, charge, body, safety, bolt, IS, grip, mag, bullet,
    sub_rail_left, sub_rail_right, sub_rail_down;
    private final AnimationDefinition shoot;
    private final AnimationDefinition recoil;


    public HkG28Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.animation.json"));
        shoot = animations.get("shoot");
        recoil = animations.get("recoil");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        handguard = gun.getChild("handguard").meshing();
        IS_front = handguard.getChild("IS_front").meshing();
        sub_rail_left = handguard.getChild("sub_rail_left").meshing();
        sub_rail_right = handguard.getChild("sub_rail_right").meshing();
        sub_rail_down = handguard.getChild("sub_rail_down").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        stock = gun.getChild("stock").meshing();
        charge = gun.getChild("charge").meshing();
        body = gun.getChild("body").meshing();
        safety = gun.getChild("safety").meshing();
        bolt = gun.getChild("bolt").meshing();
        IS = gun.getChild("IS").meshing();
        grip = gun.getChild("grip").meshing();
        mag = gun.getChild("mag").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        bullet.visible = context.shouldBulletRender();
        context.renderIf(IS, vertexConsumer, context.notContainsScope());
        context.renderIf(muzzle, vertexConsumer, context.notHasMuzzle());
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        context.renderIf(stock, vertexConsumer, context.notHasStock());
        context.render(vertexConsumer, barrel, charge, body, safety, bolt, grip);
        sub_rail_left.visible = context.has("handguard_front_left");
        sub_rail_right.visible = context.has("handguard_front_right");
        sub_rail_down.visible = context.has("handguard_front");
        IS_front.xRot = context.notContainsScope() ? 0 : 1.57079632679489655f;
        vertexConsumer = context.solidMipMap(TEXTURE);
        context.render(vertexConsumer, handguard);
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

    @Override
    protected void animationGlobal(GunRenderContext context) {
        float scale = Mth.lerp(Clients.getAdsProgress(), 1f, 0.35f);
        KeyframeAnimations.animate(this, recoil, context.lastShoot, scale);
        defaultAssaultRifleAnimation(context, shoot);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        root.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        mag.resetPose();
        bolt.resetPose();
        camera.resetPose();
    }
}
