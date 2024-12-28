package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.NewPlayerArmRenderer;
import sheridan.gcaa.client.render.RenderTypes;

@OnlyIn(Dist.CLIENT)
public class Mk47Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.png");

    private ModelPart barrel;
    private ModelPart muzzle;
    private ModelPart stock;
    private ModelPart charge;
    private ModelPart body;
    private ModelPart safety;
    private ModelPart grip;
    private ModelPart handguard;
    private ModelPart mag;
    private ModelPart slide;
    private ModelPart IS;
    private ModelPart IS_front;
    private ModelPart bullet;

    private ModelPart rail_left, rail_left_rear, rail_right,
            rail_right_rear, rail_lower, rail_lower_rear;

    private final AnimationDefinition shoot;

    public Mk47Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/mk47/mk47.animation.json"));
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        stock = gun.getChild("stock").meshing();
        charge = gun.getChild("charge").meshing();
        body = gun.getChild("body").meshing();
        safety = gun.getChild("safety").meshing();
        grip = gun.getChild("grip").meshing();
        handguard = gun.getChild("handguard").meshing();
        mag = gun.getChild("mag").meshing();
        slide = gun.getChild("slide").meshing();
        IS = gun.getChild("IS").meshing();
        IS_front = gun.getChild("IS_front").meshing();
        IS.getChild("sight").meshing();
        IS_front.getChild("sight_front").meshing();
        bullet = mag.getChild("bullet").meshing();
        rail_left = handguard.getChild("rail_left").meshing();
        rail_left_rear = handguard.getChild("rail_left_rear").meshing();
        rail_right = handguard.getChild("rail_right").meshing();
        rail_right_rear = handguard.getChild("rail_right_rear").meshing();
        rail_lower = handguard.getChild("rail_lower").meshing();
        rail_lower_rear = handguard.getChild("rail_lower_rear").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        bullet.visible = context.shouldBulletRender();
        context.renderIf(muzzle, vertexConsumer, context.notHasMuzzle());
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        context.renderIf(grip, vertexConsumer, context.notHasGrip());
        context.renderIf(stock, vertexConsumer, context.notHasStock());
        context.renderIf(vertexConsumer, context.notContainsScope(), IS, IS_front);
        context.render(vertexConsumer, barrel, charge, body, safety, slide);
        if (context.notHasHandguard()) {
            handleRailsVisible(context);
            vertexConsumer = context.getBuffer(RenderTypes.getCutOutNoCullMipmap(TEXTURE));
            context.render(vertexConsumer, handguard);
        }
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    private void handleRailsVisible(GunRenderContext context) {
        rail_left.visible = context.has("handguard_left");
        rail_left_rear.visible = context.has("handguard_left_rear");
        rail_right.visible = context.has("handguard_right");
        rail_right_rear.visible = context.has("handguard_right_rear");
        rail_lower.visible = context.has("handguard_lower");
        rail_lower_rear.visible = context.has("handguard_grip");
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderMagAttachmentIf(mag, !context.notHasMag());
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_mag".equals(modelSlotName) || super.hasSlot(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name) {
        if (name.equals("s_mag")) {
            handleGunTranslate(poseStack);
            mag.translateAndRotate(poseStack);
            return;
        }
        super.handleSlotTranslate(poseStack, name);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        defaultAssaultRifleAnimation(context, shoot);
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        gun.resetPose();
        root.resetPose();
        slide.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
        camera.resetPose();
        mag.resetPose();
        charge.resetPose();
    }
}
