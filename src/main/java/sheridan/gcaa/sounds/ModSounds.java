package sheridan.gcaa.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> MOD_SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GCAA.MODID);
    public static RegistryObject<SoundEvent> AK_MAG_OFF = registerSound("ak_mag_off", "item.generic.ak_mag_off");
    public static RegistryObject<SoundEvent> AK_MAG_ATTACH = registerSound("ak_mag_attach", "item.generic.ak_mag_attach");
    public static RegistryObject<SoundEvent> AK_CHARGE = registerSound("ak_charge", "item.generic.ak_charge");
    public static RegistryObject<SoundEvent> PISTOL_SLIDE_BACK = registerSound("pistol_slide_back", "item.generic.pistol_slide_back");
    public static RegistryObject<SoundEvent> PISTOL_SLIDE_FORWARD = registerSound("pistol_slide_forward", "item.generic.pistol_slide_forward");
    public static RegistryObject<SoundEvent> SMG_MAG_ATTACH = registerSound("smg_mag_attach", "item.generic.smg_mag_attach");
    public static RegistryObject<SoundEvent> SMG_MAG_OFF = registerSound("smg_mag_off", "item.generic.smg_mag_off");

    public static RegistryObject<SoundEvent> AKM_FIRE = registerSound("akm_fire", "item.akm.fire");
    public static RegistryObject<SoundEvent> G19_FIRE = registerSound("g19_fire", "item.g19.fire");

    private static RegistryObject<SoundEvent> registerSound(String name, String path) {
        return MOD_SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GCAA.MODID, path)));
    }

    public static void register(IEventBus bus) {
        MOD_SOUNDS.register(bus);
    }

    /**
     * play a sound immediately.
     * */
    public static void sound(float vol, float pit, Player player, SoundEvent soundEvent) {
        player.playSound(soundEvent, vol, pit);
    }

    /**
     * play a sound immediately.
     * @param name the registry path of the sound event. such as: new ResourceLocation(your_mod.MODID, name).
     *             the "name" is the name when a sound event registered, not the key of this sound event in sounds.json.
     * */
    public static void sound(float vol, float pit, Player player, ResourceLocation name) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(name);
        if (soundEvent != null) {
            sound(vol, pit, player, soundEvent);
        }
    }

    public static void soundAtPoint(float vol, float pit, float x, float y, float z, Player player, ResourceLocation name) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(name);
        if (soundEvent != null) {
            player.level().playSound(player, x, y, z, soundEvent, player.getSoundSource(), vol, pit);
        }
    }
}
