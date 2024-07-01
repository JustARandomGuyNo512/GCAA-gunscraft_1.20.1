package sheridan.gcaa.animation.recoilAnimation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicInteger;

@OnlyIn(Dist.CLIENT)
public class InertialRecoilData {
    private static final AtomicInteger TEMP_ID = new AtomicInteger(0);
    public float up;
    public float upDesc;
    public float back;
    public float backDesc;
    public float rotate;
    public float rotateDesc;
    public float aimingScaleUp;
    public float aimingBackScale;
    public float aimingRotateScale;
    public float randomX;
    public float randomY;
    public final int id;

    public InertialRecoilData(float up, float upDesc, float back, float backDesc, float rotate, float rotateDesc, float randomX, float randomY, Vector3f aimingScaleModifier) {
        this.up = up;
        this.upDesc = upDesc;
        this.back = back;
        this.backDesc = backDesc;
        this.rotate = rotate;
        this.rotateDesc = rotateDesc;
        this.aimingScaleUp = aimingScaleModifier.x;
        this.aimingBackScale = aimingScaleModifier.y;
        this.aimingRotateScale = aimingScaleModifier.z;
        this.randomX = randomX;
        this.randomY = randomY;
        this.id = TEMP_ID.getAndIncrement();
    }


}
