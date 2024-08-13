package sheridan.gcaa.client.model.attachments.akStuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.muzzle.statistic.AKStuff1;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class AKRailBracketModel implements IAttachmentModel, ISlotProviderModel {
    public final ModelPart rail;
    private final ModelPart slot_scope;
    private final ResourceLocation texture = AKStuff1.TEXTURE;

    public AKRailBracketModel() {
        rail = AKStuff1.get("rail").meshing();
        slot_scope = rail.getChild("s_rail_bracket_scope");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName) {
        if ("s_rail_bracket_scope".equals(modelSlotName)) {
            slot_scope.translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_rail_bracket_scope".equals(modelSlotName);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        rail.copyFrom(pose);
        context.render(rail, context.getBuffer(RenderType.entityCutoutNoCull(texture)));
        rail.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return rail;
    }
}
