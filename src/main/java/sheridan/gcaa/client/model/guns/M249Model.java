package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class M249Model extends GunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.png");
    private final AnimationDefinition recoil;
    private ModelPart muzzle, barrel, railed_handguard, handguard, body, stock, charge, cover, mag, grip, handle;
    private ModelPart s_scope;
    private ModelPart[] bullets;
    public M249Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m249/m249.animation.json"));
        recoil = animations.get("recoil");
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
        handleBulletChain(context, bullets);
        context.render(bodyVertex, muzzle, barrel, handguard, body, stock, charge, mag, grip, handle);
        VertexConsumer coverVertex = context.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        context.render(cover, coverVertex);
        context.renderArmLong(left_arm, false);
        context.renderArmLong(right_arm, true);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        if (context.hasScope()) {
            context.pushPose().translateTo(cover).renderScope(s_scope).popPose();
        }
        context.renderAllAttachmentsLeft(gun);
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
            KeyframeAnimations.animate(this, recoil, Clients.lastShootMain(),1);
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
        left_arm.resetPose();
        right_arm.resetPose();
        cover.resetPose();
        charge.resetPose();
    }

    private void handleBulletChain(GunRenderContext context, ModelPart[] bullets) {
        boolean shouldHandleChainAnimation = context.isFirstPerson;
        boolean alwaysShowBullets = ReloadingHandler.getReloadingProgress() > 0.45f;
        int bulletLeft = context.ammoLeft;
        int count = bullets.length;
        float fireProgress = context.getFireProgress();
        for (int i = 0; i < count; i++) {
            bullets[i].visible = (count - i) <= bulletLeft || alwaysShowBullets;
            if (shouldHandleChainAnimation && (bulletLeft > i + 1) && fireProgress != 0) {
                ModelPart next = i + 1 < bullets.length ? bullets[i + 1] : null;
                if (next != null) {
                    translateBullet(fireProgress, bullets[i], next);
                }
            }
        }
    }

    private void translateBullet(float fireProgress, ModelPart prevBulletModel, ModelPart nextBulletModel) {
        PartPose partPose = nextBulletModel.getInitialPose();
        float x, y, z;
        x = (partPose.x - prevBulletModel.x) * fireProgress;
        y = (partPose.y - prevBulletModel.y) * fireProgress;
        z = (partPose.z - prevBulletModel.z) * fireProgress;
        prevBulletModel.x += x;
        prevBulletModel.y += y;
        prevBulletModel.z += z;
    }
}
