package sheridan.gcaa.client.model.gun;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class GlockModel extends AutoMagPositionModel {
    protected ModelPart bullet, slide, barrel;
    protected ModelPart slide_low_quality, barrel_low_quality, mag_low_quality;
    protected float chargeSlideBack, chargeBarrelBack, chargeBarrelDown, chargeBarrelRot;

    public GlockModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                      @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture,
                      float chargeSlideBack, float chargeBarrelBack, float chargeBarrelDown, float chargeBarrelRot) {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
        this.chargeSlideBack = chargeSlideBack;
        this.chargeBarrelBack = chargeBarrelBack;
        this.chargeBarrelDown = chargeBarrelDown;
        this.chargeBarrelRot = (float) Math.toRadians(chargeBarrelRot);
    }

    public GlockModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                      float chargeSlideBack, float chargeBarrelBack, float chargeBarrelDown, float chargeBarrelRot) {
        this(modelPath, animationPath, texture, null, null, chargeSlideBack, chargeBarrelBack, chargeBarrelDown, chargeBarrelRot);
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        bullet = mag.getChild("bullet");
        slide = main.getChild("slide");
        barrel = main.getChild("barrel");
        if (lowQualityLoaded) {
            slide_low_quality = lowQualityMain.getChild("slide");
            barrel_low_quality = lowQualityMain.getChild("barrel");
            mag_low_quality = lowQualityMain.getChild("mag");
        }
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = getDefaultVertex(context);
        bullet.visible = context.shouldBulletRender();
        mag.visible = context.notHasMag();
        if (context.isFirstPerson && context.ammoLeft == 0 && !ReloadingHandler.isReloading()) {
            slide.setZ(slide.getInitialPose().z + chargeSlideBack);
            barrel.addZ(chargeBarrelBack);
            barrel.addY(chargeBarrelDown);
            barrel.addxRot(-chargeBarrelRot);
        }
        context.render(vertexConsumer, main);
        if (context.shouldShowLeftArm()) {
            context.renderArmOldStylePistol(left_arm, false);
        }
        context.renderArmOldStylePistol(right_arm, true);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        defaultAnimation(context);
    }

    @Override
    protected void renderGunModelLowQuality(GunRenderContext context) {
        slide_low_quality.copyFrom(slide);
        barrel_low_quality.copyFrom(barrel);
        VertexConsumer vertexConsumer = getDefaultVertexLow(context);
        mag_low_quality.visible = context.notHasMag();
        context.render(vertexConsumer, lowQualityMain);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        super.afterRender(context);
        if (getShouldRenderLowQuality(context)) {
            slide_low_quality.resetPose();
            barrel_low_quality.resetPose();
        }
    }
}
