package sheridan.gcaa.client.animation.recoilAnimation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicInteger;

@OnlyIn(Dist.CLIENT)
public class InertialRecoilData {
    private static final AtomicInteger TEMP_ID = new AtomicInteger(0);
    public float up;
    public float upDec;
    public float back;
    public float backDec;
    public float rotate;
    public float rotateDec;
    public float aimingScaleUp;
    public float aimingBackScale;
    public float aimingRotateScale;
    public float randomX;
    public float randomY;
    public final int id;

    public InertialRecoilData(float up, float upDec, float back, float backDec, float rotate, float rotateDec, float randomX, float randomY, Vector3f aimingScaleModifier) {
        this.up = up;
        this.upDec = upDec;
        this.back = back;
        this.backDec = backDec;
        this.rotate = rotate;
        this.rotateDec = rotateDec;
        this.aimingScaleUp = aimingScaleModifier.x;
        this.aimingBackScale = aimingScaleModifier.y;
        this.aimingRotateScale = aimingScaleModifier.z;
        this.randomX = randomX;
        this.randomY = randomY;
        this.id = TEMP_ID.getAndIncrement();
    }


}
