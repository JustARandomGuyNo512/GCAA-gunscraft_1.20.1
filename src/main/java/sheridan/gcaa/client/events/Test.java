package sheridan.gcaa.client.events;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.client.model.modelPart.BufferedMeshModelBone;

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

    static boolean start = false;

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            if (!start) {
                BufferedMeshModelBone.__start_test__();
            }
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() == Items.APPLE) {
                BufferedMeshModelBone.__test_render__();
            }
        }
    }

//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event)  {
//        if (event.phase == TickEvent.Phase.END) {
//            System.out.println(PlayerStatusProvider.getStatus(event.player).isReloading() + " " + event.player.level().isClientSide);
//        }
//    }
}
