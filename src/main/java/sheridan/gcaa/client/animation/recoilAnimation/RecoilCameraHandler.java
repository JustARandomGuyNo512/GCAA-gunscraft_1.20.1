package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
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
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.items.gun.IGun;

import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class RecoilCameraHandler {
    private static final ICameraShakeHandler DEFAULT_CAMERA_SHAKE_HANDLER = new CameraShakeHandler();
    public static final RecoilCameraHandler INSTANCE = new RecoilCameraHandler();
    private ICameraShakeHandler cameraShakeHandler;
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private Player player;
    private float pitchSpeed;
    private float yawSpeed;
    private float pitchControl;
    private float yawControl;
    private IGun gun;
    private float lastRecoil = 0;

    protected RecoilCameraHandler() {
        cameraShakeHandler = DEFAULT_CAMERA_SHAKE_HANDLER;
    }

    public void onShoot(float pitchVec, float yawVec, float pitchControl, float yawControl) {
        if (pitchVec > 0) {
            pitchSpeed = pitchVec;
            this.pitchControl = pitchControl;
            lastRecoil = pitchVec;
            enabled.set(true);
            doRecover = false;
        }
        if (Math.abs(yawVec) != 0) {
            yawSpeed += yawVec;
            this.yawControl = yawControl;
            enabled.set(true);
            doRecover = false;
        }
    }

    public void onShoot(IGun gun, ItemStack itemStack, float dx, Player player, float pitchScale, float yawScale) {
        float pitchVec = gun.getRecoilPitch(itemStack);
        float yawVec = gun.getRecoilYaw(itemStack) * dx;
        float controlScale = player.isCrouching() ? 1.2f : 1f;
        this.gun = gun;
        onShoot(pitchVec, yawVec,
                gun.getRecoilPitchControl(itemStack) * 0.2f * controlScale * pitchScale,
                gun.getRecoilYawControl(itemStack) * 0.2f * controlScale * yawScale);
    }

    private float lastPitch = Float.NaN;
    private boolean doRecover = false;
    private float recoverPitch = Float.NaN;
    public void handle() {
        if (player != null && player.getId() == Clients.clientPlayerId && enabled.get()) {
            if (!doRecover) {
                pitchSpeed -= pitchControl;
                yawSpeed = yawSpeed > 0 ? yawSpeed - yawControl : yawSpeed + yawControl;
                pitchSpeed *= Mth.clamp(1 - pitchControl * 5f, 0.8f, 0.9f);
                yawSpeed *= Mth.clamp(1 - yawControl * 5f, 0.8f, 0.9f);
                float scale = (Clients.isInAds() ? (1 - Clients.getAdsProgress() * 0.25f) : 1f) * 0.2f;
                if (Float.isNaN(lastPitch)) {
                    lastPitch = player.getXRot();
                }
                float pitchVec = pitchSpeed * scale;
                player.setXRot(player.getXRot() - pitchVec);
                player.setYRot(player.getYRot() + yawSpeed * scale);
                if (!Float.isNaN(lastPitch)) {
                    float dis = player.getXRot() - lastPitch;
                    float bound = dis / -25f;
                    bound *= 1 + pitchControl * (isPistol() ? 1 : 3f);
                    player.setXRot(player.getXRot() + pitchVec * bound);
                }
            }
            if (!doRecover && ((pitchSpeed < 0.25f && Math.abs(yawSpeed) < 0.25f) || pitchSpeed < 0)) {
                doRecover = true;
                recoverPitch = (player.getXRot() - lastPitch) * (Mth.clamp(pitchControl * 1.5f, 0, 0.5f));
            }
            if (doRecover) {
                if (recoverPitch > 0) {
                    clear();
                } else {
                    player.setXRot(player.getXRot() + recoverPitch * 0.05f);
                    recoverPitch *= 0.95f;
                }
            }
        } else {
            player = Minecraft.getInstance().player;
        }
    }

    private boolean isPistol() {
        return gun != null && gun.isPistol();
    }

    public float getLastRecoil() {
        return lastRecoil;
    }

    public void clear() {
        pitchSpeed = 0;
        yawSpeed = 0;
        pitchControl = 0;
        yawControl = 0;
        lastPitch = Float.NaN;
        recoverPitch = Float.NaN;
        doRecover = false;
        enabled.set(false);
    }

    public ICameraShakeHandler getCameraShakeHandler() {
        return cameraShakeHandler;
    }

    public void setCameraShakeHandler(ICameraShakeHandler cameraShakeHandler) {
        this.cameraShakeHandler.clear();
        this.cameraShakeHandler = cameraShakeHandler;
    }

    public void useDefaultCameraShakeHandler() {
        cameraShakeHandler.clear();
        cameraShakeHandler = DEFAULT_CAMERA_SHAKE_HANDLER;
    }

    @OnlyIn(Dist.CLIENT)
    private static class CameraShakeHandler implements ICameraShakeHandler {
        private static final ModelPart CAMERA = createBodyLayer().bakeRoot().getChild("root");

        private static final AnimationDefinition RECOIL_SHAKE = AnimationDefinition.Builder.withLength(0.3333F)
                .addAnimation("root", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.0333F, KeyframeAnimations.degreeVec(-0.8F, 0.0F, 0.7F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.0833F, KeyframeAnimations.degreeVec(0.5F, 0.0F, -0.5F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.1583F, KeyframeAnimations.degreeVec(-0.3F, 0.0F, 0.3F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.2333F, KeyframeAnimations.degreeVec(0.1F, 0.0F, -0.2F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                ))
                .build();


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

        @Override
        public void use() {

        }

        @Override
        public void clear() {
            reset();
        }

        @Override
        public boolean shake(float particleTicks, PoseStack poseStack, IGun gun, Player player, ItemStack itemStack) {
            if (System.currentTimeMillis() - Clients.lastShootMain() < 1000L) {
                KeyframeAnimations._animateToModelPart(CAMERA, RECOIL_SHAKE, Clients.lastShootMain(), 0, 0.5f, -0.5f, Clients.MAIN_HAND_STATUS.lastRecoilDirection * -0.5f, true);
                applyToPose(poseStack);
                float factor = (System.currentTimeMillis() - Clients.lastShootMain()) / 100f;
                factor = Mth.clamp(factor, 0, 1);
                factor = - Mth.sin((float) (Math.pow(factor, 0.28f) * Math.PI));
                poseStack.translate(0,0,
                        (Mth.clamp(INSTANCE.getLastRecoil(), 0, 3))
                                * factor * 0.01f);

                reset();
            }
            return false;
        }

    }

}
