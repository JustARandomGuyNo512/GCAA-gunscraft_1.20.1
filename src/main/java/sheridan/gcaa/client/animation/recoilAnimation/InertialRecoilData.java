package sheridan.gcaa.client.animation.recoilAnimation;

import com.google.gson.JsonObject;
import sheridan.gcaa.data.IJsonSyncable;

import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class InertialRecoilData implements IJsonSyncable {
    private static final AtomicInteger TEMP_ID = new AtomicInteger(0);
    public float xRotOffset;
    public float yRotOffset;
    public float back;
    public float backDec;
    public float rotate;
    public float rotateDec;
    public float aimingScaleUp;
    public float aimingBackScale;
    public float aimingRotateScale;
    public float randomX;
    public float randomY;
    public int id;
    private boolean canMix = false;
    public float randomXChangeRate = 0.5f;
    public float randomYChangeRate = 0.5f;

    public InertialRecoilData(float xRotOffset, float yRotOffset, float back, float backDec, float rotate, float rotateDec, float randomX, float randomXChangeRate,
                              float randomY, float randomYChangeRate, float aimingScaleUp, float aimingBackScale, float aimingRotateScale) {
        this.xRotOffset = xRotOffset / 16f;
        this.yRotOffset = yRotOffset / 16f;
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

    public InertialRecoilData(float xRotOffset, float yRotOffset, float back, float backDec, float rotate, float rotateDec, float randomX,
                              float randomY, float aimingScaleUp, float aimingBackScale, float aimingRotateScale) {
        this.xRotOffset = xRotOffset / 16f;
        this.yRotOffset = yRotOffset / 16f;
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

    public InertialRecoilData(float xRotOffset, float yRotOffset, float back, float backDec, float rotate, float rotateDec, float randomX, float randomY) {
        this.xRotOffset = xRotOffset / 16f;
        this.yRotOffset = yRotOffset / 16f;
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

    public InertialRecoilData() {
        this.id = TEMP_ID.getAndIncrement();
    }

    public boolean isCanMix() {
        return canMix;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty("xRotOffset", xRotOffset * 16f);
        jsonObject.addProperty("yRotOffset", yRotOffset * 16f);
        jsonObject.addProperty("back", back);
        jsonObject.addProperty("backDec", backDec);
        jsonObject.addProperty("rotate", rotate);
        jsonObject.addProperty("rotateDec", rotateDec);
        jsonObject.addProperty("aimingScaleUp", aimingScaleUp);
        jsonObject.addProperty("aimingBackScale", aimingBackScale);
        jsonObject.addProperty("aimingRotateScale", aimingRotateScale);
        jsonObject.addProperty("randomX", randomX);
        jsonObject.addProperty("randomY", randomY);
        jsonObject.addProperty("randomXChangeRate", randomXChangeRate);
        jsonObject.addProperty("randomYChangeRate", randomYChangeRate);
        jsonObject.addProperty("canMix", canMix);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        xRotOffset = jsonObject.get("xRotOffset").getAsFloat() / 16f;
        yRotOffset = jsonObject.get("yRotOffset").getAsFloat() / 16f;
        back = jsonObject.get("back").getAsFloat();
        backDec = jsonObject.get("backDec").getAsFloat();
        rotate = jsonObject.get("rotate").getAsFloat();
        rotateDec = jsonObject.get("rotateDec").getAsFloat();
        aimingScaleUp = jsonObject.get("aimingScaleUp").getAsFloat();
        aimingBackScale = jsonObject.get("aimingBackScale").getAsFloat();
        aimingRotateScale = jsonObject.get("aimingRotateScale").getAsFloat();
        randomX = jsonObject.get("randomX").getAsFloat();
        randomY = jsonObject.get("randomY").getAsFloat();
        randomXChangeRate = jsonObject.get("randomXChangeRate").getAsFloat();
        randomYChangeRate = jsonObject.get("randomYChangeRate").getAsFloat();
        canMix = jsonObject.get("canMix").getAsBoolean();
    }
}
