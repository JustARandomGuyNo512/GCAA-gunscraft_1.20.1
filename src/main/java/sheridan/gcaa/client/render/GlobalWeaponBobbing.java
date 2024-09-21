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
import sheridan.gcaa.items.gun.IGun;

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
    public float sprintingProgress;
    public boolean shouldApplySprintingTranslation = false;
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
                shouldApplySprintingTranslation = player.isSprinting() && now - Clients.lastShootMain() > gun.applySprintingPoseDelay();
                if (shouldApplySprintingTranslation) {
                    sprintingProgress = Math.min(sprintingProgress + timer, 1.0f);
                } else {
                    if (sprintingProgress != 0) {
                        sprintingProgress = Math.max(sprintingProgress - timer, 0f);
                    }
                }
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


        @Override
        public void use() {
            JumpBobbingHandler.initInstance();
        }

        @Override
        public void clear() {
            idleProgress = 0;
            JumpBobbingHandler.clear();
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
            float aimingFactor = 1f - Clients.mainHandStatus.adsProgress * 0.75f;
            walkDis = player.walkDist - player.walkDistO;
            swing = -(player.walkDist + walkDis * particleTick) * PI;
            float bob = Mth.lerp(particleTick, player.oBob, player.bob);
            sprintingFactor = player.isSprinting() ? Math.min(bob * 10f, 1f) : 1f;
            scaleFactor = aimingFactor * (player.isSprinting() ? 1f + sprintingFactor * 0.3f : 1f);
            float idleScale = Math.min((System.currentTimeMillis() - Clients.lastShootMain()) * 0.001f, 1f) * scaleFactor * (player.isCrouching() ? 0.7f : 1f);
            float scaledBob = bob * scaleFactor;
            float pistolFactor = gun.isPistol() ? 0.5f : 1f;
            float bobRY = Mth.rotLerp(particleTick, player.yBobO, player.yBob);
            float headRY = Mth.rotLerp(particleTick, player.yHeadRotO, player.yHeadRot);
            swingRy = Mth.clamp((headRY - bobRY) * 0.003f, -0.1f, 0.1f) * pistolFactor;
            float bobRX = Mth.rotLerp(particleTick, player.xBobO, player.xBob);
            float headRX = Mth.rotLerp(particleTick, player.xRotO, player.getXRot());
            swingRx = Mth.clamp((headRX - bobRX) * 0.003f, -0.1f, 0.1f) * pistolFactor;
            idlePitch = Mth.sin(idle + PI * 0.75f) * 0.0035f;
            idleYaw = Mth.sin(idle) * 0.015f;
            idleRoll = Mth.sin(idle) * 0.00025f;
            poseStack.translate(
                    -Mth.sin(swing - PI * 0.125f) * scaledBob * 0.115f + swingRy * scaleFactor,
                    (1.08f - Math.abs(Mth.cos(swing - PI * 0.1f))) * scaledBob * 0.25f +
                            EQUIP_HEIGHT * pistolFactor * instance.equipProgress - (swingRx * 0.5f - JumpBobbingHandler.getOffset() * pistolFactor) * scaleFactor
                            + idleYaw * idleScale * pistolFactor,
                    scaledBob * 0.5f + idleRoll * idleScale);
            poseStack.mulPose(new Quaternionf().rotateXYZ(
                    -Math.abs(Mth.cos(swing - PI * 0.023F) * bob) * scaleFactor * 0.12f
                            + swingRx * 0.8f * aimingFactor - idlePitch * idleScale + instance.equipProgress * 0.2f,
                    swingRy * 0.9f * aimingFactor,
                    -swingRy * aimingFactor * pistolFactor + instance.equipProgress));



        }

        @Override
        public Vector2f getSwing() {
            return new Vector2f(swingRx, swingRy);
        }

    }
}
