package sheridan.gcaa.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.ClientSoundPacket;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> MOD_SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GCAA.MODID);
    public static RegistryObject<SoundEvent> AK_MAG_OFF = registerSound("ak_mag_off", "item.generic.ak_mag_off");
    public static RegistryObject<SoundEvent> AK_MAG_ATTACH = registerSound("ak_mag_attach", "item.generic.ak_mag_attach");
    public static RegistryObject<SoundEvent> AK_CHARGE = registerSound("ak_charge", "item.generic.ak_charge");
    public static RegistryObject<SoundEvent> PISTOL_SLIDE_BACK = registerSound("pistol_slide_back", "item.generic.pistol_slide_back");
    public static RegistryObject<SoundEvent> PISTOL_SLIDE_FORWARD = registerSound("pistol_slide_forward", "item.generic.pistol_slide_forward");
    public static RegistryObject<SoundEvent> SMG_MAG_ATTACH = registerSound("smg_mag_attach", "item.generic.smg_mag_attach");
    public static RegistryObject<SoundEvent> SMG_MAG_OFF = registerSound("smg_mag_off", "item.generic.smg_mag_off");
    public static RegistryObject<SoundEvent> AWP_MAG_OFF = registerSound("awp_mag_off", "item.generic.awp_mag_off");
    public static RegistryObject<SoundEvent> AWP_MAG_ATTACH = registerSound("awp_mag_attach", "item.generic.awp_mag_attach");
    public static RegistryObject<SoundEvent> AWP_BOLT_BACK = registerSound("awp_bolt_back", "item.generic.awp_bolt_back");
    public static RegistryObject<SoundEvent> AWP_BOLT_FORWARD = registerSound("awp_bolt_forward", "item.generic.awp_bolt_forward");
    public static RegistryObject<SoundEvent> M870_INSERT_SHELL = registerSound("m870_insert_shell", "item.generic.m870_insert_shell");
    public static RegistryObject<SoundEvent> M870_PUMP_ACTION = registerSound("m870_pump_action", "item.generic.m870_pump_action");
    public static RegistryObject<SoundEvent> MG_BOX_ATTACH = registerSound("mg_box_attach", "item.generic.mg_box_attach");
    public static RegistryObject<SoundEvent> MG_BOX_OFF = registerSound("mg_box_off", "item.generic.mg_box_off");
    public static RegistryObject<SoundEvent> MG_CHAIN = registerSound("mg_chain", "item.generic.mg_chain");
    public static RegistryObject<SoundEvent> MG_CHARGE_BACK = registerSound("mg_charge_back", "item.generic.mg_charge_back");
    public static RegistryObject<SoundEvent> MG_CHARGE_FROWARD = registerSound("mg_charge_forward", "item.generic.mg_charge_forward");
    public static RegistryObject<SoundEvent> MG_COVER_OFF = registerSound("mg_cover_off", "item.generic.mg_cover_off");
    public static RegistryObject<SoundEvent> MG_COVER_OPEN = registerSound("mg_cover_open", "item.generic.mg_cover_open");

    public static RegistryObject<SoundEvent> AKM_FIRE = registerSound("akm_fire", "item.akm.fire");
    public static RegistryObject<SoundEvent> G19_FIRE = registerSound("g19_fire", "item.g19.fire");
    public static RegistryObject<SoundEvent> AWP_FIRE = registerSound("awp_fire", "item.awp.fire");
    public static RegistryObject<SoundEvent> AWP_FIRE_SUPPRESSED = registerSound("awp_fire_suppressed", "item.awp.fire_suppressed");
    public static RegistryObject<SoundEvent> M870_FIRE = registerSound("m870_fire", "item.m870.fire");
    public static RegistryObject<SoundEvent> M870_FIRE_SUPPRESSED = registerSound("m870_fire_suppressed", "item.m870.fire_suppressed");
    public static RegistryObject<SoundEvent> PYTHON_357_FIRE = registerSound("python_357_fire", "item.python_357.fire");
    public static RegistryObject<SoundEvent> M249_FIRE = registerSound("m249_fire", "item.m249.fire");
    public static RegistryObject<SoundEvent> PYTHON_357_BULLETS_IN = registerSound("bullets_in", "item.python_357.bullets_in");
    public static RegistryObject<SoundEvent> PYTHON_357_BULLETS_OUT = registerSound("bullets_out", "item.python_357.bullets_out");
    public static RegistryObject<SoundEvent> PYTHON_357_MAG_OPEN = registerSound("mag_open", "item.python_357.mag_open");
    public static RegistryObject<SoundEvent> PYTHON_357_MAG_CLOSE = registerSound("mag_close", "item.python_357.mag_close");

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
     *             like: ForgeRegistries.SOUND_EVENTS.getKey(soundEvent)
     * */
    public static void sound(float vol, float pit, Player player, ResourceLocation name) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(name);
        if (soundEvent != null) {
            sound(vol, pit, player, soundEvent);
        }
    }

    public static void boardCastSound(SoundEvent soundEvent, float vol, float volModify, float pitch, ServerPlayer player) {
        if (soundEvent == null) {
            return;
        }
        ResourceLocation key = ForgeRegistries.SOUND_EVENTS.getKey(soundEvent);
        if (key != null) {
            boardCastSound(key.toString(), soundEvent.getRange(vol), vol, volModify, pitch, player);
        }
    }

    public static void boardCastSound(String name, float range, float vol, float volModify, float pitch, ServerPlayer player) {
        PacketHandler.simpleChannel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                player, player.position().x, player.position().y, player.position().z, range, player.level().dimension()
        )), new ClientSoundPacket(vol, volModify, pitch, player.position(), name));
    }

    public static void soundAtPoint(float vol, float pit, float x, float y, float z, Player player, ResourceLocation name) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(name);
        if (soundEvent != null) {
            player.level().playSound(player, x, y, z, soundEvent, player.getSoundSource(), vol, pit);
        }
    }
}
