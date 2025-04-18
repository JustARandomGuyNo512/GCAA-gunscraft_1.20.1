package sheridan.gcaa.client.animation.recoilAnimation;

import com.google.gson.JsonObject;
import sheridan.gcaa.data.IJsonSyncable;

import java.util.concurrent.atomic.AtomicInteger;


public class InertialRecoilData implements IJsonSyncable {
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
    public int id;
    private boolean canMix = false;
    public float randomXChangeRate = 0.5f;
    public float randomYChangeRate = 0.5f;
    public Shake shake;

    public InertialRecoilData(float up, float upDec, float back, float backDec, float rotate, float rotateDec, float randomX, float randomXChangeRate,
                              float randomY, float randomYChangeRate, float aimingScaleUp, float aimingBackScale, float aimingRotateScale) {
        this.up = up ;
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

    public InertialRecoilData() {
        this.id = TEMP_ID.getAndIncrement();
    }

    public InertialRecoilData shake(float size, float xRotScaleFactor, float yRotScaleFactor, float period, float adsRotZScale, float adsRotYScale, float adsRotXScale) {
        this.shake = new Shake(size, xRotScaleFactor, yRotScaleFactor, period, adsRotZScale, adsRotYScale, adsRotXScale);
        return this;
    }

    public boolean isCanMix() {
        return canMix;
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty("xRotOffset", up * 16f);
        jsonObject.addProperty("yRotOffset", upDec * 16f);
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
        if (shake != null) {
            JsonObject shakeObject = new JsonObject();
            shake.writeData(shakeObject);
            jsonObject.add("shake", shakeObject);
        }
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        up = jsonObject.get("xRotOffset").getAsFloat() / 16f;
        upDec = jsonObject.get("yRotOffset").getAsFloat() / 16f;
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
        if (jsonObject.has("shake")) {
            shake = Shake.create();
            shake.loadData(jsonObject.get("shake").getAsJsonObject());
        }
    }

    public static class Shake implements IJsonSyncable{
        public float size;
        public float xRotScaleFactor;
        public float yRotScaleFactor;
        public float period;
        public float adsRotZScale;
        public float adsRotYScale;
        public float adsRotXScale;

        public Shake(float size, float xRotScaleFactor, float yRotScaleFactor, float period, float adsRotZScale, float adsRotYScale, float adsRotXScale) {
            this.size = size;
            this.xRotScaleFactor = xRotScaleFactor;
            this.yRotScaleFactor = yRotScaleFactor;
            this.period = period;
            this.adsRotZScale = adsRotZScale;
            this.adsRotYScale = adsRotYScale;
            this.adsRotXScale = adsRotXScale;
        }

        private Shake() {}

        public static Shake create() {
            return new Shake();
        }

        @Override
        public void writeData(JsonObject jsonObject) {
            jsonObject.addProperty("size", size);
            jsonObject.addProperty("xRotScaleFactor", xRotScaleFactor);
            jsonObject.addProperty("yRotScaleFactor", yRotScaleFactor);
            jsonObject.addProperty("period", period);
            jsonObject.addProperty("adsRotZScale", adsRotZScale);
            jsonObject.addProperty("adsRotYScale", adsRotYScale);
            jsonObject.addProperty("adsRotXScale", adsRotXScale);
        }

        @Override
        public void loadData(JsonObject jsonObject) {
            size = jsonObject.get("size").getAsFloat();
            xRotScaleFactor = jsonObject.get("xRotScaleFactor").getAsFloat();
            yRotScaleFactor = jsonObject.get("yRotScaleFactor").getAsFloat();
            period = jsonObject.get("period").getAsFloat();
            adsRotZScale = jsonObject.get("adsRotZScale").getAsFloat();
            adsRotYScale = jsonObject.get("adsRotYScale").getAsFloat();
            adsRotXScale = jsonObject.get("adsRotXScale").getAsFloat();
        }
    }
}
