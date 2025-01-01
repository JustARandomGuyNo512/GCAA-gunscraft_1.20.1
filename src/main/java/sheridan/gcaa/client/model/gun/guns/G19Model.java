package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.LodGunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
@OnlyIn(Dist.CLIENT)
public class G19Model extends LodGunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.png");
    private final ResourceLocation TEXTURE_LOW = new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19_low.png");
    private ModelPart body, slide, mag, barrel, bullet;
    private ModelPart slot_scope;

    private ModelPart body_low, slide_low, mag_low, barrel_low;

    private AnimationDefinition recoil;
    private AnimationDefinition shoot;

    public G19Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19_low.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.animation.json"));
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        body = gun.getChild("body").meshing();
        slide = gun.getChild("slide").meshing();
        mag = gun.getChild("mag").meshing();
        bullet = mag.getChild("bullet").meshing();
        slot_scope = slide.getChild("s_scope");

        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInitLowQuality(ModelPart lowQualityGun, ModelPart lowQualityRoot) {
        barrel_low = lowQualityGun.getChild("barrel").meshing();
        body_low = lowQualityGun.getChild("body").meshing();
        slide_low = lowQualityGun.getChild("slide").meshing();
        mag_low = lowQualityGun.getChild("mag").meshing();
    }

    @Override
    protected void renderGunNormal(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        bullet.visible = context.shouldBulletRender();
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        if (context.isFirstPerson && context.ammoLeft == 0 && !ReloadingHandler.isReloading()) {
            slide.z += 5;
        }
        context.render(vertexConsumer, barrel, slide, body);
        if (context.shouldShowLeftArm()) {
            context.renderArmOldStylePistol(left_arm, false);
        }
        context.renderArmOldStylePistol(right_arm, true);
    }

    @Override
    protected void renderGunLow(GunRenderContext context) {
        slide_low.copyFrom(slide);
        barrel_low.copyFrom(barrel);
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE_LOW));
        context.renderIf(mag_low, vertexConsumer, context.notHasMag());
        context.render(vertexConsumer, barrel_low, slide_low, body_low);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        if (!context.notHasScope()) {
            context.pushPose().translateTo(slide).renderScopeAttachment(slot_scope).popPose();
        }
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
        defaultPistolAnimation(context, shoot);
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return ("s_mag".equals(modelSlotName) || "s_scope".equals(modelSlotName)) || super.hasSlot(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        if (name.equals("s_mag")) {
            handleGunTranslate(poseStack);
            mag.translateAndRotate(poseStack);
            return;
        }
        if (name.equals("s_scope")) {
            handleGunTranslate(poseStack);
            slide.translateAndRotate(poseStack);
            slot_scope.translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name);
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        slide.resetPose();
        barrel.resetPose();
        gun.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        mag.resetPose();
        camera.resetPose();
        if (getShouldRenderLowQuality(gunRenderContext)) {
            slide_low.resetPose();
            barrel_low.resetPose();
        }
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return recoil;
    }

}
