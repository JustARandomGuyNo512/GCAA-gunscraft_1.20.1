package sheridan.gcaa.client.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.Clients;
import sheridan.gcaa.attachmentSys.common.AttachmentRegister;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            try {
                Clients.lock.lock();
            } catch (Exception ignored) {}
            if (!Clients.clientRegistriesHandled) {
                ForgeRegistries.ITEMS.getEntries().forEach(entry -> {
                    AttachmentRegister.onHandleRegistriesInit(entry);
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
