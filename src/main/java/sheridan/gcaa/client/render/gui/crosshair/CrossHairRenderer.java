package sheridan.gcaa.client.render.gui.crosshair;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class CrossHairRenderer {
    private static final float BASE_SCALE = 3;
    private static final float SPREAD_SIZE_FACTOR = 5f;
    public static final CrossHairRenderer INSTANCE = new CrossHairRenderer();
    public static final ResourceLocation CROSSHAIR = new ResourceLocation(GCAA.MODID, "textures/gui/crosshair/crosshair.png");
    private static float tempSpread;
    public void render(int index, int singleQuadSize, IGun gun, GuiGraphics guiGraphics, Player player, ItemStack itemStack, Window window, float particleTick) {
        index = Mth.clamp(index, 0, 5);
        int textureSize = singleQuadSize * 5;
        int partSize = singleQuadSize - 1;
        float vOffset = singleQuadSize * index;
        float centerX = (window.getGuiScaledWidth() - partSize) / 2f;
        float centerY = (window.getGuiScaledHeight() - partSize) / 2f;
        float spread = (int) (Clients.mainHandStatus.spread * SPREAD_SIZE_FACTOR) + BASE_SCALE + partSize / 2f;
        float currentSpread = Mth.lerp(particleTick, tempSpread, spread);
        tempSpread = spread;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        blit(CROSSHAIR, centerX, centerY, 0, vOffset, partSize, partSize, textureSize, textureSize, guiGraphics.pose());
        blit(CROSSHAIR, centerX, centerY - currentSpread, singleQuadSize, vOffset, partSize, partSize, textureSize, textureSize, guiGraphics.pose());
        blit(CROSSHAIR, centerX - currentSpread, centerY, singleQuadSize * 2, vOffset, partSize, partSize, textureSize, textureSize, guiGraphics.pose());
        blit(CROSSHAIR, centerX, centerY + currentSpread, singleQuadSize * 3, vOffset, partSize, partSize, textureSize, textureSize, guiGraphics.pose());
        blit(CROSSHAIR, centerX + currentSpread, centerY, singleQuadSize * 4, vOffset, partSize, partSize, textureSize, textureSize, guiGraphics.pose());
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    void blit(ResourceLocation pAtlasLocation, float pX, float pY, float pUOffset, float pVOffset, float pWidth, float pHeight, float pTextureWidth, float pTextureHeight, PoseStack stack) {
        this.blit(pAtlasLocation, pX, pY, pWidth, pHeight, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight, stack);
    }

    void blit(ResourceLocation pAtlasLocation, float pX, float pY, float pWidth, float pHeight, float pUOffset, float pVOffset, float pUWidth, float pVHeight, float pTextureWidth, float pTextureHeight, PoseStack stack) {
        this._blit(pAtlasLocation, pX, pX + pWidth, pY, pY + pHeight, pUWidth, pVHeight, pUOffset, pVOffset, pTextureWidth, pTextureHeight, stack);
    }

    void _blit(ResourceLocation pAtlasLocation, float pX1, float pX2, float pY1, float pY2, float pUWidth, float pVHeight, float pUOffset, float pVOffset, float pTextureWidth, float pTextureHeight, PoseStack stack) {
        this.innerBlit(pAtlasLocation, pX1, pX2, pY1, pY2,  pUOffset / pTextureWidth, (pUOffset + pUWidth) / pTextureWidth, (pVOffset + 0.0F) / pTextureHeight, (pVOffset + pVHeight) / pTextureHeight, stack);
    }

    void innerBlit(ResourceLocation pAtlasLocation, float pX1, float pX2, float pY1, float pY2, float pMinU, float pMaxU, float pMinV, float pMaxV, PoseStack stack) {
        RenderSystem.setShaderTexture(0, pAtlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = stack.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, pX1, pY1, 0).uv(pMinU, pMinV).endVertex();
        bufferbuilder.vertex(matrix4f, pX1, pY2, 0).uv(pMinU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, pX2, pY2, 0).uv(pMaxU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, pX2, pY1, 0).uv(pMaxU, pMinV).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
