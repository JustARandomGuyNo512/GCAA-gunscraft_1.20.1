package sheridan.gcaa.client.model.attachments;

import sheridan.gcaa.Clients;

public interface IScopeModel extends ISightModel{
    double tanDefault = Math.tan(Math.toRadians(35));

    float getMinDisZDistance(float prevFov);

    default float calcMinDisZDistance(float defaultVal, float prevFov)  {
        if (useAimingModelFovModify() && Clients.isInAds()) {
            double tanNew = Math.tan(Math.toRadians(prevFov / 2.0));
            return (float) (defaultVal * (tanDefault / tanNew));
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
