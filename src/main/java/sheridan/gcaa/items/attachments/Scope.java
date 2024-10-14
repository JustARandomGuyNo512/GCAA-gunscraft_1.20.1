package sheridan.gcaa.items.attachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import sheridan.gcaa.Clients;

public abstract class Scope extends Sight {
    public final float maxMagnification;
    public final float minMagnification;
    public final float adsSpeedRate;

    public Scope(float maxMagnification, float minMagnification, float adsSpeedRate, float weight)    {
        super(weight);
        this.maxMagnification = maxMagnification;
        this.minMagnification = minMagnification;
        this.adsSpeedRate = adsSpeedRate;
    }

    private double tempMouseSensitivity = -1;

    public void handleMouseSensitivity() {
        float aimingProgress = Clients.mainHandStatus.getLerpAdsProgress(Math.pow(Minecraft.getInstance().getPartialTick(), 3));
        if (Clients.isInAds()) {
            if (tempMouseSensitivity == -1) {
                tempMouseSensitivity = Minecraft.getInstance().options.sensitivity().get();
            } else {
                float rate = Clients.mainHandStatus.getScopeMagnificationRate();
                float prevMagnification = Mth.lerp(rate, maxMagnification, minMagnification);
                double prevMouseSensitivity = Mth.lerp(aimingProgress, tempMouseSensitivity,
                        tempMouseSensitivity * (1 / (prevMagnification)));
                Minecraft.getInstance().options.sensitivity().set(prevMouseSensitivity);
            }
        } else {
            if (tempMouseSensitivity != -1) {
                Minecraft.getInstance().options.sensitivity().set(tempMouseSensitivity);
            }
            tempMouseSensitivity = -1;
        }
    }

    public void onLoseEffective() {
        if (tempMouseSensitivity != -1) {
            Minecraft.getInstance().options.sensitivity().set(tempMouseSensitivity);
        }
        tempMouseSensitivity = -1;
    }

    protected static float getNormalField() {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        return (float) (renderer.isPanoramicMode() ? Math.tan(Math.toRadians(45)) : Math.tan(Math.toRadians(35)));
    }

    public static float getFov(float rate) {
        return (float) (Math.atan(getNormalField() / rate) * 180 / Math.PI * 2);
    }
}
