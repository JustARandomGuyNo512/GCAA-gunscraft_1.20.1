package sheridan.gcaa.client.model.attachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import sheridan.gcaa.Clients;

public abstract class ScopeModel extends SightModel {
    protected float getNormalField() {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        return (float) (renderer.isPanoramicMode() ? Math.tan(Math.toRadians(45)) : Math.tan(Math.toRadians(35)));
    }

    public abstract float getMinDisZDistance(float prevAdsProgress);

    public float calcMinDisZDistance(float defaultVal, float prevFov)  {
        if (useModelFovModifyWhenAds() && Clients.isInAds()) {
            double tanNew = Math.tan(Math.toRadians(prevFov / 2.0));
            return (float) (defaultVal * (getNormalField() / tanNew));
        }
        return defaultVal;
    }

    public float modelFovModifyWhenAds() {
        return 8.5f;
    }

    public boolean useModelFovModifyWhenAds() {
        return true;
    }
}
