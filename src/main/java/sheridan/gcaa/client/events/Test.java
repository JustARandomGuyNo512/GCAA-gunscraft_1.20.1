package sheridan.gcaa.client.events;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.GCAA;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class Test {
//    public static ShaderInstance renderTypeTest;

//    @SubscribeEvent
//    public static void textureTest(RegisterShadersEvent event){
//        try {
//            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(GCAA.MODID, ""), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (shaderInstance) -> {renderTypeTest = shaderInstance;});
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
