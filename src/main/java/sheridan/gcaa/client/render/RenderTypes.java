package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderTypes extends RenderType {
    private static final Map<String, RenderType> TEMP = new HashMap<>();

    public RenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType getMuzzleFlash(ResourceLocation texture) {
        String baseKey = texture.toString() + ":" + "muzzle_flash";
        if (TEMP.containsKey(baseKey)) {
            return TEMP.get(baseKey);
        } else {
            RenderType baseType = RenderType.create(GCAA.MODID + ":" + "muzzle_flash", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                    VertexFormat.Mode.QUADS, 256, true, true,
                    CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setLightmapState(LightmapStateShard.LIGHTMAP)
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));
            TEMP.put(baseKey, baseType);
            return baseType;
        }
    }
}
