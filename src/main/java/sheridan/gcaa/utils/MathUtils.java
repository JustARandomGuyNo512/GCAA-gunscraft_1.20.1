package sheridan.gcaa.utils;

import net.minecraft.util.Mth;

public class MathUtils {
    public static float sLerp(float progress) {
        float f1 = progress * progress;
        float f2 = 1.0f - (1.0f - progress) * (1.0f - progress);
        return Mth.lerp(progress, f1, f2);
    }
}
