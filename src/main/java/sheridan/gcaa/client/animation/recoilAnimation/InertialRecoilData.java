package sheridan.gcaa.client.animation.recoilAnimation;

import com.google.gson.JsonObject;
import sheridan.gcaa.data.IDataPacketGen;

import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class InertialRecoilData implements IDataPacketGen {
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
    public float randomXChangeRate = 0.5f;
    public float randomYChangeRate = 0.5f;

    public InertialRecoilData(float up, float upDec, float back, float backDec, float rotate, float rotateDec, float randomX, float randomXChangeRate,
                              float randomY, float randomYChangeRate, float aimingScaleUp, float aimingBackScale, float aimingRotateScale) {
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
        this.randomXChangeRate = randomXChangeRate;
        this.randomYChangeRate = randomYChangeRate;
        this.id = TEMP_ID.getAndIncrement();
    }

    public InertialRecoilData(float up, float upDec, float back, float backDec, float rotate, float rotateDec, float randomX,
                              float randomY, float aimingScaleUp, float aimingBackScale, float aimingRotateScale) {
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
        this.randomXChangeRate = 0;
        this.canMix = true;
    }

    public boolean isCanMix() {
        return canMix;
    }

    @Override
    public void writeData(JsonObject jsonObject) {

    }

    @Override
    public void loadData(JsonObject jsonObject) {

    }
}
