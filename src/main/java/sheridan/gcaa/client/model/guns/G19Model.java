package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
@OnlyIn(Dist.CLIENT)
public class G19Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.png");
    private ModelPart body, slide, mag, barrel, bullet;
    private ModelPart slot_scope;

    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    public G19Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        body = gun.getChild("body").meshing();
        slide = gun.getChild("slide").meshing();
        mag = gun.getChild("mag").meshing();
        bullet = mag.getChild("bullet").meshing();

        slot_scope = slide.getChild("s_scope");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        bullet.visible = context.shouldBulletRender();
        context.render(vertexConsumer, barrel, slide, body, mag);
        if (context.shouldShowLeftArm()) {
            context.renderArm(left_arm, false);
        }
        context.renderArm(right_arm, true);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        if (context.hasScope()) {
            context.pushPose().translateTo(slide).renderScope(slot_scope).popPose();
        }
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson || context.isThirdPerson()) {
            if (context.isFirstPerson) {
                AnimationHandler.INSTANCE.applyRecoil(this);
                if (!ReloadingHandler.isReloadingGeneric()) {
                    AnimationHandler.INSTANCE.applyReload(this);
                    CameraAnimationHandler.INSTANCE.mix(camera);
                }
            }
            KeyframeAnimations.animate(this, shoot, Clients.lastShootMain(), 1);
        }
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
        mag.resetPose();
        camera.resetPose();
    }

    @Override
    public AnimationDefinition getRecoil() {
        return recoil;
    }

}
