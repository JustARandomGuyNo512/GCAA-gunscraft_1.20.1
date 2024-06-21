package sheridan.gcaa.client.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.items.guns.IGun;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvents {

    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (player != null && player.level().isClientSide()) {
                ItemStack stackMain = player.getMainHandItem();
                IGun gunMain = stackMain.getItem() instanceof IGun ? (IGun) stackMain.getItem() : null;
                Clients.mainHandStatus.holdingGun.set(gunMain != null);
            }
        }
    }

}
