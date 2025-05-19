package sheridan.gcaa.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// LightTextureAccessor.java
@Mixin({LightTexture.class})
public interface LightTextureAccessor {
    @Accessor("lightTexture")
    DynamicTexture getLightTexture();

    @Accessor("lightPixels")
    NativeImage getLightPixels();
}