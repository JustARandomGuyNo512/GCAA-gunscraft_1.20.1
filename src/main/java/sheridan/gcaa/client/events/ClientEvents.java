package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.items.AutoRegister;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    private static int timeOffsetSyncTick = 0;

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (event.phase == TickEvent.Phase.START) {
            try {
                Clients.cancelLooperWork.set(!Minecraft.getInstance().isWindowActive() || minecraft.isPaused() || minecraft.screen != null);
                Clients.cancelLooperWorkWithCoolDown.set(player == null || player.isSpectator() || player.isSwimming() || player.isInLava());
                AnimationHandler.INSTANCE.onClientTick();
                Clients.LOCK.lock();
            } catch (Exception ignored) {}
            if (!Clients.clientRegistriesHandled) {
                ForgeRegistries.ITEMS.getEntries().forEach(entry -> {
                    if (entry.getValue() instanceof AutoRegister autoRegister) {
                        autoRegister.clientRegister(entry);
                    }
                });
                Clients.clientRegistriesHandled = true;
            }
        }

        if (event.phase == TickEvent.Phase.END) {
            if (Clients.LOCK.isLocked()) {
                Clients.LOCK.unlock();
            }
            Clients.lastClientTick = System.currentTimeMillis();
            Clients.equipDelayCoolDown();
            if (timeOffsetSyncTick == 20 && player != null) {
                PlayerStatusProvider.updateLocalTimeOffset(player);
                timeOffsetSyncTick = 0;
            }
            timeOffsetSyncTick++;
        }
    }

}
