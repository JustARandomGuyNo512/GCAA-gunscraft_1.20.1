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
public class MGModel extends GunModel {
    protected ModelPart[] bullets;
    protected ModelPart cover, mag;
    private final int chainSize;
    private final boolean disableCoverFaceCulling;
    private final int reloadingShowBulletsDelay;
    private AnimationDefinition shoot;

    public MGModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                   @Nullable ResourceLocation lowQualityModelPath, @Nullable ResourceLocation lowQualityTexture,
                   int chainSize, int reloadingShowBulletsDelay, boolean disableCoverFaceCulling)  {
        super(modelPath, animationPath, texture, lowQualityModelPath, lowQualityTexture);
        this.chainSize = chainSize;
        this.reloadingShowBulletsDelay = reloadingShowBulletsDelay;
        this.disableCoverFaceCulling = disableCoverFaceCulling;
        initChain();
    }

    public MGModel(ResourceLocation modelPath, ResourceLocation animationPath, ResourceLocation texture,
                    int chainSize, int reloadingShowBulletsDelay, boolean disableCoverFaceCulling)    {
        super(modelPath, animationPath, texture);
        this.chainSize = chainSize;
        this.reloadingShowBulletsDelay = reloadingShowBulletsDelay;
        this.disableCoverFaceCulling = disableCoverFaceCulling;
        initChain();
    }

    @Override
    protected void postInit(ModelPart main, ModelPart gun, ModelPart root) {
        super.postInit(main, gun, root);
        mag = main.getChild("mag");
        cover = main.getChild("cover");
        shoot = animations.get("shoot");
    }

    protected void initChain() {
        bullets = new ModelPart[chainSize];
        for (int i = 0; i < chainSize; i ++) {
            this.bullets[i] = mag.getChild("bullet_" + i);
        }
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer bodyVertex = getDefaultVertex(context);
        BulletChainHandler.handleBulletChain(context, bullets, reloadingShowBulletsDelay);
        cover.visible = false;
        context.render(main, bodyVertex);
        if (disableCoverFaceCulling) {
            bodyVertex = context.solidNoCull(texture);
        }
        cover.visible = true;
        context.render(cover, bodyVertex);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            KeyframeAnimations.animate(this, shoot, Clients.lastShootMain(),1);
            AnimationHandler.INSTANCE.applyReload(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    protected void renderGunModelLowQuality(GunRenderContext context) {

    }
}
