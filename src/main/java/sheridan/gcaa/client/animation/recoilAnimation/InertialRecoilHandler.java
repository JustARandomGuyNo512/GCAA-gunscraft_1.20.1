package sheridan.gcaa.client.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.Clients;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@OnlyIn(Dist.CLIENT)
public class InertialRecoilHandler {
    private final ReentrantLock lock = new ReentrantLock();
    public static final InertialRecoilHandler INSTANCE = new InertialRecoilHandler();
    private final AtomicReference<InertialRecoilData> data = new AtomicReference<>(null);
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private static final float UP_FACTOR = 0.09f;
    private static final float BACK_FACTOR = 0.18f;
    private static final float ROTATE_FACTOR = 0.025f;

    private float up;
    private float back;
    private float rotate;
    private float randomX;
    private float randomY;

    private float upSpeed;
    private float backSpeed;
    private float rotateSpeed;
    private float randomXSpeed;
    private float randomYSpeed;
    private final boolean[] finished = new boolean[] {false, false, false, false, false};

    public void applyTransform(PoseStack poseStack, InertialRecoilData data, boolean aiming) {
        if (data == null) {
            return;
        }
        if (this.data.get() == null) {
            this.data.set(data);
            return;
        } else {
            if (data.id != this.data.get().id) {
                clear(true);
                return;
            }
        }
        if (enabled.get()) {
            float scaleY = 1;
            float scaleZ = 1;
            float scaleRot = 1;
            Player player = Minecraft.getInstance().player;
            if (player != null && player.isCrouching()) {
                scaleRot *= 0.9f;
            }
            float adsProgress = Clients.MAIN_HAND_STATUS.adsProgress;
            if (aiming || adsProgress > 0) {
                scaleY = Mth.lerp(adsProgress, scaleY, data.aimingScaleUp);
                scaleZ = Mth.lerp(adsProgress, scaleZ, data.aimingBackScale);
                scaleRot = Mth.lerp(adsProgress, scaleRot, data.aimingRotateScale);
            }
            float r0 = (rotate + randomY) * scaleRot * ROTATE_FACTOR;
            float r1 = randomX * scaleRot * ROTATE_FACTOR;
            poseStack.translate(0, - up * UP_FACTOR * scaleY, back * BACK_FACTOR * scaleZ);
            poseStack.mulPose(new Quaternionf().rotateXYZ(- r0, r1, 0));
        }
    }

    public void onShoot(InertialRecoilData data, float randomDirectionX, float randomDirectionY, float pRate,  float yRate)  {
        if (data == null) {
            clear();
        } else {
            try {
                lock.lock();
                randomYSpeed += data.randomY * randomDirectionY ;
                if (randomYSpeed < 0) {
                    randomYSpeed *= 0.5f;
                }
                lastBackOld = lastBack;
                lastBack = back;
                float unstableFactor = Mth.lerp(
                        Mth.clamp((1 - (lastBack - lastBackOld)) *
                                        Math.min(back, 1 + (Math.min(0, back - 1) * ((data.back / data.backDec) * 0.158f))),
                                0, 1.016f),
                        0.385f, 1.025f);
                yRate *= unstableFactor;
                randomXSpeed += data.randomX * randomDirectionX * (0.75 + Math.random() * 0.5f) * yRate;
                backSpeed += data.back * pRate;
                rotateSpeed += data.rotate * pRate * unstableFactor;
                upSpeed += data.up;
                Arrays.fill(finished, false);
                if (!data.isCanMix()) {
                    this.data.set(data);
                }
                enabled.set(true);
            } finally {
                lock.unlock();
            }
        }
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean disable) {
        up = upSpeed = 0;
        back = backSpeed = 0;
        rotate = rotateSpeed = 0;
        randomX = randomXSpeed = 0;
        randomY = randomYSpeed = 0;
        lastBack = 0;
        lastBackOld = 0;
        Arrays.fill(finished, false);
        this.data.set(null);
        this.enabled.set(!disable);
    }


    private boolean shouldClear(float speed, float val) {
        return Math.abs(speed) <= 0.001 && Math.abs(val) <= 0.00075;
    }

    private float lastBack;
    private float lastBackOld;
    public void update() {
        if (enabled.get() && this.data.get() != null) {
            try {
                lock.lock();
                InertialRecoilData recoilData = data.get();
                if (!finished[0] && (back != 0 || backSpeed != 0)) {
                    back += backSpeed;
                    if (back > 0) {
                        backSpeed -= back * recoilData.backDec;
                        if (backSpeed < 0) {
                            backSpeed *= 0.58f;
                        }
                    } else {
                        backSpeed -= backSpeed > 0 ?
                                back * recoilData.backDec * 1.6f :
                                back * recoilData.backDec * 0.65f;
                        if (backSpeed < 0) {
                            backSpeed *= 0.35f;
                        }
                    }
                    backSpeed *= 0.81f;
                }
                if (shouldClear(backSpeed, back)) {
                    back = backSpeed = 0;
                    finished[0] = true;
                    lastBack = 0;
                    lastBackOld = 0;
                }

                if (!finished[1] && (up != 0 || upSpeed != 0)) {
                    upSpeed -= up * recoilData.upDec;
                    upSpeed *= 0.5f;
                    up += upSpeed * 0.7f;
                }
                if (shouldClear(upSpeed, up)) {
                    upSpeed = up = 0;
                    finished[1] = true;
                }

                if (!finished[2] && (rotate != 0 || rotateSpeed != 0)) {
                    rotateSpeed -= rotate * recoilData.rotateDec;
                    rotateSpeed *= 0.85f;
                    rotate += rotateSpeed * 0.575f;
                }
                if (shouldClear(rotateSpeed, rotate)) {
                    rotateSpeed = rotate = 0;
                    finished[2] = true;
                }

                if (!finished[3] && (randomX != 0 || randomXSpeed != 0)) {
                    randomX += randomXSpeed * 0.3f;
                    randomXSpeed *= 0.925f;
                    randomX *= 0.9f;
                }
                if (shouldClear(randomXSpeed, randomX)) {
                    randomXSpeed = randomX = 0;
                    finished[3] = true;
                }

                if (!finished[4] && (randomY != 0 || randomYSpeed != 0)) {
                    randomY += randomYSpeed * 0.3f;
                    randomYSpeed *= 0.925f;
                    randomY *= 0.92f;
                }
                if (shouldClear(randomYSpeed, randomY)) {
                    randomYSpeed = randomY = 0;
                    finished[4] = true;
                }

                boolean clear = true;
                for (boolean v : finished) {
                    if (!v) {
                        clear = false;
                        break;
                    }
                }
                if (clear) {
                    clear(true);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
