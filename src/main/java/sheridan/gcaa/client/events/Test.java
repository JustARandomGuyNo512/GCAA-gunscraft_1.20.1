package sheridan.gcaa.client.events;


import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.client.model.io.GltfLoader;
import sheridan.gcaa.client.model.modelPart.BufferedModelBone;

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


    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() == Items.APPLE) {
                BufferedModelBone.__test_render__(Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(player, event.getPartialTick()));
            }
        }
    }

    static boolean test = false;
    @SubscribeEvent
    public static void test(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !test) {
            System.out.println("Player logged in test load gltf");
            GltfLoader.test();
            test = true;
        }
    }

//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event)  {
//        if (event.phase == TickEvent.Phase.END) {
//            System.out.println(PlayerStatusProvider.getStatus(event.player).isReloading() + " " + event.player.level().isClientSide);
//        }
//    }
}
