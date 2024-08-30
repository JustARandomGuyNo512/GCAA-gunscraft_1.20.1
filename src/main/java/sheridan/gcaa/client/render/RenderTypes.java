package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.events.Test;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderTypes extends RenderType {
    private static final Map<String, RenderType> TEMP = new HashMap<>();
    private static RenderType TEST;
    private static final ResourceLocation TEXT_TEXTURE = new ResourceLocation(GCAA.MODID, "textures/misc/test.png");

    public RenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType getMuzzleFlash(ResourceLocation texture) {
        return RenderType.create("muzzle_flash", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS, 256, true, true,
                CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new TextureStateShard(texture, false, false))
                        .setLightmapState(LightmapStateShard.LIGHTMAP)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));

    }

    public static RenderType getAttachmentOverlayDepth(ResourceLocation texture) {
        String baseKey = texture.toString() + ":" + "attachment_overlay_depth";
        if (TEMP.containsKey(baseKey)) {
            return TEMP.get(baseKey);
        } else {
            RenderType baseType = RenderType.create(GCAA.MODID + ":" + "attachment_overlay_depth", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                    VertexFormat.Mode.QUADS, 256, true, false,
                    CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
            TEMP.put(baseKey, baseType);
            return baseType;
        }
    }

    public static RenderType getStencilCull(ResourceLocation texture) {
        String baseKey = texture.toString()+ ":" + "stencil_cull";
        if (TEMP.containsKey(baseKey)) {
            return TEMP.get(baseKey);
        } else {
            RenderType baseType = RenderType.create(GCAA.MODID + ":" + "stencil_cull", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                    VertexFormat.Mode.QUADS, 256, true, false,
                    CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setWriteMaskState(new WriteMaskStateShard(false, false))
                            .setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
            TEMP.put(baseKey, baseType);

            return baseType;
        }
    }

    public static RenderType getBackground(ResourceLocation texture) {
        String baseKey = texture.toString() + ":" + "get_background";
        if (TEMP.containsKey(baseKey)) {
            return TEMP.get(baseKey);
        } else {
            RenderType baseType = RenderType.create(GCAA.MODID + ":" + "test", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                    VertexFormat.Mode.QUADS, 256, true, false,
                    CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
            TEMP.put(baseKey, baseType);

            return baseType;
        }
    }

    public static RenderType getTest(ResourceLocation location) {
        String baseKey = location.toString() + ":" + "test";
        if (TEMP.containsKey(baseKey)) {
            return TEMP.get(baseKey);
        } else {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(Test::getTestShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);
            RenderType baseType = create("test",
                    DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                    256,
                    true,
                    false,
                    compositeState);
            TEMP.put(baseKey, baseType);
            return baseType;
        }
    }
}
