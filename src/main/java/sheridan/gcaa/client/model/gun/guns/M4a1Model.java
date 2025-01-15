package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.AutoMagPositionModel;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.NewPlayerArmRenderer;

@OnlyIn(Dist.CLIENT)
public class M4a1Model extends AutoMagPositionModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.png");
    private ModelPart barrel, front_IS, handguard, muzzle, stock, charge, body, safety, bolt, IS, grip, ring, mag, bullet;
    private final AnimationDefinition shoot;

    public M4a1Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/m4a1/m4a1.animation.json"));
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        front_IS = gun.getChild("front_IS").meshing();
        handguard = gun.getChild("handguard").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        stock = gun.getChild("stock").meshing();
        charge = gun.getChild("charge").meshing();
        body = gun.getChild("body").meshing();
        safety = gun.getChild("safety").meshing();
        bolt = gun.getChild("bolt").meshing();
        IS = gun.getChild("IS").meshing();
        grip = gun.getChild("grip").meshing();
        ring = gun.getChild("ring").meshing();
        mag = gun.getChild("mag").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        bullet.visible = context.shouldBulletRender();
        context.renderIf(IS, vertexConsumer, context.notContainsScope());
        context.renderIf(muzzle, vertexConsumer, context.notHasMuzzle());
        context.renderIf(front_IS, vertexConsumer, !context.has("gas_block"));
        context.renderIf(handguard, vertexConsumer, context.notHasHandguard());
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        context.renderIf(stock, vertexConsumer, context.notHasStock());
        context.render(vertexConsumer, barrel, charge, body, safety, bolt, grip, ring);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        super.renderAttachmentsModel(context);
        context.renderAllAttachmentsLeft(gun);
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
    protected ModelPart getMag() {
        return mag;
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        root.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        mag.resetPose();
        bolt.resetPose();
        camera.resetPose();
    }
}