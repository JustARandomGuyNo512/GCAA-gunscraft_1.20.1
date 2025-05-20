package sheridan.gcaa.client.events;


import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.io.GltfLoader;
import sheridan.gcaa.client.model.modelPart.BufferedModelBone;
import sheridan.gcaa.client.model.modelPart.GCAAShaderInstance;

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

    public static ShaderInstance SHADER_FOR_ENTITY_CUTOUT;
    @SubscribeEvent
    public static void registerShader(RegisterShadersEvent event) {
        try {
            event.registerShader(new GCAAShaderInstance(event.getResourceProvider(), new ResourceLocation(GCAA.MODID, "rendertype_entity_cutout.json"), DefaultVertexFormat.NEW_ENTITY), (shader) -> {
                SHADER_FOR_ENTITY_CUTOUT = shader;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
