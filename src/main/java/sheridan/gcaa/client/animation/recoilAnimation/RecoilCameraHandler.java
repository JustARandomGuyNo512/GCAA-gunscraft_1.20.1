//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.frameAnimation.AnimationChannel;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.Keyframe;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.animation.frameAnimation.AnimationChannel.Interpolations;
import sheridan.gcaa.client.animation.frameAnimation.AnimationChannel.Targets;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition.Builder;
import sheridan.gcaa.client.model.modelPart.CubeListBuilder;
import sheridan.gcaa.client.model.modelPart.LayerDefinition;
import sheridan.gcaa.client.model.modelPart.MeshDefinition;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class RecoilCameraHandler {
    private static final ICameraShakeHandler DEFAULT_CAMERA_SHAKE_HANDLER = new RecoilCameraHandler.CameraShakeHandler();
    public static final RecoilCameraHandler INSTANCE = new RecoilCameraHandler();
    private ICameraShakeHandler cameraShakeHandler;
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private Player player;
    private float pitchSpeed;
    private float yawSpeed;
    private float pitchControl;
    private float yawControl;
    public boolean disable = false;

    protected RecoilCameraHandler() {
        this.cameraShakeHandler = DEFAULT_CAMERA_SHAKE_HANDLER;
    }

    public void onShoot(float pitchVec, float yawVec, float pitchControl, float yawControl) {
        if (disable) {
            return;
        }
        if (pitchVec > 0.0F) {
            this.pitchSpeed = pitchVec;
            this.pitchControl = pitchControl;
            this.enabled.set(true);
        }

        if (Math.abs(yawVec) != 0.0F) {
            this.yawSpeed += yawVec;
            this.yawControl = yawControl;
            this.enabled.set(true);
        }

    }

    public void onShoot(IGun gun, ItemStack itemStack, float dx, Player player, float pitchScale, float yawScale) {
        float pitchVec = gun.getRecoilPitch(itemStack);
        float yawVec = gun.getRecoilYaw(itemStack) * dx;
        float controlScale = player.isCrouching() ? 1.2F : 1.0F;
        this.onShoot(pitchVec, yawVec, gun.getRecoilPitchControl(itemStack) * 0.2F * controlScale * pitchScale, gun.getRecoilYawControl(itemStack) * 0.2F * controlScale * yawScale);
    }

    public void handle() {
        if (this.player != null && this.player.getId() == Clients.clientPlayerId && this.enabled.get()) {
            this.pitchSpeed -= this.pitchControl;
            this.yawSpeed = this.yawSpeed > 0.0F ? this.yawSpeed - this.yawControl : this.yawSpeed + this.yawControl;
            this.pitchSpeed *= Mth.clamp(1.0F - this.pitchControl * 5.0F, 0.8F, 0.9F);
            this.yawSpeed *= Mth.clamp(1.0F - this.yawControl * 5.0F, 0.8F, 0.9F);
            float scale = (Clients.isInAds() ? 1.0F - Clients.getAdsProgress() * 0.25F : 1.0F) * 0.2F;
            this.player.setXRot(this.player.getXRot() - this.pitchSpeed * scale);
            this.player.setYRot(this.player.getYRot() + this.yawSpeed * scale);
            if (this.pitchSpeed < 0.25F && Math.abs(this.yawSpeed) < 0.25F || this.pitchSpeed < 0.0F) {
                this.clear();
            }
        } else {
            this.player = Minecraft.getInstance().player;
        }

    }

    public void clear() {
        this.pitchSpeed = 0.0F;
        this.yawSpeed = 0.0F;
        this.pitchControl = 0.0F;
        this.yawControl = 0.0F;
        this.enabled.set(false);
    }

    public ICameraShakeHandler getCameraShakeHandler() {
        return this.cameraShakeHandler;
    }

    public void setCameraShakeHandler(ICameraShakeHandler cameraShakeHandler) {
        this.cameraShakeHandler.clear();
        this.cameraShakeHandler = cameraShakeHandler;
    }

    public void useDefaultCameraShakeHandler() {
        this.cameraShakeHandler.clear();
        this.cameraShakeHandler = DEFAULT_CAMERA_SHAKE_HANDLER;
    }

    @OnlyIn(Dist.CLIENT)
    private static class CameraShakeHandler implements ICameraShakeHandler {
        private static final ModelPart CAMERA = createBodyLayer().bakeRoot().getChild("root");
        private static final AnimationDefinition RECOIL_SHAKE;

        private CameraShakeHandler() {
        }

        private static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            meshdefinition.getRoot().addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 2), PartPose.offset(0.0F, 0.0F, 0.0F));
            return LayerDefinition.create(meshdefinition, 0, 0);
        }

        public static void applyToPose(PoseStack poseStack) {
            if (CAMERA.xRot != 0.0F || CAMERA.yRot != 0.0F || CAMERA.zRot != 0.0F) {
                poseStack.mulPose((new Quaternionf()).rotationZYX(CAMERA.zRot, CAMERA.yRot, CAMERA.xRot));
            }

            if (CAMERA.xScale != 1.0F || CAMERA.yScale != 1.0F || CAMERA.zScale != 1.0F) {
                poseStack.scale(CAMERA.xScale, CAMERA.yScale, CAMERA.zScale);
            }

        }

        public static void reset() {
            CAMERA.resetPose();
        }

        public void use() {
        }

        public void clear() {
            reset();
        }

        public boolean shake(float particleTicks, PoseStack poseStack, IGun gun, Player player, ItemStack itemStack) {
            if (System.currentTimeMillis() - Clients.lastShootMain() < 1000L) {
                KeyframeAnimations._animateToModelPart(CAMERA, RECOIL_SHAKE, Clients.lastShootMain(), 0L, -0.5F, -0.5F, Clients.MAIN_HAND_STATUS.lastRecoilDirection * -0.5F, true);
                applyToPose(poseStack);
                reset();
            }

            return false;
        }

        static {
            RECOIL_SHAKE = Builder.withLength(0.3333F).addAnimation("root", new AnimationChannel(Targets.ROTATION, new Keyframe[]{new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR), new Keyframe(0.0333F, KeyframeAnimations.degreeVec(-0.8F, 0.0F, 0.7F), Interpolations.CATMULLROM), new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.5F, 0.0F, -0.5F), Interpolations.CATMULLROM), new Keyframe(0.1583F, KeyframeAnimations.degreeVec(-0.3F, 0.0F, 0.3F), Interpolations.CATMULLROM), new Keyframe(0.2333F, KeyframeAnimations.degreeVec(0.1F, 0.0F, -0.2F), Interpolations.CATMULLROM), new Keyframe(0.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)})).build();
        }
    }
}
