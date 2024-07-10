package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.guns.IGun;

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
    public Player player;

    GlobalWeaponBobbing() {
        weaponBobbing = DEFAULT;
    }

    public void handleTranslation(PoseStack poseStack) {
        if (weaponBobbing != null && player != null && gun != null) {
            weaponBobbing.handleTranslation(poseStack, INSTANCE);
        }
    }

    public void update(float particleTicks, float equipProgress) {
        if (player != null && gun !=null) {
            this.particleTicks = particleTicks;
            this.equipProgress = equipProgress;
            long now = System.currentTimeMillis();
            timer = (float) (now - lastUpdate) * 0.001f;
            lastUpdate = now;
        } else {
            player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof IGun gun) {
                this.gun = gun;
            } else {
                gun = null;
            }
        }
    }

    public void setWeaponBobbing(IWeaponBobbing weaponBobbing) {
        this.weaponBobbing.clear();
        this.weaponBobbing = weaponBobbing;
    }

    public void useDefault() {
        this.weaponBobbing.clear();
        this.weaponBobbing = DEFAULT;
    }

    public interface IWeaponBobbing {
        void clear();
        void handleTranslation(PoseStack poseStack, GlobalWeaponBobbing instance);
    }

    private static class DefaultBobbing implements IWeaponBobbing {
        private static final float EQUIP_HEIGHT = 1.5f;
        private float idleProgress = 0;

        @Override
        public void clear() {
            idleProgress = 0;
        }

        @Override
        public void handleTranslation(PoseStack poseStack, GlobalWeaponBobbing instance) {
            IGun gun = instance.gun;
            Player player = instance.player;
            float equipProgress = instance.equipProgress;
            float aimingFactor = Clients.mainHandStatus.ads ? 0.25f : 1f;
            float scaleFactor = aimingFactor * (player.isCrouching() ? 0.5f : 1f);

            poseStack.translate(0, EQUIP_HEIGHT * equipProgress, 0);
            idleProgress += instance.timer;
            float pitch = Mth.sin(idleProgress + PI * 0.75f) * 0.0035f;
            float yaw = Mth.sin(idleProgress) * 0.015f;
            float roll = Mth.sin(idleProgress) * 0.01f;
            float lastFireDis = (System.currentTimeMillis() - Clients.mainHandStatus.lastShoot) * 0.001f;
            scaleFactor = Math.min(lastFireDis, 1f) * scaleFactor;
            float yFactor = gun.isPistol() ? 0.5f : 1f;
            poseStack.translate(0, yaw * scaleFactor * yFactor, roll * 0.025f * scaleFactor);
            poseStack.mulPose(new Quaternionf().rotateXYZ(-pitch * scaleFactor, 0, 0));
            if (idleProgress >= PI) {
                idleProgress = 0;
            }

        }

    }
}
