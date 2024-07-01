package sheridan.gcaa.animation.recoilAnimation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class InertialRecoilHandler {
    public static final InertialRecoilHandler INSTANCE = new InertialRecoilHandler();
    private final AtomicReference<InertialRecoilData> data = new AtomicReference<>(null);
    private final AtomicBoolean enabled = new AtomicBoolean(false);

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

    public void applyTransform(PoseStack poseStack, int id, boolean aiming) {

    }

    public void onShoot(InertialRecoilData data, int randomDirectionX, int randomDirectionY) {
        if (data == null) {
            clear();
        } else {
            float[] scaleArgs = new float[] {1f, 1f};

            this.data.set(data);
        }
    }

    public void clear() {

    }


    public void update(float delta) {
        if (data.get() != null) {

        }
    }
}
