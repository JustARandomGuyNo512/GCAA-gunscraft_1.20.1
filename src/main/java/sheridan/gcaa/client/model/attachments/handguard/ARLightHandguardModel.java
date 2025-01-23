package sheridan.gcaa.client.model.attachments.handguard;

import com.mojang.blaze3d.vertex.PoseStack;
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
public class ARLightHandguardModel implements IAttachmentModel, ISlotProviderModel {
    private final ResourceLocation TEXTURE = StatisticModel.AR_LIGHT_HANDGUARD.texture;
    private final ModelPart model, rail_grip, rail_front, rail_front_left, rail_front_right, rail_front_left_front, rail_front_right_front;

    public ARLightHandguardModel() {
        model = StatisticModel.AR_LIGHT_HANDGUARD.get("long").meshing();
        rail_grip = model.getChild("rail_grip").meshing();
        rail_front = model.getChild("rail_front").meshing();
        rail_front_left = model.getChild("rail_front_left").meshing();
        rail_front_right = model.getChild("rail_front_right").meshing();
        rail_front_left_front = model.getChild("rail_front_left_front").meshing();
        rail_front_right_front = model.getChild("rail_front_right_front").meshing();
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
        AttachmentRenderEntry s_handguard_left = attachmentRenderEntry.getChild("s_handguard_left");
        AttachmentRenderEntry s_handguard_right = attachmentRenderEntry.getChild("s_handguard_right");
        AttachmentRenderEntry s_handguard_grip = attachmentRenderEntry.getChild("s_handguard_grip");
        AttachmentRenderEntry s_handguard_front = attachmentRenderEntry.getChild("s_handguard_front");
        AttachmentRenderEntry s_handguard_left_front = attachmentRenderEntry.getChild("s_handguard_left_front");
        AttachmentRenderEntry s_handguard_right_front = attachmentRenderEntry.getChild("s_handguard_right_front");
        rail_front_right.visible = s_handguard_right != null;
        rail_front_left.visible = s_handguard_left != null;
        rail_grip.visible = s_handguard_grip != null;
        rail_front.visible = s_handguard_front != null;
        rail_front_left_front.visible = s_handguard_left_front != null;
        rail_front_right_front.visible = s_handguard_right_front != null;
        context.render(model, context.solidNoCullMipMap(TEXTURE));
        context.pushPose().translateTo(model);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_scope"), model.getChild("s_handguard_scope"));
        context.renderEntry(s_handguard_left, model.getChild("s_handguard_left"));
        context.renderEntry(s_handguard_right, model.getChild("s_handguard_right"));
        context.renderEntry(s_handguard_grip, model.getChild("s_handguard_grip"));
        context.renderEntry(s_handguard_front, model.getChild("s_handguard_front"));
        context.renderEntry(s_handguard_left_front, model.getChild("s_handguard_left_front"));
        context.renderEntry(s_handguard_right_front, model.getChild("s_handguard_right_front"));
        context.popPose();
        model.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return model;
    }
}
