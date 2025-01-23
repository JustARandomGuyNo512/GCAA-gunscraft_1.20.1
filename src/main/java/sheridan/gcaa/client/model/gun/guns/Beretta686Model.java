package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.guns.Beretta686;

@OnlyIn(Dist.CLIENT)
public class Beretta686Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/beretta_686/beretta_686.png");
    private ModelPart grip, body, stock, s_rail_clamp;

    private final AnimationDefinition recoil;
    private final AnimationDefinition recoil_volley;

    public Beretta686Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/beretta_686/beretta_686.geo.json"),
                new ResourceLocation(GCAA.MODID, "model_assets/guns/beretta_686/beretta_686.animation.json"));
        recoil = animations.get("recoil");
        recoil_volley = animations.get("recoil_volley");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        gun.meshingAll();
        grip = gun.getChild("grip");
        ModelPart barrel = grip.getChild("barrel_main");
        ModelPart bullets = barrel.getChild("bullets");
        bullets.getChild("shells_1").getChild("shell_1");
        bullets.getChild("shells_1").getChild("shell_1_fired");
        bullets.getChild("shells_2").getChild("shell_2");
        bullets.getChild("shells_2").getChild("shell_2_fired");
        body = gun.getChild("body");
        stock = gun.getChild("stock");
        s_rail_clamp = grip.getChild("s_rail_clamp");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.solid(TEXTURE);
        context.render(vertexConsumer, grip, body, stock);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        AttachmentRenderEntry rail_clamp = context.getAttachmentRenderEntry("s_rail_clamp");
        if (rail_clamp != null) {
            context.pushPose().translateTo(grip).renderEntry(rail_clamp, s_rail_clamp);
            context.popPose();
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_rail_clamp".equals(modelSlotName);
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String name, IGun iGunInstance) {
        handleGunTranslate(poseStack);
        grip.translateAndRotate(poseStack);
        s_rail_clamp.translateAndRotate(poseStack);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        float scale = 1;
        if (context.isFirstPerson && System.currentTimeMillis() - context.lastShoot < 100) {
            if (Clients.MAIN_HAND_STATUS.fireMode instanceof Beretta686.Volley && context.ammoLeft >= 1) {
                scale *= 1.8f;
            }
        }
        context.renderMuzzleFlash(scale);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson) {
            AnimationHandler.INSTANCE.applyReload(this);
            AnimationHandler.INSTANCE.applyRecoil(this);
            CameraAnimationHandler.INSTANCE.mix(camera);
        }
    }

    @Override
    public AnimationDefinition getRecoil(GunRenderContext context) {
        return (Clients.MAIN_HAND_STATUS.fireMode instanceof Beretta686.Volley && context.ammoLeft >= 1) ? recoil_volley : recoil;
    }

    @Override
    protected void afterRender(GunRenderContext context) {
        grip.resetPoseAll();
        stock.resetPoseAll();
        camera.resetPose();
        gun.resetPose();
        root.resetPose();
        left_arm.resetPoseAll();
        right_arm.resetPoseAll();
    }
}
