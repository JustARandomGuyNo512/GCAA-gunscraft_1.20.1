package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.AutoRegister;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (event.phase == TickEvent.Phase.START) {
            try {
                Clients.cancelLooperWork.set(!Minecraft.getInstance().isWindowActive() || minecraft.isPaused() || minecraft.screen != null);
                Clients.cancelLooperWorkWithCoolDown.set(player == null || player.isSpectator() || player.isSwimming() || player.isInLava());
                Clients.lock.lock();
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
            if (Clients.lock.isLocked()) {
                Clients.lock.unlock();
            }
        }
    }


}
