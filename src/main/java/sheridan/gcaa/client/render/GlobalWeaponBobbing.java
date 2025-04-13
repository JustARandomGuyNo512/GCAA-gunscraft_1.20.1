package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.SprintingHandler;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class GlobalWeaponBobbing {
    private static final IWeaponBobbing DEFAULT = new DefaultBobbing();
    public static final GlobalWeaponBobbing INSTANCE = new GlobalWeaponBobbing();
    public IWeaponBobbing weaponBobbing;
    public float particleTicks = 0;
    public float equipProgress = 0;
    public float timer = 0;
    public long lastUpdate = System.currentTimeMillis();
    public static final float PI = 3.14159265358979323846f;
    public IGun gun;
    public LocalPlayer player;

    GlobalWeaponBobbing() {
        weaponBobbing = DEFAULT;
    }

    public void handleTranslation(PoseStack poseStack) {
        if (weaponBobbing != null && player != null && gun != null) {
            weaponBobbing.handleTranslation(poseStack, INSTANCE);
        }
    }

    public Vector2f getSwing() {
        return weaponBobbing == null ? null : weaponBobbing.getSwing();
    }

    public void update(float particleTicks, float equipProgress) {
        if (player != null) {
            if (player.getMainHandItem().getItem() instanceof IGun gun) {
                this.gun = gun;
                long now = System.currentTimeMillis();
                this.particleTicks = particleTicks;
                this.equipProgress = equipProgress;
                timer = (float) (now - lastUpdate) * 0.001f;
                lastUpdate = now;
            }
        } else {
            lastUpdate = System.currentTimeMillis();
        }
        player = Minecraft.getInstance().player;
    }

    public void setWeaponBobbing(IWeaponBobbing weaponBobbing) {
        this.weaponBobbing.clear();
        this.weaponBobbing = weaponBobbing;
        this.weaponBobbing.use();
    }

    public void useDefault() {
        this.weaponBobbing.clear();
        this.weaponBobbing = DEFAULT;
        this.weaponBobbing.use();
    }

    public interface IWeaponBobbing {
        void use();
        void clear();
        void handleTranslation(PoseStack poseStack, GlobalWeaponBobbing instance);
        Vector2f getSwing();
    }

    private static class DefaultBobbing implements IWeaponBobbing {
        private static final float EQUIP_HEIGHT = 3f;
        private float idleProgress = 0;
        float walkDis;
        float swing;
        float swingRx;
        float swingRy;
        float sprintingFactor;
        float scaleFactor;
        float idlePitch;
        float idleYaw;
        float idleRoll;
        private static float sprintingStartSwing = Float.NaN;

        @Override
        public void use() {
            InertialBobbingHandler.initInstance();
        }

        @Override
        public void clear() {
            idleProgress = 0;
            InertialBobbingHandler.clear();
        }

        @Override
        public void handleTranslation(PoseStack poseStack, GlobalWeaponBobbing instance) {
            IGun gun = instance.gun;
            LocalPlayer player = instance.player;
            idleProgress += instance.timer;
            if (idleProgress > PI * 2) {
                idleProgress = 0;
            }
            float idle = idleProgress * 2;
            float particleTick = instance.particleTicks;
            float sprintingProgress = SprintingHandler.INSTANCE.getLerpedSprintingProgress(particleTick);
            float aimingFactor = 1f - Clients.MAIN_HAND_STATUS.adsProgress * 0.75f;
            walkDis = player.walkDist - player.walkDistO;
            swing = -(player.walkDist + walkDis * particleTick) * PI;
            float bob = Mth.lerp(particleTick, player.oBob, player.bob);
            sprintingFactor = player.isSprinting() ? Math.min(bob * 10f, 1f) : 1f;
            scaleFactor = aimingFactor * (player.isSprinting() ? 1f + sprintingFactor * 0.3f : 1f);
            float idleScale = Math.min((System.currentTimeMillis() - Clients.lastShootMain()) * 0.1f, 0.8f) * scaleFactor * (player.isCrouching() ? 0.5f : 0.8f);
            float scaledBob = bob * scaleFactor * (1 - sprintingProgress);
            float pistolFactor = gun.isPistol() ? 0.5f : 1f;
            float zRot = InertialBobbingHandler.getXOffset() * scaleFactor * (pistolFactor * pistolFactor);
            float bobRY = Mth.rotLerp(particleTick, player.yBobO, player.yBob);
            float headRY = Mth.rotLerp(particleTick, player.yHeadRotO, player.yHeadRot);
            swingRy = Mth.clamp((headRY - bobRY) * 0.003f, -0.1f, 0.1f) * pistolFactor;
            float bobRX = Mth.rotLerp(particleTick, player.xBobO, player.xBob);
            float headRX = Mth.rotLerp(particleTick, player.xRotO, player.getXRot());
            swingRx = Mth.clamp((headRX - bobRX) * 0.003f, -0.1f, 0.1f) * pistolFactor;
            idlePitch = Mth.sin(idle + PI * 0.75f) * 0.0035f;
            idleYaw = Mth.sin(idle) * 0.01f;
            idleRoll = Mth.sin(idle) * 0.00025f;
            if (sprintingProgress != 1) {
                poseStack.translate(
                        -Mth.sin(swing - PI * 0.125f) * scaledBob * 0.115f + swingRy * scaleFactor,
                        (1.08f - Math.abs(Mth.cos(swing - PI * 0.1f))) * scaledBob * 0.25f +
                                EQUIP_HEIGHT * pistolFactor * instance.equipProgress - (swingRx * 0.5f - InertialBobbingHandler.getYOffset() * pistolFactor) * scaleFactor
                                + idleYaw * idleScale * pistolFactor,
                        scaledBob * 0.5f + idleRoll * idleScale);
                poseStack.mulPose(new Quaternionf().rotateXYZ(
                        -Math.abs(Mth.cos(swing - PI * 0.023F) * bob) * scaleFactor * 0.12f
                                + swingRx * 0.8f * aimingFactor - idlePitch * idleScale + instance.equipProgress * 0.2f,
                        swingRy * 0.9f * aimingFactor,
                        - swingRy * aimingFactor * pistolFactor + instance.equipProgress + zRot));
            }
            if (sprintingProgress != 0) {
                if (sprintingStartSwing == -123456789f) {
                    sprintingStartSwing = swing + PI;
                }
                swing -= sprintingStartSwing;
                float[] sprintingTrans = GunModelRegister.getDisplayData(gun).getSprintingTrans();
                float shakeScale = (gun.isPistol() ? 1.35f : 2.4f) * bob;
                float sin = Mth.sin(swing);
                poseStack.translate(
                        (sprintingTrans[0] - sin * shakeScale + swingRy * shakeScale * 30f) * sprintingProgress,
                        (sprintingTrans[1] + Math.abs(Mth.cos(swing - PI * 0.1f)) * shakeScale) * (sprintingProgress * sprintingProgress)
                                + idleYaw * idleScale,
                        sprintingTrans[2] * sprintingProgress + idleRoll * idleScale);
                shakeScale *= 0.5f;
                poseStack.mulPose(new Quaternionf().rotateXYZ(
                        (sprintingTrans[3] - Math.abs(sin) * shakeScale) * sprintingProgress - idlePitch * idleScale,
                        (sprintingTrans[4] + sin * shakeScale) * (RenderAndMathUtils.sLerp(sprintingProgress)),
                        sprintingTrans[5] * sprintingProgress));
            } else {
                sprintingStartSwing = -123456789f;
            }

        }

        @Override
        public Vector2f getSwing() {
            return new Vector2f(swingRx, swingRy);
        }

    }
}
