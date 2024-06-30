package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.Clients;

@OnlyIn(Dist.CLIENT)
public class GlobalWeaponBobbing {
    public static final GlobalWeaponBobbing INSTANCE = new GlobalWeaponBobbing();
    public IWeaponBobbing weaponBobbing;
    public float particleTicks = 0;
    public float equipProgress = 0;
    public float timer = 0;
    public long lastUpdate = System.currentTimeMillis();
    public static final float PI = 3.14159265358979323846f;

    GlobalWeaponBobbing() {
        weaponBobbing = new DefaultBobbingController();
    }

    public void handleTranslation(PoseStack poseStack) {
        if (weaponBobbing != null) {
            weaponBobbing.handleTranslation(poseStack, INSTANCE);
        }
    }

    public void update(float particleTicks, float equipProgress) {
        this.particleTicks = particleTicks;
        this.equipProgress = equipProgress;
        long now = System.currentTimeMillis();
        timer = (float) (now - lastUpdate) * 0.001f;
        lastUpdate = now;
    }

    public interface IWeaponBobbing {
        void handleTranslation(PoseStack poseStack, GlobalWeaponBobbing globalWeaponBobbing);
    }

    public static abstract class BobbingController implements IWeaponBobbing {
        protected float idleProgress = 0;

        @Override
        public void handleTranslation(PoseStack poseStack, GlobalWeaponBobbing globalWeaponBobbing) {
            float equipProgress = globalWeaponBobbing.equipProgress;
            float scaleFactor = getScaleFactor();
            float aimingFactor = getAimingFactor();
            handleEquipProgress(poseStack, equipProgress);
            idleProgress += globalWeaponBobbing.timer;
            handleIdleMove(poseStack, idleProgress * 2, globalWeaponBobbing.particleTicks, scaleFactor);
            if (idleProgress >= PI) {
                idleProgress = 0;
            }
        }

        abstract float getAimingFactor();
        abstract float getScaleFactor();
        abstract void handleEquipProgress(PoseStack poseStack, float equipProgress);
        abstract void handleIdleMove(PoseStack poseStack, float idleProgress, float particleTicks, float scaleFactor);
    }

    private static class DefaultBobbingController extends BobbingController {
        private static final float EQUIP_HEIGHT = 1.5f;

        @Override
        float getAimingFactor() {
            return Clients.mainHandStatus.ads ? 0.25f : 1f;
        }

        @Override
        float getScaleFactor() {
            return getAimingFactor();
        }

        @Override
        void handleEquipProgress(PoseStack poseStack, float equipProgress) {
            poseStack.translate(0, EQUIP_HEIGHT * equipProgress, 0);
        }

        @Override
        void handleIdleMove(PoseStack poseStack, float idleProgress, float particleTicks, float scaleFactor) {
            float pitch = Mth.sin(idleProgress + PI * 0.75f) * 0.0035f;
            float yaw = Mth.sin(idleProgress) * 0.015f;
            float roll = Mth.sin(idleProgress) * 0.01f;
            poseStack.translate(0, yaw, roll * 0.025f);
            poseStack.mulPose(new Quaternionf().rotateXYZ(-pitch, 0, 0));
        }
    }
}
