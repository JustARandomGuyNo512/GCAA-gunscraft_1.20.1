package sheridan.gcaa.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.awt.*;

public class FontUtils {

    public static MutableComponent helperTip(MutableComponent component) {
        component.setStyle(Style.EMPTY.withColor(Color.GRAY.getRGB()).withItalic(true));
        return component;
    }

    private static int getColor(float value, float good, float bad) {
        float ratio;
        int r, g, b;
        if (good > bad) {
            ratio = (value - bad) / (good - bad);
        } else {
            ratio = (value - good) / (bad - good);
        }
        ratio = Math.max(0, Math.min(1, ratio));
        if (ratio <= 0.5) {
            float adjustedRatio = ratio * 2;
            r = 255;
            g = (int) (255 * adjustedRatio);
            b = (int) (255 * adjustedRatio);
        } else {
            float adjustedRatio = (ratio - 0.5f) * 2;
            r = (int) (255 * (1 - adjustedRatio));
            g = 255;
            b = (int) (255 * (1 - adjustedRatio));
        }

        return (r << 16) | (g << 8) | b;
    }

    public static MutableComponent dataTip(String key, float value, float good, float bad)  {
        return Component.translatable(key).append(Component.literal(value + "").withStyle(Style.EMPTY.withColor(getColor(value, good, bad))));
    }

    public static MutableComponent dataTip(String key, float value, float good, float bad, String unit)  {
        return dataTip(key, value, good, bad).append(Component.literal(" ").append(Component.translatable(unit)));
    }
}
