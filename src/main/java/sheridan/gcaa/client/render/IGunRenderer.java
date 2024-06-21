package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.render.gui.AttachmentsGuiContext;
import sheridan.gcaa.items.guns.IGun;

@OnlyIn(Dist.CLIENT)
public interface IGunRenderer {
    void renderInGuiScreen(ItemStack itemStack, GuiGraphics guiGraphics, IGun gun, IGunModel model, AttachmentsGuiContext attachmentsGuiContext);


    void justRenderModel(ItemStack itemStackIn, ItemDisplayContext transformTypeIn,
                         PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, IGun gun, IGunModel model, DisplayData transformData);

    void renderWithEntity(LivingEntity entityIn, PoseStack stackIn,
                          ItemStack itemStackIn, ItemDisplayContext type, MultiBufferSource bufferIn, IGun gun,
                          int combinedLightIn, int combinedOverlayIn, boolean leftHand, IGunModel model, DisplayData displayData);
}
