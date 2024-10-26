package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Mk47Model extends GunModel  {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.png");

    private ModelPart barrel, muzzle, stock, charge, body, safety, grip, handguard, mag, slide, IS, IS_front, bullet, sight, sight_front;

    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    public Mk47Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        stock = gun.getChild("stock").meshing();
        charge = gun.getChild("charge").meshing();
        body = gun.getChild("body").meshing();
        safety = gun.getChild("safety").meshing();
        grip = gun.getChild("grip").meshing();
        handguard = gun.getChild("handguard").meshing();
        mag = gun.getChild("mag").meshing();
        slide = gun.getChild("slide").meshing();
        IS = gun.getChild("IS").meshing();
        IS_front = gun.getChild("IS_front").meshing();
        sight = IS.getChild("sight").meshing();
        sight_front = IS_front.getChild("sight_front").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        bullet.visible = context.shouldBulletRender();
        context.renderIf(muzzle, vertexConsumer, !context.hasMuzzle());
        context.renderIf(mag, vertexConsumer, !context.hasMag());
        context.renderIf(grip, vertexConsumer, !context.hasGrip());
        context.renderIf(stock, vertexConsumer, !context.hasStock());
        if (context.hasScope()) {
            sight.xRot = 1.5707963267948966f;
            sight_front.xRot = 1.5707963267948966f;
        } else {
            sight.xRot = 0.0f;
            sight_front.xRot = 0.0f;
        }
        context.render(vertexConsumer, barrel, charge, body, safety, slide, IS, IS_front);
        if (!context.hasHandguard()) {
            VertexConsumer handguardConsumer = context.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
            context.render(handguardConsumer, handguard);
        }
        context.renderArmLong(left_arm, false);
        context.renderArmLong(right_arm, true);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderMagAttachmentIf(mag, context.hasMag());
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
    protected void animationGlobal(GunRenderContext context) {
        defaultAssaultRifleAnimation(context, recoil, shoot);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        root.resetPose();
        slide.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        camera.resetPose();
        mag.resetPose();
        charge.resetPose();
    }
}
