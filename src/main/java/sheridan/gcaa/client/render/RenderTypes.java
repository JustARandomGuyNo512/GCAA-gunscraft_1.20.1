package sheridan.gcaa.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import sheridan.gcaa.GCAA;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class RenderTypes extends RenderType {
    private static final Map<String, RenderType> TEMP = new HashMap<>();

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
    public static RenderType getMuzzleFlashNotWriteDepth(ResourceLocation texture) {
        return RenderType.create("muzzle_flash_not_write_depth", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS, 256, true, true,
                CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new TextureStateShard(texture, false, false))
                        .setLightmapState(LightmapStateShard.LIGHTMAP)
                        .setWriteMaskState(new WriteMaskStateShard(true, false))
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

    public static RenderType getCutOutNoCullMipmap(ResourceLocation location) {
        String baseKey = location.toString() + ":" + "cutout_no_cull_mip_map";
        if (TEMP.containsKey(baseKey)) {
            return TEMP.get(baseKey);
        } else {
            RenderType.CompositeState type = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER).setTextureState(new TextureStateShardMip(location)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
            RenderType baseType = create("cutout_no_cull_mip_map", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, type);
            TEMP.put(baseKey, baseType);
            return baseType;
        }
    }

    public static RenderType getCutOutMipmap(ResourceLocation location) {
        String baseKey = location.toString() + ":" + "cutout_mip_map";
        if (TEMP.containsKey(baseKey)) {
            return TEMP.get(baseKey);
        } else {
            RenderType.CompositeState type = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER).setTextureState(new TextureStateShardMip(location)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
            RenderType baseType = create("cutout_mip_map", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, type);
            TEMP.put(baseKey, baseType);
            return baseType;
        }
    }

    public static final int GL_TEXTURE_MAX_ANISOTROPY = 0x84FE;
    public static final int GL_MAX_TEXTURE_MAX_ANISOTROPY = 0x84FF;
    public static class TextureStateShardMip extends RenderStateShard.EmptyTextureStateShard {
        private final Optional<ResourceLocation> texture;
        public TextureStateShardMip(ResourceLocation pTexture) {
            super(() -> {
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                AbstractTexture texture = texturemanager.getTexture(pTexture);
                texture.setFilter(false, true);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY, GL11.glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY));
                RenderSystem.setShaderTexture(0, pTexture);
            }, () -> {
            });
            this.texture = Optional.of(pTexture);
            this.blur = false;
            this.mipmap = true;
        }

        protected boolean blur;
        protected boolean mipmap;

        public @NotNull String toString() {
            return this.name + "[" + this.texture + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
        }

        protected @NotNull Optional<ResourceLocation> cutoutTexture() {
            return this.texture;
        }
    }

}
