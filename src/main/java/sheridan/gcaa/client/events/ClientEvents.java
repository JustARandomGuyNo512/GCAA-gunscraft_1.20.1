package sheridan.gcaa.client.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            try {
                Clients.lock.lock();
            } catch (Exception ignored) {}
        }

        if (event.phase == TickEvent.Phase.END) {
            if (Clients.lock.isLocked()) {
                Clients.lock.unlock();
            }
        }
    }
}
