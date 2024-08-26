package sheridan.gcaa.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.fireModes.HandAction;

@Mod.EventBusSubscriber
public class TestEvents {

//   static  int delay = 0;
//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        Player player = event.player;
//        if (event.phase == TickEvent.Phase.END) {
//            delay ++;
//            if (delay >= 5) {
//                if (player.getMainHandItem().getItem() instanceof HandActionGun gun) {
//                    System.out.println(gun.needHandAction(player.getMainHandItem()) + " " + (player.level().isClientSide ? "client" : "server"));
//                }
//                delay = 0;
//            }
//        }
//    }
}
