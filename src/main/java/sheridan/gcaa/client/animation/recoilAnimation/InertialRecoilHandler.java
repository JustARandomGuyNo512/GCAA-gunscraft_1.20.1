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
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@OnlyIn(Dist.CLIENT)
public class InertialRecoilHandler {
    private final ReentrantLock lock = new ReentrantLock();
    public static final InertialRecoilHandler INSTANCE = new InertialRecoilHandler();
    private final AtomicReference<InertialRecoilData> data = new AtomicReference<>(null);
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private static final float UP_FACTOR = 0.08f;
    private static final float BACK_FACTOR = 0.18f;
    private static final float ROTATE_FACTOR = 0.025f;

    private static SplittableRandom SPLITTABLE_RANDOM = new SplittableRandom();
    private static int randomIndexX = SPLITTABLE_RANDOM.nextBoolean() ? 1 : -1;
    private static int randomIndexY = SPLITTABLE_RANDOM.nextBoolean() ? 1 : -1;

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
    private float shake;

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
            poseStack.mulPose(new Quaternionf().rotateXYZ(-r0, r1, 0));
            poseStack.translate(0, - up * UP_FACTOR * scaleY - 0, back * BACK_FACTOR * scaleZ);
        }
    }

    static float shakeRotIndex = 0.6f;
    public void onShoot(InertialRecoilData data, float randomDirectionX, float randomDirectionY, float pRate,  float yRate)  {
        if (data == null) {
            clear();
        } else {
            try {
                this.lock.lock();
                this.lastBackOld = this.lastBack;
                this.lastBack = this.back;
                float unstableFactor = Mth.clamp(Mth.clamp((1.0F - (this.lastBack - this.lastBackOld)) *
                        Math.min(this.back, 1.0F + Math.min(0.0F, this.back - 1.0F) * data.back / data.backDec * 0.158F), 0.0F, 1.016F),
                        0.385F, 1.025F);
                this.randomYSpeed += data.randomY * Mth.clamp(unstableFactor, 0.385F, 1.0F) * randomDirectionY;
                if (this.randomYSpeed < 0.0F) {
                    this.randomYSpeed *= 0.6F;
                }

                yRate *= unstableFactor;
                this.randomXSpeed = (float)((double)this.randomXSpeed + (double)(data.randomX * randomDirectionX) * (0.75D + Math.random() * 0.5D) * (double)yRate);
                this.backSpeed += data.back * pRate;
                this.rotateSpeed += data.rotate * pRate * unstableFactor;
                this.upSpeed += data.up;
                Arrays.fill(this.finished, false);
                if (!data.isCanMix()) {
                    this.data.set(data);
                }

                this.enabled.set(true);
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
        shake = 0;
        lastBackOld = 0;
        Arrays.fill(finished, false);
        this.data.set(null);
        this.enabled.set(!disable);
        shakeRotIndex = 0.6f;
    }


    private boolean shouldClear(float speed, float val) {
        return Math.abs(speed) <= 0.001 && Math.abs(val) <= 0.00075;
    }

    private float lastBack;
    private float lastBackOld;
    private float shakeSpeed = 0, antiShake = 0.03f;
    public void update() {
        if (enabled.get() && this.data.get() != null) {
            try {
                lock.lock();
                InertialRecoilData recoilData = data.get();
                if (!finished[0] && (back != 0 || backSpeed != 0)) {
                    back += backSpeed;
                    if (back > 0) {
                        if (Clients.isInAds()) {
                            float adsFactor = Clients.getAdsProgress();
                            backSpeed -= back * (recoilData.backDec * (1 + Mth.clamp(adsFactor * adsFactor, 0, 0.8f)));
                        } else {
                            backSpeed -= back * recoilData.backDec;
                        }
                        if (backSpeed < 0) {
                            backSpeed *= 0.66f;
                        }
                    } else {
                        backSpeed -= backSpeed > 0 ?
                                back * recoilData.backDec * 1.7f :
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

//                if (!finished[1] && (up != 0 || upSpeed != 0)) {
//                    up += upSpeed;
//                    upSpeed -= up * recoilData.upDec;
//                    upSpeed *= 0.4f;
//                }
//                if (shouldClear(upSpeed, up)) {
//                    upSpeed = up = 0;
//                    finished[1] = true;
//                }

                if (!finished[2] && (rotate != 0 || rotateSpeed != 0)) {
                    rotate += rotateSpeed * 0.9f;
                    rotate *= 1 - Math.abs(recoilData.rotateDec / recoilData.rotate);
                    rotateSpeed *= 0.78f;
                }
                if (shouldClear(rotateSpeed, rotate)) {
                    rotateSpeed = rotate = 0;
                    finished[2] = true;
                }

                if (!finished[3] && (randomX != 0 || randomXSpeed != 0)) {
                    randomX += randomXSpeed * 0.47f;
                    randomXSpeed *= 0.85f;
                    randomX *= 0.8f;
                }
                if (shouldClear(randomXSpeed, randomX)) {
                    randomXSpeed = randomX = 0;
                    finished[3] = true;
                }

                if (!finished[4] && (randomY != 0 || randomYSpeed != 0)) {
                    randomY += randomYSpeed * 0.57f;
                    randomYSpeed *= 0.9f;
                    randomY *= 0.8f;
                }
                if (shouldClear(randomYSpeed, randomY)) {
                    randomYSpeed = randomY = 0;
                    finished[4] = true;
                }
//                shake += shakeSpeed * 0.4f;
//                shakeSpeed -= antiShake * shake;
//                shakeSpeed *= 0.8f;
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

    public static int randomIndexX(float changeFrequency) {
        if (SPLITTABLE_RANDOM.nextDouble() < changeFrequency) {
            randomIndexX *= -1;
        }
        return randomIndexX;
    }

    public static int randomIndexY(float changeFrequency) {
        if (SPLITTABLE_RANDOM.nextDouble() < changeFrequency) {
            randomIndexY *= -1;
        }
        return randomIndexY;
    }

    public static void flushRandomIndex() {
        randomIndexX = SPLITTABLE_RANDOM.nextBoolean() ? 1 : -1;
        randomIndexY = SPLITTABLE_RANDOM.nextBoolean() ? 1 : -1;
    }
}
