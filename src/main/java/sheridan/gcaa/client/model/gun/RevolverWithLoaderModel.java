package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class RevolverWithLoaderModel extends GunModel {
    private final int magSize;
    private ModelPart reloading_arm;
    private ModelPart drum, loader;
    private final ModelPart[] unFiredBullets;
    private final ModelPart[] firedBullets;
    private final float drumRotSub;
    private final float hammerChargeRot;
    private ModelPart hammer;

    public RevolverWithLoaderModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                                   @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture,
                                   int magSize, float hammerChargeRot) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
        this.magSize = magSize;
        unFiredBullets = new ModelPart[magSize];
        firedBullets = new ModelPart[magSize];
        this.drumRotSub = (float) Math.toRadians(360f / magSize);
        this.hammerChargeRot = (float) Math.toRadians(hammerChargeRot);
        loadBullets();
    }

    public RevolverWithLoaderModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                                   int magSize, float hammerChargeRot) {
        super(modelPath, animationPath, texture);
        this.magSize = magSize;
        unFiredBullets = new ModelPart[magSize];
        firedBullets = new ModelPart[magSize];
        this.drumRotSub = (float) Math.toRadians(360f / magSize);
        this.hammerChargeRot = (float) Math.toRadians(hammerChargeRot);
        loadBullets();
    }

    protected void loadBullets() {
        ModelPart bullets = drum.getChild("bullets");
        for (int i = 0; i < magSize; i++) {
            this.firedBullets[i] = bullets.getChild("bullet" + i + "_fired");
            this.unFiredBullets[i] = bullets.getChild("bullet" + i );
        }
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        ModelPart mag = main.getChild("mag");
        drum = mag.getChild("drum");
        reloading_arm = main.getChild("reloading_arm");
        loader = reloading_arm.getChild("loader");
        if (main.hasChild("hammer")) {
            hammer = main.getChild("hammer");
        }
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = getDefaultVertex(context);
        if (context.isFirstPerson) {
            handleBulletsVisible(context);
            handleChargeAnimation(context);
        }
        reloading_arm.visible = false;
        loader.visible = false;
        context.render(vertexConsumer, main);
        if (ReloadingHandler.isReloading() && context.isFirstPerson) {
            loader.visible = true;
            context.pushPose().translateTo(reloading_arm).render(loader, vertexConsumer);
            context.popPose();
        }
        ModelPart leftArm = left_arm.xScale == 0 ? reloading_arm : left_arm;
        if (context.shouldShowLeftArm()) {
            context.renderArmOldStylePistol(leftArm, false);
        }
        context.renderArmOldStylePistol(right_arm, true);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    private void handleChargeAnimation(GunRenderContext context) {
        if (ReloadingHandler.isReloading()) {
            return;
        }
        float chargeProgress = Clients.MAIN_HAND_STATUS.getLerpedChargeTick(Minecraft.getInstance().getPartialTick());
        int ammoLeft = context.ammoLeft;
        if (hammer != null && chargeProgress != 0) {
            hammer.xRot = -Mth.lerp(chargeProgress, 0, hammerChargeRot);
            hammer.setTouched();
        }
        drum.zRot = -Mth.lerp(chargeProgress, (magSize - ammoLeft) * drumRotSub, (magSize - ammoLeft + 1) * drumRotSub);
        drum.setTouched();
    }

    private void handleBulletsVisible(GunRenderContext context) {
        int ammoLeft = context.ammoLeft;
        for (int i = magSize - 1; i >= 0; i --) {
            unFiredBullets[i].visible = ammoLeft > i;
            firedBullets[i].visible = !unFiredBullets[i].visible;
        }
    }

    @Override
    protected void renderGunModelLowQuality(GunRenderContext context) {
        VertexConsumer defaultVertexLow = getDefaultVertexLow(context);
        context.render(lowQualityMain, defaultVertexLow);
    }

    @Override
    public AnimationDefinition getFullReload() {
        return getReload();
    }
}
