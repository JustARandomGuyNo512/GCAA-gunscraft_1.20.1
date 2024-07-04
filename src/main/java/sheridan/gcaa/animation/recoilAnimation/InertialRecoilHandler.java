package sheridan.gcaa.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class InertialRecoilHandler {
    public static final InertialRecoilHandler INSTANCE = new InertialRecoilHandler();
    private final AtomicReference<InertialRecoilData> data = new AtomicReference<>(null);
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private static final float UP_FACTOR = 0.09f;
    private static final float BACK_FACTOR = 0.18f;
    private static final float ROTATE_FACTOR = 0.025f;

    private volatile float up;
    private volatile float back;
    private volatile float rotate;
    private volatile float randomX;
    private volatile float randomY;

    private float upSpeed;
    private float backSpeed;
    private float rotateSpeed;
    private float randomXSpeed;
    private float randomYSpeed;
    private long startTime;

    public void applyTransform(PoseStack poseStack, int id, boolean aiming) {
        if (data.get() == null) {
            return;
        } else {
            if (id != this.data.get().id) {
                clear(true);
                return;
            }
        }
        if (enabled.get()) {
//            float scaleY = 1;
//            float scaleZ = 1;
            float scaleRot = 1;
            Player player = Minecraft.getInstance().player;
            if (player != null && player.isCrouching()) {
                scaleRot = Math.max(0.2f, scaleRot - 0.1f);
            }
            float r0 = (rotate + randomY) * scaleRot * ROTATE_FACTOR;
            float r1 = randomX * scaleRot * ROTATE_FACTOR;

            poseStack.translate(0, -up * UP_FACTOR, back * BACK_FACTOR);
            poseStack.mulPose(new Quaternionf().rotateXYZ(- r0, r1, 0));
        }
    }

    public void onShoot(InertialRecoilData data, float randomDirectionX, float randomDirectionY) {
        if (data == null) {
            clear();
        } else {
            randomYSpeed += data.randomY * randomDirectionY;
            if (randomYSpeed < 0) {
                randomYSpeed *= 0.2f;
            }
            randomXSpeed += data.randomX * randomDirectionX;
            startTime = System.currentTimeMillis();
            backSpeed += data.back;
            rotateSpeed += data.rotate;
            upSpeed += data.up;
            this.data.set(data);
            enabled.set(true);
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
        this.data.set(null);
        this.enabled.set(!disable);
    }


    private boolean shouldClear(float speed, float val) {
        return Math.abs(speed) <= 0.0001 && Math.abs(val) <= 0.0001;
    }

    public void update() {
        if (enabled.get() && this.data.get() != null) {
            InertialRecoilData recoilData = data.get();
            back += backSpeed;
            if (back > 0) {
                backSpeed -= back * recoilData.backDesc;
                if (backSpeed < 0) {
                    backSpeed *= 0.55f;
                }
            } else {
                backSpeed -= backSpeed > 0 ? back * recoilData.backDesc * 1.6f : back * recoilData.backDesc * 0.65f;
                if (backSpeed < 0) {
                    backSpeed *= 0.35f;
                }
            }
            backSpeed *= 0.8f;
            if (shouldClear(backSpeed, back)) {
                back = backSpeed = 0;
            }

            upSpeed -= up * recoilData.upDesc;
            upSpeed *= 0.5f;
            up += upSpeed * 0.7f;
            if (shouldClear(upSpeed, up)) {
                upSpeed = up = 0;
            }

            rotateSpeed -= rotate * recoilData.rotateDesc;
            rotateSpeed *= 0.7f;
            rotate += rotateSpeed;
            if (shouldClear(rotateSpeed, rotate)) {
                rotateSpeed = rotate = 0;
            }

            randomX += randomXSpeed * 0.3f;
            randomXSpeed *= 0.925f;
            randomX *= 0.92f;
            if (shouldClear(randomXSpeed, randomX)) {
                randomXSpeed = randomX = 0;
            }

            randomY += randomYSpeed * 0.3f;
            randomYSpeed *= 0.925f;
            randomY *= 0.92f;
            if (shouldClear(randomYSpeed, randomY)) {
                randomYSpeed = randomY = 0;
            }

            if (System.currentTimeMillis() - startTime > 1000) {
                clear(true);
            }
        }
    }
}
