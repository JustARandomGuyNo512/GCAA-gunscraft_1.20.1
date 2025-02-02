package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.BulletChainHandler;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class M60E4Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.png");
    AnimationDefinition shoot;
    ModelPart main, cover, handle, s_scope, muzzle, IS;
    ModelPart[] bullets;

    public M60E4Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m60e4/m60e4.animation.json"));
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        main = gun.getChild("main");
        main.meshingAll();
        cover = main.getChild("cover");
        s_scope = cover.getChild("s_scope");
        handle = main.getChild("handle");
        ModelPart mag = main.getChild("mag");
        this.bullets = new ModelPart[9];
        for (int i = 0; i < 9; i ++) {
            this.bullets[i] = mag.getChild("bullet_" + (i + 1)).meshing();
        }
        IS = main.getChild("IS");
        muzzle = main.getChild("muzzle");
        shoot = animations.get("shoot");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer bodyVertex = context.solid(TEXTURE);
        muzzle.visible = context.notHasMuzzle();
        BulletChainHandler.handleBulletChain(context, bullets, 2400L);
        IS.xRot = context.notContainsScope() ? 0 : 1.57079632679489655f;
        context.render(main, bodyVertex);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return main.hasChild(modelSlotName) || "s_scope".equals(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name, IGun iGunInstance) {
        if ("s_scope".equals(name)) {
            handleGunTranslate(poseStack);
            main.translateAndRotate(poseStack);
            cover.translateAndRotate(poseStack);
            s_scope.translateAndRotate(poseStack);
        } else {
            ModelPart slot = main.getChild(name);
            handleGunTranslate(poseStack);
            main.translateAndRotate(poseStack);
            slot.translateAndRotate(poseStack);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        main.translateAndRotate(context.poseStack);
        if (!context.notHasScope()) {
            context.pushPose().translateTo(cover).renderScopeAttachment(s_scope).popPose();
        }
        context.renderAllAttachmentsLeft(main);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        defaultAssaultRifleAnimation(context, shoot);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        left_arm.resetPose();
        right_arm.resetPose();
        camera.resetPose();
        root.resetPose();
        gun.resetPoseAll();
    }
}
