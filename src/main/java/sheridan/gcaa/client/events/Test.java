package sheridan.gcaa.client.events;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.GCAA;

import java.io.IOException;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class Test {

    public static ShaderInstance testShader;

    static boolean firstTick = true;
    @SubscribeEvent
    public static void init(TickEvent.ClientTickEvent event) throws IOException {
//        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().screen != null) {
//            System.out.println(Minecraft.getInstance().screen + " " + Minecraft.getInstance().screen.getClass().getName());
//        }
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
