package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Python357Model extends GunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.png");
    private final AnimationDefinition recoil, reload;
    private ModelPart body;
    private ModelPart hammer;
    private ModelPart mag;
    private ModelPart reloading_arm;
    private ModelPart loader;
    private ModelPart drum;
    private ModelPart[] unFiredBullets;
    private ModelPart[] firedBullets;

    public Python357Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.animation.json"));
        recoil = animations.get("recoil");
        reload = animations.get("reload");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        body = gun.getChild("body").meshing();
        hammer = gun.getChild("hammer").meshing();
        mag = gun.getChild("mag").meshing();
        drum = mag.getChild("drum").meshing();
        ModelPart bullets = drum.getChild("bullets").meshing();
        unFiredBullets = new ModelPart[] {
                bullets.getChild("bullet1").meshing(),
                bullets.getChild("bullet2").meshing(),
                bullets.getChild("bullet3").meshing(),
                bullets.getChild("bullet4").meshing(),
                bullets.getChild("bullet5").meshing(),
                bullets.getChild("bullet6").meshing()};
        firedBullets = new ModelPart[] {
                bullets.getChild("bullet1_fired").meshing(),
                bullets.getChild("bullet2_fired").meshing(),
                bullets.getChild("bullet3_fired").meshing(),
                bullets.getChild("bullet4_fired").meshing(),
                bullets.getChild("bullet5_fired").meshing(),
                bullets.getChild("bullet6_fired").meshing()};
        reloading_arm = gun.getChild("reloading_arm");
        loader = reloading_arm.getChild("loader").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        if (context.isFirstPerson) {
            handleBulletsVisible(context);
            handleChargeAnimation(context);
        }
        context.render(vertexConsumer, body, hammer, mag);
        if (ReloadingHandler.isReloading()) {
            context.pushPose().translateTo(reloading_arm).render(loader, vertexConsumer);
            context.popPose();
        }
        ModelPart leftArm = left_arm.xScale == 0 ? reloading_arm : left_arm;
        context.renderArm(leftArm, false);
        context.renderArm(right_arm, true);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {}

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    private static final float R_45 = (float) Math.toRadians(45);
    private static final float R_60 = (float) Math.toRadians(60);
    private void handleChargeAnimation(GunRenderContext context) {
        float chargeProgress = Clients.mainHandStatus.getLerpedChargeTick(Minecraft.getInstance().getPartialTick());
        int ammoLeft = context.ammoLeft;
        if (chargeProgress != 0) {
            hammer.xRot = -Mth.lerp(chargeProgress, 0, R_45);
        }
        drum.zRot = -Mth.lerp(chargeProgress, (6 - ammoLeft) * R_60, (6 - ammoLeft + 1) * R_60);
    }

    private void handleBulletsVisible(GunRenderContext context) {
        int ammoLeft = context.ammoLeft;
        for (int i = 0; i < 6; i ++) {
            firedBullets[i].visible = (6 - ammoLeft) >= (i + 1);
            unFiredBullets[5 - i].visible = ammoLeft >= (i + 1);
        }
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        gun.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        reloading_arm.resetPoseAll();
        hammer.resetPose();
        mag.resetPoseAll();
        camera.resetPose();
    }

    @Override
    public AnimationDefinition getRecoil() {
        return recoil;
    }

    @Override
    public AnimationDefinition getFullReload() {
        return reload;
    }
}
