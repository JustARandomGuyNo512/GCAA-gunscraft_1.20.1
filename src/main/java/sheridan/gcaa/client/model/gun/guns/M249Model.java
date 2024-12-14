package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.gun.BulletChainHandler;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class M249Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.png");
    private final AnimationDefinition shoot;
    private ModelPart muzzle, barrel, railed_handguard, handguard, body, stock, charge, cover, mag, grip, handle;
    private ModelPart s_scope;
    private ModelPart[] bullets;
    public M249Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.animation.json"));
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        muzzle = gun.getChild("muzzle").meshing();
        barrel = gun.getChild("barrel").meshing();
        railed_handguard = gun.getChild("railed_handguard").meshing();
        handguard = gun.getChild("handguard").meshing();
        body = gun.getChild("body").meshing();
        stock = gun.getChild("stock").meshing();
        charge = gun.getChild("charge").meshing();
        cover = gun.getChild("cover").meshing();
        mag = gun.getChild("mag").meshing();
        grip = gun.getChild("grip").meshing();
        handle = gun.getChild("handle").meshing();

        s_scope = cover.getChild("s_scope");
        this.bullets = new ModelPart[11];
        for (int i = 0; i < 11; i ++) {
            this.bullets[i] = mag.getChild("bullet_" + i).meshing();
        }
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer bodyVertex = context.getBuffer(RenderType.entityCutout(TEXTURE));
        BulletChainHandler.handleBulletChain(context, bullets, 2400L);
        context.renderIf(stock, bodyVertex, context.notHasStock());
        context.renderIfOrElse(handguard, railed_handguard, context.notHasHandguard(), bodyVertex);
        context.render(bodyVertex, barrel, body, charge, mag, grip, handle);
        context.renderIf(muzzle, bodyVertex, context.notHasMuzzle());
        bodyVertex = context.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        context.render(cover, bodyVertex);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        if (!context.notHasScope()) {
            context.pushPose().translateTo(cover).renderScopeAttachment(s_scope).popPose();
        }
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
            KeyframeAnimations.animate(this, shoot, Clients.lastShootMain(),1);
            AnimationHandler.INSTANCE.applyReload(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_scope".equals(modelSlotName) || super.hasSlot(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        if (name.equals("s_scope")) {
            handleGunTranslate(poseStack);
            cover.translateAndRotate(poseStack);
            s_scope.translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name);
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        gun.resetPose();
        camera.resetPose();
        handle.resetPose();
        mag.resetPoseAll();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        cover.resetPose();
        charge.resetPose();
    }

}
