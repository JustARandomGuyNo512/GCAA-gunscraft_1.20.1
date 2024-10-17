package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Xm1014Model extends GunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.png");
    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    private ModelPart IS, handguard, stock, barrel, mag, body, slide, ammo_track, reloading_arm, shell;

    public Xm1014Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/xm1014/xm1014.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        this.IS = gun.getChild("IS").meshing();
        this.handguard = gun.getChild("handguard").meshing();
        this.stock = gun.getChild("stock").meshing();
        this.barrel = gun.getChild("barrel").meshing();
        this.mag = gun.getChild("mag").meshing();
        this.body = gun.getChild("body").meshing();
        this.slide = gun.getChild("slide").meshing();
        this.ammo_track = gun.getChild("ammo_track").meshing();
        this.reloading_arm = gun.getChild("reloading_arm").meshing();
        this.shell = reloading_arm.getChild("shell").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        ModelPart leftArm = left_arm.xScale > 0 ? left_arm : reloading_arm;
        ModelPart rightArm = right_arm.xScale > 0 ? right_arm : reloading_arm;
        if (context.isFirstPerson && (leftArm == reloading_arm ||rightArm == reloading_arm)) {
            context.pushPose().translateTo(reloading_arm).render(shell, vertexConsumer);
            context.popPose();
        }
        context.render(vertexConsumer, IS, handguard, stock, barrel, mag, body, slide, ammo_track);
        context.renderArmLong(leftArm, false);
        context.renderArmLong(rightArm, true);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyHandAction(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public AnimationDefinition getRecoil() {
        return recoil;
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        slide.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        reloading_arm.resetPoseAll();
        camera.resetPose();
    }
}
