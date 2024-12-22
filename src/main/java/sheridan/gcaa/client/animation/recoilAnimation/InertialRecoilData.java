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
    private boolean canMix = false;
    public float holdingOffset;

    public InertialRecoilData(float up, float upDec, float back, float backDec, float rotate, float rotateDec, float randomX, float randomY,
                              float aimingScaleUp, float aimingBackScale, float aimingRotateScale) {
        this.up = up;
        this.upDec = upDec;
        this.back = back;
        this.backDec = backDec;
        this.rotate = rotate;
        this.rotateDec = rotateDec;
        this.aimingScaleUp = aimingScaleUp;
        this.aimingBackScale = aimingBackScale;
        this.aimingRotateScale = aimingRotateScale;
        this.randomX = randomX;
        this.randomY = randomY;
        this.holdingOffset = holdingOffset * 0.0625f;
        this.id = TEMP_ID.getAndIncrement();
    }

    public InertialRecoilData(float up, float upDec, float back, float backDec, float rotate, float rotateDec, float randomX, float randomY) {
        this.up = up;
        this.upDec = upDec;
        this.back = back;
        this.backDec = backDec;
        this.rotate = rotate;
        this.rotateDec = rotateDec;
        this.aimingScaleUp = 1;
        this.aimingBackScale = 1;
        this.aimingRotateScale = 1;
        this.randomX = randomX;
        this.randomY = randomY;
        this.id = TEMP_ID.getAndIncrement();
        this.holdingOffset = 0;
        this.canMix = true;
    }

    public boolean isCanMix() {
        return canMix;
    }

}
