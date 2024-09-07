package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Python357Model extends GunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.png");
    private final AnimationDefinition recoil, reload;
    private ModelPart body, hammer, mag, reloading_arm, loader;

    public Python357Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/python_357/python_357.animation.json"));
        recoil = animations.get("recoil");
        reload = animations.get("reload");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        body = gun.getChild("body").meshing();
        hammer = gun.getChild("hammer").meshing();
        mag = gun.getChild("mag").meshing();
        reloading_arm = gun.getChild("reloading_arm");
        loader = reloading_arm.getChild("loader").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        context.render(vertexConsumer, body, hammer, mag);
        if (ReloadingHandler.isReloading()) {
            context.pushPose().translateTo(reloading_arm).render(loader, vertexConsumer);
            context.popPose();
        }
        ModelPart leftArm = left_arm.xScale == 0 ? reloading_arm : left_arm;
        context.renderArm(leftArm, false);
        context.renderArm(right_arm, true);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyRecoil(this);
            AnimationHandler.INSTANCE.applyReload(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        gun.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        reloading_arm.resetPoseAll();
        mag.resetPoseAll();
        camera.resetPose();
    }

    @Override
    public AnimationDefinition getRecoil() {
        return recoil;
    }

    @Override
    public AnimationDefinition getFullReload() {
        return reload;
    }
}
