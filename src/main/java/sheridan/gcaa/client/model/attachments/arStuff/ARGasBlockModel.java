package sheridan.gcaa.client.model.attachments.arStuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.statistic.ARStuff1;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class ARGasBlockModel implements IAttachmentModel {
    private final ModelPart gas_block;
    private final ResourceLocation texture = ARStuff1.TEXTURE;

    public ARGasBlockModel() {
        gas_block = ARStuff1.get("gas_block");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        gas_block.copyFrom(pose);
        context.render(gas_block, context.getBuffer(RenderType.entityCutout(texture)));
        gas_block.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return gas_block;
    }

}
