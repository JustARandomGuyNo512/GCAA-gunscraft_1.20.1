package sheridan.gcaa.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

@OnlyIn(Dist.CLIENT)
public class KeyBinds {
    public static final KeyMapping OPEN_DEBUG_SCREEN = new KeyMapping("key.gcaa.open_debug_gun_screen", 72, "keys.categories.gcaa");
    public static final KeyMapping SWITCH_FIRE_MODE = new KeyMapping("key.gcaa.switch_fire_mode", 86, "keys.categories.gcaa");
    public static final KeyMapping RELOAD = new KeyMapping("key.gcaa.reload", 82, "keys.categories.gcaa");

    public static final KeyMapping DEBUG_KEY = new KeyMapping("", 74, "");


    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_DEBUG_SCREEN);
        event.register(SWITCH_FIRE_MODE);
        event.register(RELOAD);
    }
}
