package sheridan.gcaa.client.model.attachments.handguard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
public class ARLightHandguardShortModel implements IAttachmentModel, ISlotProviderModel {
    private final ResourceLocation TEXTURE = StatisticModel.AR_LIGHT_HANDGUARD.texture;
    private final ModelPart model, rail_front_right, rail_front_left, rail_grip;

    public ARLightHandguardShortModel() {
        model = StatisticModel.AR_LIGHT_HANDGUARD.get("short").meshing();
        model.resetChildLayerName("s_handguard_right2", "s_handguard_right");
        model.resetChildLayerName("s_handguard_left2", "s_handguard_left");
        model.resetChildLayerName("s_handguard_grip2", "s_handguard_grip");
        model.resetChildLayerName("s_handguard_scope2", "s_handguard_scope");
        rail_front_right = model.getChild("rail_front_right2").meshing();
        rail_front_left = model.getChild("rail_front_left2").meshing();
        rail_grip = model.getChild("rail_grip2").meshing();
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
        if (model.hasChild(modelSlotName)) {
            model.getChild(modelSlotName).translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return model.hasChild(modelSlotName);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        model.copyFrom(pose);
        AttachmentRenderEntry s_handguard_scope = attachmentRenderEntry.getChild("s_handguard_scope");
        AttachmentRenderEntry s_handguard_left = attachmentRenderEntry.getChild("s_handguard_left");
        AttachmentRenderEntry s_handguard_right = attachmentRenderEntry.getChild("s_handguard_right");
        AttachmentRenderEntry s_handguard_grip = attachmentRenderEntry.getChild("s_handguard_grip");
        rail_front_right.visible = s_handguard_right != null;
        rail_front_left.visible = s_handguard_left != null;
        rail_grip.visible = s_handguard_grip != null;
        context.render(model, context.solidNoCull(TEXTURE));
        context.pushPose().translateTo(model);
        context.renderEntry(s_handguard_scope, model.getChild("s_handguard_scope"));
        context.renderEntry(s_handguard_left, model.getChild("s_handguard_left"));
        context.renderEntry(s_handguard_right, model.getChild("s_handguard_right"));
        context.renderEntry(s_handguard_grip, model.getChild("s_handguard_grip"));
        context.popPose();
        model.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return model;
    }
}
