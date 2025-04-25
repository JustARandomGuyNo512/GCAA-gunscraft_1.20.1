package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AutoShotGunModel extends GunModel {
    private ModelPart reloading_arm, shell;
    public AutoShotGunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture, @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
    }

    public AutoShotGunModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture) {
        super(modelPath, animationPath, texture);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        reloading_arm = main.getChild("reloading_arm");
        shell = reloading_arm.getChild("shell");
        processReloadingArm();
    }

    protected void processReloadingArm() {
        if (reloading_arm.hasChild("left_arm_slim2")) {
            reloading_arm.resetChildLayerName("left_arm_slim2", "left_arm_slim");
            reloading_arm.addChild("right_arm_slim", this.reloading_arm.getChild("left_arm_slim"));
        }
        if (reloading_arm.hasChild("left_arm_normal2")) {
            reloading_arm.resetChildLayerName("left_arm_normal2", "left_arm_normal");
            reloading_arm.addChild("right_arm_normal", this.reloading_arm.getChild("left_arm_normal"));
        }
        if (reloading_arm.hasChild("right_arm_slim2")) {
            reloading_arm.resetChildLayerName("right_arm_slim2", "right_arm_slim");
            reloading_arm.addChild("left_arm_slim", this.reloading_arm.getChild("right_arm_slim"));
        }
        if (reloading_arm.hasChild("right_arm_normal2")) {
            reloading_arm.resetChildLayerName("right_arm_normal2", "right_arm_normal");
            reloading_arm.addChild("left_arm_normal", this.reloading_arm.getChild("right_arm_normal"));
        }
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = getDefaultVertex(context);
        ModelPart leftArm = left_arm.xScale > 1e-6 ? left_arm : reloading_arm;
        ModelPart rightArm = right_arm.xScale > 1e-6 ? right_arm : reloading_arm;
        if (context.isFirstPerson && (leftArm == reloading_arm ||rightArm == reloading_arm)) {
            reloading_arm.visible = false;
            context.pushPose().translateTo(reloading_arm).render(shell, vertexConsumer);
            context.popPose();
        }
        if (!context.isFirstPerson) {
            reloading_arm.visible = false;
        }
        context.render(main, vertexConsumer);
        if (context.isFirstPerson) {
            context.renderArm(rightArm, true);
            context.renderArm(leftArm, false);
        }
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            KeyframeAnimations.animate(this, shoot, context.lastShoot,1);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    protected void renderGunModelLowQuality(GunRenderContext context) {}

}
