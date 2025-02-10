package sheridan.gcaa.client.model.attachments.akStuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class AKRailBracketModel implements IAttachmentModel, ISlotProviderModel {
    public final ModelPart rail;
    private final ModelPart slot_scope;
    private final ModelPart low;
    private final ResourceLocation texture = StatisticModel.AK_STUFF1.texture;

    public AKRailBracketModel() {
        rail = StatisticModel.AK_STUFF1.get("rail");
        slot_scope = rail.getChild("s_rail_bracket_scope");
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("ak_handguard1_rail_set1").getChild("rail").meshing();
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
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
        if (context.useLowQuality()) {
            low.copyFrom(rail);
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
        } else {
            context.render(rail, context.getBuffer(RenderType.entityCutout(texture)));
        }
        context.pushPose().translateTo(rail);
        context.renderEntry(attachmentRenderEntry.getChild("s_rail_bracket_scope"), slot_scope);
        context.popPose();
        rail.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return rail;
    }
}
