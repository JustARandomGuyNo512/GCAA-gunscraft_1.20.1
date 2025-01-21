package sheridan.gcaa.client.model.attachments.handguard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AKImprovedHandguardModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart handguard;
    private final ResourceLocation texture = StatisticModel.AK_STUFF1.texture;
    private final ModelPart slot_handguard_grip;
    private final ModelPart slot_handguard_sight;
    private final ModelPart slot_handguard_left;
    private final ModelPart slot_handguard_right;
    private final ModelPart low;
    private final Set<String> slotNames = new HashSet<>();

    public AKImprovedHandguardModel() {
        handguard = StatisticModel.AK_STUFF1.get("handguard");
        slotNames.add("s_handguard_grip");
        slotNames.add("s_handguard_sight");
        slotNames.add("s_handguard_left");
        slotNames.add("s_handguard_right");
        slot_handguard_grip = handguard.getChild("s_handguard_grip");
        slot_handguard_sight = handguard.getChild("s_handguard_sight");
        slot_handguard_left = handguard.getChild("s_handguard_left");
        slot_handguard_right = handguard.getChild("s_handguard_right");
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("ak_handguard1_rail_set1").getChild("handguard").meshing();
    }


    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        handguard.copyFrom(pose);
        if (context.useLowQuality()) {
            low.copyFrom(handguard);
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
        } else {
            context.render(handguard, context.getBuffer(RenderType.entityCutout(texture)));
        }
        context.pushPose().translateTo(handguard);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_sight"), slot_handguard_sight);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_left"), slot_handguard_left);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_right"), slot_handguard_right);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_grip"), slot_handguard_grip);
        context.popPose();
        handguard.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return handguard;
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
        if (handguard.hasChild(modelSlotName)) {
            handguard.getChild(modelSlotName).translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return slotNames.contains(modelSlotName);
    }
}
