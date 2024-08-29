package sheridan.gcaa.client.model.attachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import sheridan.gcaa.Clients;

public interface IScopeModel extends ISightModel{
    default float getNormalField() {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        return (float) (renderer.isPanoramicMode() ? Math.tan(Math.toRadians(45)) : Math.tan(Math.toRadians(35)));
    }

    float getMinDisZDistance(float prevFov);

    default float calcMinDisZDistance(float defaultVal, float prevFov)  {
        if (useAimingModelFovModify() && Clients.isInAds()) {
            double tanNew = Math.tan(Math.toRadians(prevFov / 2.0));
            return (float) (defaultVal * (getNormalField() / tanNew));
        }
        return defaultVal;
    }

    default float aimingModelFovModify() {
        return 8.5f;
    }

    default boolean useAimingModelFovModify() {
        return true;
    }
}
