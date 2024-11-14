package sheridan.gcaa.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

@OnlyIn(Dist.CLIENT)
public class KeyBinds {
    public static final KeyMapping SWITCH_FIRE_MODE = new KeyMapping("key.gcaa.switch_fire_mode", 86, "keys.categories.gcaa");
    public static final KeyMapping RELOAD = new KeyMapping("key.gcaa.reload", 82, "keys.categories.gcaa");
    public static final KeyMapping OPEN_GUN_MODIFY_SCREEN = new KeyMapping("key.gcaa.open_gun_modify_screen", 71, "keys.categories.gcaa");
    public static final KeyMapping SHOW_FULL_GUN_INFO = new KeyMapping("key.gcaa.key_show_full_gun_info", 78, "keys.categories.gcaa");
    public static final KeyMapping SWITCH_EFFECTIVE_SIGHT = new KeyMapping("key.gcaa.switch_effective_sight", 89, "keys.categories.gcaa");
    public static final KeyMapping USE_GRENADE_LAUNCHER = new KeyMapping("key.gcaa.use_grenade_launcher", 342, "keys.categories.gcaa");
    public static final KeyMapping TURN_FLASHLIGHT = new KeyMapping("key.gcaa.turn_flashlight", 75, "keys.categories.gcaa");
    public static final KeyMapping UNLOAD_GUN = new KeyMapping("key.gcaa.unload_gun", 45, "keys.categories.gcaa");


    public static final KeyMapping OPEN_CLIENT_SETTINGS_SCREEN = new KeyMapping("key.gcaa.open_client_settings_screen", 73, "keys.categories.gcaa");
    public static final KeyMapping OPEN_DEBUG_SCREEN = new KeyMapping("key.gcaa.open_debug_gun_screen", 72, "keys.categories.gcaa");


    public static final KeyMapping DEBUG_KEY = new KeyMapping("", 74, "");


    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SWITCH_FIRE_MODE);
        event.register(RELOAD);
        event.register(OPEN_GUN_MODIFY_SCREEN);
        event.register(SHOW_FULL_GUN_INFO);
        event.register(SWITCH_EFFECTIVE_SIGHT);
        event.register(USE_GRENADE_LAUNCHER);
        event.register(TURN_FLASHLIGHT);
        //event.register(UNLOAD_GUN);

        //event.register(OPEN_CLIENT_SETTINGS_SCREEN);
        //event.register(DEBUG_KEY);
        //event.register(OPEN_DEBUG_SCREEN);
    }
}
