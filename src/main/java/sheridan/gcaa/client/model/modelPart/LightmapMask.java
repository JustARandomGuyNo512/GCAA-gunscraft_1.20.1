package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.mixin.LightTextureAccessor;

@OnlyIn(Dist.CLIENT)
public class LightmapMask {
    private static final int prevLightmapUV = 15728880;

    private static final int[][] lightmapStorage = new int[16][16];


    public static void maskLight(int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        LightTexture lightTexture = mc.gameRenderer.lightTexture();
        LightTextureAccessor accessor = (LightTextureAccessor) lightTexture;

        NativeImage pixels = accessor.getLightPixels();
        DynamicTexture texture = accessor.getLightTexture();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j ++) {
                int color = pixels.getPixelRGBA(i, j);
                lightmapStorage[i][j] = color;
            }
        }

        int distU = 15 - (packedLight & '\uffff') / 16;
        int distV = 15 - (packedLight >> 16 & '\uffff') / 16;

        for (int i = 15; i >= 0; i --) {
            for (int j = 15; j >= 0; j --) {
                int color = pixels.getPixelRGBA(Math.max(i - distU, 0), Math.max(j - distV, 0));
                pixels.setPixelRGBA(i, j, color);
            }
        }

        texture.upload();
    }

    public static void reset() {
        Minecraft mc = Minecraft.getInstance();
        LightTexture lightTexture = mc.gameRenderer.lightTexture();
        LightTextureAccessor accessor = (LightTextureAccessor) lightTexture;
        NativeImage pixels = accessor.getLightPixels();
        DynamicTexture texture = accessor.getLightTexture();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j ++) {
                pixels.setPixelRGBA(i, j, lightmapStorage[i][j]);
            }
        }
        texture.upload();
    }
}
