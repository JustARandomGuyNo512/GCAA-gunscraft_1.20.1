package sheridan.gcaa.items.attachments;

public abstract class Scope extends Sight {
    public final float maxMagnification;
    public final float minMagnification;
    public final float adsSpeedRate;

    public Scope(int order, float maxMagnification, float minMagnification, float adsSpeedRate)    {
        super(order);
        this.maxMagnification = maxMagnification;
        this.minMagnification = minMagnification;
        this.adsSpeedRate = adsSpeedRate;
    }

    public float aimingModelFovModify() {
        return 7.5f;
    }
}
