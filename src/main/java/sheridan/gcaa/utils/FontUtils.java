package sheridan.gcaa.utils;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.awt.*;

public class FontUtils {

    public static MutableComponent helperTip(MutableComponent component) {
        component.setStyle(Style.EMPTY.withColor(Color.GRAY.getRGB()).withItalic(true));
        return component;
    }

}
