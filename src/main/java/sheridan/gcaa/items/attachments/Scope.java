package sheridan.gcaa.items.attachments;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import sheridan.gcaa.Clients;

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

    private double tempMouseSensitivity = -1;

    public void handleMouseSensitivity() {
        float aimingProgress = Clients.mainHandStatus.getLerpAdsProgress(Math.pow(Minecraft.getInstance().getPartialTick(), 3));
        if (Clients.isInAds()) {
            if (tempMouseSensitivity == -1) {
                tempMouseSensitivity = Minecraft.getInstance().options.sensitivity().get();
            } else {
                double prevMouseSensitivity = Mth.lerp(aimingProgress, tempMouseSensitivity, tempMouseSensitivity * (1 / maxMagnification));
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
}
