package sheridan.gcaa.client.events;


import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class Test {

    public static ShaderInstance testShader;

    static boolean firstTick = true;
    @SubscribeEvent
    public static void init(TickEvent.ClientTickEvent event) throws IOException {

    }

    public static ShaderInstance getTestShader() {
        return testShader;
    }


//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event)  {
//        if (event.phase == TickEvent.Phase.END) {
//            System.out.println(PlayerStatusProvider.getStatus(event.player).isReloading() + " " + event.player.level().isClientSide);
//        }
//    }
}
