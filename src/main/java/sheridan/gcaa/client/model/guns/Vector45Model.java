package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class Vector45Model extends GunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/vector_45/vector_45.png");
    private ModelPart safety, safety2, stock, front_IS, IS, handle, slide, mag, bullet0, exp_part, body, barrel, muzzle;
    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    public Vector45Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/vector_45/vector_45.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/vector_45/vector_45.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        safety = gun.getChild("safety").meshing();
        safety2 = gun.getChild("safety2").meshing();
        stock = gun.getChild("stock").meshing();
        front_IS = gun.getChild("front_IS").meshing();
        IS = gun.getChild("IS").meshing();
        handle = gun.getChild("handle").meshing();
        slide = gun.getChild("slide").meshing();
        mag = gun.getChild("mag").meshing();
        bullet0 = mag.getChild("bullet0").meshing();
        exp_part = mag.getChild("exp_part").meshing();
        body = gun.getChild("body").meshing();
        barrel = gun.getChild("barrel").meshing();
        muzzle = gun.getChild("muzzle").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        bullet0.visible = context.shouldBulletRender();
        exp_part.visible = !context.notHasMag();
        context.renderIf(stock, vertexConsumer, context.notHasStock());
        context.renderIf(front_IS, vertexConsumer, context.notHasScope());
        context.renderIf(IS, vertexConsumer, context.notHasScope());
        context.renderIf(muzzle, vertexConsumer, context.notHasMuzzle());
        context.render(vertexConsumer, safety, safety2, handle, slide, mag, body, barrel);
        context.renderArmLong(left_arm, false);
        context.renderArmLong(right_arm, true);
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
    protected void animationGlobal(GunRenderContext context) {
        defaultAssaultRifleAnimation(context, recoil, shoot);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        left_arm.resetPose();
        right_arm.resetPose();
        mag.resetPoseAll();
        handle.resetPoseAll();
        camera.resetPoseAll();
        slide.resetPose();
    }
}
