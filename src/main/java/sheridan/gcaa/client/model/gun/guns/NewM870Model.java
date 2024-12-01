package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.NewPlayerArmRenderer;

@OnlyIn(Dist.CLIENT)
public class NewM870Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.png");
    private ModelPart slide, handguard, stock, mag, barrel, body, reloading_arm, shell;
    private final AnimationDefinition recoil;

    public NewM870Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.geo.new.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m870/m870.animation.json"));
        recoil = animations.get("recoil");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        slide = gun.getChild("slide").meshing();
        handguard = gun.getChild("handguard").meshing();
        stock = gun.getChild("stock").meshing();
        mag = gun.getChild("mag").meshing();
        barrel = gun.getChild("barrel").meshing();
        body = gun.getChild("body").meshing();
        reloading_arm = gun.getChild("reloading_arm");
        shell = reloading_arm.getChild("shell").meshing();
        this.reloading_arm.resetChildLayerName("left_arm_slim2", "left_arm_slim");
        this.reloading_arm.resetChildLayerName("left_arm_normal2", "left_arm_normal");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        context.render(vertexConsumer, slide, handguard, stock, barrel, body);
        ModelPart leftArm = left_arm.xScale > 0 ? left_arm : reloading_arm;
        if (leftArm == reloading_arm && context.isFirstPerson) {
            context.pushPose().translateTo(reloading_arm).render(shell, vertexConsumer);
            context.popPose();
        }
        if (context.isFirstPerson) {
            NewPlayerArmRenderer.INSTANCE.renderByLayer(right_arm, 1, 1, 1, context.packedLight, context.packedOverlay, true, context.bufferSource, context.poseStack);
            NewPlayerArmRenderer.INSTANCE.renderByLayer(leftArm, 1, 1, 1, context.packedLight, context.packedOverlay, false, context.bufferSource, context.poseStack);
        }
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        if ("s_handguard".equals(name)) {
            handleGunTranslate(poseStack);
            handguard.translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name);
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return super.hasSlot(modelSlotName) || "s_handguard".equals(modelSlotName);
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
    protected void animationGlobal(GunRenderContext gunRenderContext) {
        if (gunRenderContext.isFirstPerson) {
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
    protected void afterRender(GunRenderContext gunRenderContext) {
        gun.resetPose();
        camera.resetPose();
        slide.resetPose();
        reloading_arm.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        shell.resetPose();
        handguard.resetPose();
    }
}