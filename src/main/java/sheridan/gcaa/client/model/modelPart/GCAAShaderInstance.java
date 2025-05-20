package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class GCAAShaderInstance extends ShaderInstance {
    public GCAAShaderInstance(ResourceProvider pResourceProvider, ResourceLocation shaderLocation, VertexFormat pVertexFormat) throws IOException {
        super(pResourceProvider, shaderLocation, pVertexFormat);
    }
}
