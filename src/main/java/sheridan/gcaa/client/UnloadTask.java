package sheridan.gcaa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.PlayerSoundPacket;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class UnloadTask implements IReloadTask{
    public static UnloadData RIFLE = null;
    public static UnloadData PISTOL = null;
    public static UnloadData SHOTGUN = null;
    public static UnloadData MG = null;
    public static UnloadData SMG = null;
    public static UnloadData SNIPER = null;
    public static UnloadData CAL_50 = null;
    public static UnloadData REVOLVER = null;
    private static boolean init = false;

    public static void init() {
        if (!init) {
            RIFLE = new UnloadData(RenderAndMathUtils.secondsToTicks(2.5f), ModSounds.RIFLE_UNLOAD.get());
            PISTOL = new UnloadData(RenderAndMathUtils.secondsToTicks(1.6f), ModSounds.PISTOL_UNLOAD.get());
            SHOTGUN = new UnloadData(RenderAndMathUtils.secondsToTicks(2f), ModSounds.SHOTGUN_UNLOAD.get());
            MG = new UnloadData(RenderAndMathUtils.secondsToTicks(5f), ModSounds.MG_UNLOAD.get());
            SMG = new UnloadData(RenderAndMathUtils.secondsToTicks(1.8f), ModSounds.SMG_UNLOAD.get());
            SNIPER = new UnloadData(RenderAndMathUtils.secondsToTicks(1.9f), ModSounds.SNIPER_UNLOAD.get());
            CAL_50 = new UnloadData(RenderAndMathUtils.secondsToTicks(3f), ModSounds.CAL_50_UNLOAD.get());
            REVOLVER = new UnloadData(RenderAndMathUtils.secondsToTicks(2f), ModSounds.REVOLVER_UNLOAD.get());
        }
        init = true;
    }

    public int length;
    public int tick = 0;
    public boolean completed = false;
    public ItemStack gunStack;
    public IGun gun;
    public SoundEvent soundEvent;

    public UnloadTask(IGun gun, ItemStack gunStack, UnloadData data) {
        this.length = data.length;
        this.soundEvent = data.sound;
        this.gunStack = gunStack;
        this.gun = gun;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void tick(Player clientPlayer) {
        if (tick == 0) {
            PlayerStatusProvider.setReloading(clientPlayer, true);
        }
        if (tick == 3 && soundEvent != null) {
            ModSounds.sound(1 ,1, clientPlayer, soundEvent);
            ResourceLocation key = ForgeRegistries.SOUND_EVENTS.getKey(soundEvent);
            if (key != null) {
                PacketHandler.simpleChannel.sendToServer(new PlayerSoundPacket(key.toString()));
            }
        }
        if (tick == length) {
            //TODO: unload

        }
        if (tick >= length) {
            completed = true;
        }
        tick ++;
    }

    @Override
    public ItemStack getStack() {
        return gunStack;
    }

    @Override
    public int getCustomPayload() {
        return 0;
    }

    @Override
    public void onBreak() {
        onCancel();
    }

    @Override
    public void onCancel() {
        PlayerStatusProvider.setReloading(Minecraft.getInstance().player, false);
    }

    @Override
    public void start() {
        Clients.setEquipDelay(length);
    }

    @Override
    public boolean isGenericReloading() {
        return true;
    }

    @Override
    public float getProgress() {
        return length == 0 ? 0 : tick / (float) length;
    }

    public record UnloadData(int length, SoundEvent sound) {}

}
