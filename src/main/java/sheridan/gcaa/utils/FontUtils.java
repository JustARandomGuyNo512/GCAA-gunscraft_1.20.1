package sheridan.gcaa.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.w3c.dom.css.RGBColor;

import java.awt.*;

public class FontUtils {
    private static MutableComponent EXCELLENT_WORSE = null;
    private static String EXCELLENT_WORSE_STR = "";

    public static MutableComponent helperTip(MutableComponent component) {
        component.setStyle(Style.EMPTY.withColor(Color.GRAY.getRGB()).withItalic(true));
        return component;
    }

    public static int getColor(float value, float good, float bad) {
        float ratio;
        ratio = Math.abs((value - good)) / Math.abs((bad - good));
        ratio = Mth.clamp(1 - ratio, 0, 1);
        int red = (int) (255 * Math.cos(ratio * Math.PI / 2));
        int green = (int) (255 * Math.sin(ratio * Math.PI / 2));
        return (red << 16) | (green << 8);
    }

    public static MutableComponent dataTip(String key, float value, float good, float bad)  {
        return Component.translatable(key).append(Component.literal(value + "").withStyle(Style.EMPTY.withColor(getColor(value, good, bad))));
    }

    public static MutableComponent dataTip(String key, String value, int color)  {
        return Component.translatable(key).append(Component.literal(value).withStyle(Style.EMPTY.withColor(color)));
    }

    public static MutableComponent dataTip(String key, float value, float good, float bad, String unit)  {
        return dataTip(key, value, good, bad).append(Component.literal(" ").append(Component.translatable(unit)));
    }

    public static MutableComponent getExcellentWorse() {
        String str = Component.translatable("tooltip.gcaa.excellent_worse").getString();
        if (EXCELLENT_WORSE == null || !EXCELLENT_WORSE_STR.equals(str)) {
            EXCELLENT_WORSE_STR = str;
            String[] words = str.split(" ");
            EXCELLENT_WORSE = (Component.literal(words[0]).withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB()).withItalic(true)))
                    .append(Component.literal("⚫   ").withStyle(Style.EMPTY.withColor(0x00FF00)))
                    .append(Component.literal(words[1]).withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB()).withItalic(true)))
                    .append(Component.literal("⚫").withStyle(Style.EMPTY.withColor(0xFF0000)));
        }
        return EXCELLENT_WORSE;
    }

    public static float toPercentage(float val) {
        return Math.round(val * 10000) / 100.0f;
    }

    public static String toPercentageStr(float val) {
        return String.format("%.2f%%", val * 100);
    }
}
