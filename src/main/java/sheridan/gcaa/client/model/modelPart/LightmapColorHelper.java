package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import sheridan.gcaa.mixin.LightTextureAccessor;


public class LightmapColorHelper {
    static Vector3f NONE = new Vector3f(1, 1, 1);

    public static Vector3f init(int packedLight) {
        LightTexture lightmap = Minecraft.getInstance().gameRenderer.lightTexture();
        NativeImage pixels = ((LightTextureAccessor) lightmap).getLightTexture().getPixels();
        if (pixels == null) {
            return NONE;
        }

        int u = packedLight & '\uffff';
        int v = packedLight >> 16 & '\uffff';

        int color = pixels.getPixelRGBA(u / 16, v / 16);

        float b = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float r = (color & 0xFF) / 255f;
        return new Vector3f(r, g, b);
    }

}
