package sheridan.gcaa.client.render.postEffect;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;
import java.util.function.IntSupplier;

@OnlyIn(Dist.CLIENT)
public class PostPass implements AutoCloseable {
    public  final IOSafeEffectInstance effect;
    public final RenderTarget inTarget;
    public final RenderTarget outTarget;
    public  final List<IntSupplier> auxAssets = Lists.newArrayList();
    public  final List<String> auxNames = Lists.newArrayList();
    public  final List<Integer> auxWidths = Lists.newArrayList();
    public  final List<Integer> auxHeights = Lists.newArrayList();
    public  Matrix4f shaderOrthoMatrix;

    public PostPass(ResourceManager pResourceManager, String pName, RenderTarget pInTarget, RenderTarget pOutTarget) throws IOException {
        this.effect = new IOSafeEffectInstance(pResourceManager, pName);
        this.inTarget = pInTarget;
        this.outTarget = pOutTarget;
    }

    public void close() {
        this.effect.close();
    }

    public final String getName() {
        return this.effect.getName();
    }

    public void addAuxAsset(String pAuxName, IntSupplier pAuxFramebuffer, int pWidth, int pHeight) {
        this.auxNames.add(this.auxNames.size(), pAuxName);
        this.auxAssets.add(this.auxAssets.size(), pAuxFramebuffer);
        this.auxWidths.add(this.auxWidths.size(), pWidth);
        this.auxHeights.add(this.auxHeights.size(), pHeight);
    }

    public void setOrthoMatrix(Matrix4f pShaderOrthoMatrix) {
        this.shaderOrthoMatrix = pShaderOrthoMatrix;
    }

    public void process(float pPartialTicks) {
        this.inTarget.unbindWrite();
        float f = (float)this.outTarget.width;
        float f1 = (float)this.outTarget.height;
        RenderSystem.viewport(0, 0, (int)f, (int)f1);
        this.effect.setSampler("DiffuseSampler", this.inTarget::getColorTextureId);

        for(int i = 0; i < this.auxAssets.size(); ++i) {
            this.effect.setSampler(this.auxNames.get(i), this.auxAssets.get(i));
            this.effect.safeGetUniform("AuxSize" + i).set((float) this.auxWidths.get(i), (float) this.auxHeights.get(i));
        }
        this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
        this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
        this.effect.safeGetUniform("OutSize").set(f, f1);
        this.effect.safeGetUniform("Time").set(pPartialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        this.effect.safeGetUniform("ScreenSize").set((float)minecraft.getWindow().getWidth(), (float)minecraft.getWindow().getHeight());
        this.effect.apply();
        this.outTarget.clear(Minecraft.ON_OSX);
        this.outTarget.bindWrite(false);
        RenderSystem.depthFunc(519);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(0.0D, 0.0D, 500.0D).endVertex();
        bufferbuilder.vertex(f, 0.0D, 500.0D).endVertex();
        bufferbuilder.vertex(f, f1, 500.0D).endVertex();
        bufferbuilder.vertex(0.0D, f1, 500.0D).endVertex();
        BufferUploader.draw(bufferbuilder.end());
        RenderSystem.depthFunc(515);
        this.effect.clear();
        this.outTarget.unbindWrite();
        this.inTarget.unbindRead();

        for(Object object : this.auxAssets) {
            if (object instanceof RenderTarget) {
                ((RenderTarget)object).unbindRead();
            }
        }

    }

    public IOSafeEffectInstance getEffect() {
        return this.effect;
    }
}