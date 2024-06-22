package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
public class GunRenderer implements IGunRenderer{

    @Override
    public void renderInGuiScreen(ItemStack itemStack, GuiGraphics guiGraphics, IGun gun, IGunModel model, AttachmentsGuiContext attachmentsGuiContext) {

    }

    @Override
    public void justRenderModel(ItemStack itemStackIn, ItemDisplayContext transformTypeIn, PoseStack poseStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, IGun gun, IGunModel model, DisplayData displayData) {
        if (model != null && displayData != null) {
            poseStackIn.mulPose(Axis.ZP.rotationDegrees(180));
            displayData.applyTransform(transformTypeIn, poseStackIn);
            model.render(new GunRenderContext(bufferIn, poseStackIn, itemStackIn, gun, transformTypeIn, combinedLightIn, combinedOverlayIn));
        }
    }

    @Override
    public void renderWithEntity(LivingEntity entityIn, PoseStack stackIn, ItemStack itemStackIn, ItemDisplayContext type, MultiBufferSource bufferIn, IGun gun, int combinedLightIn, int combinedOverlayIn, boolean leftHand, IGunModel model, DisplayData displayData) {
        if (entityIn == null) {
            justRenderModel(itemStackIn, type, stackIn, bufferIn, combinedLightIn, combinedOverlayIn, gun, model, displayData);
            return;
        }
        if (model != null && displayData != null) {
            boolean isFirstPerson = type.firstPerson();
            if (isFirstPerson) {
                PoseStack poseStack = new PoseStack();
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                GlobalWeaponBobbing.INSTANCE.handleTranslation(poseStack);
                displayData.applyTransform(type, poseStack);
                model.render(new GunRenderContext(bufferIn, poseStack, itemStackIn, gun, type, combinedLightIn, combinedOverlayIn));
            } else {
                stackIn.mulPose(Axis.ZP.rotationDegrees(180));
                displayData.applyTransform(type, stackIn);
                model.render(new GunRenderContext(bufferIn, stackIn, itemStackIn, gun, type, combinedLightIn, combinedOverlayIn));
            }
        }
    }
}