package sheridan.gcaa.client.model.attachments.grip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.ArmRendererModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class VerticalGripModel extends ArmRendererModel implements IAttachmentModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart left_arm;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.png");

    public VerticalGripModel() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.geo.json")).bakeRoot().getChild("root");
        body = root.getChild("body").meshing();
        left_arm = root.getChild("left_arm");
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        root.copyFrom(pose);
        context.pushPose().translateTo(root);
        context.render(body, vertexConsumer);
        renderArm(false, RenderAndMathUtils.copyPoseStack(context.poseStack), context, attachmentRenderEntry);
        context.popPose();
        root.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    protected ModelPart getLeftArm() {
        return left_arm;
    }

    @Override
    protected ModelPart getRightArm() {
        return null;
    }

    @Override
    protected PoseStack lerpArmPose(boolean mainHand, PoseStack prevPose, GunRenderContext context) {
        return LerpReloadAnimationPose(false, context, prevPose);
    }

    @Override
    protected boolean shouldRenderArm(boolean mainHand, GunRenderContext context, AttachmentRenderEntry entry) {
        return defaultShouldRenderArm(mainHand, context, entry);
    }

}