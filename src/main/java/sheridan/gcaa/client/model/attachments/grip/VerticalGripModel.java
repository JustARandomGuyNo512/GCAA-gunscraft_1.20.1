package sheridan.gcaa.client.model.attachments.grip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.ArmRendererModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.IDirectionalModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class VerticalGripModel extends ArmRendererModel implements IAttachmentModel, IDirectionalModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart left_arm_new;
    private final ModelPart low;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.png");

    public VerticalGripModel() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/grips/vertical_grip.geo.json")).bakeRoot().getChild("root");
        body = root.getChild("body").meshing();
        left_arm = root.getChild("left_arm");
        left_arm_new = root.getChild("left_arm_new");
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("grip").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        context.pushPose();
        initTranslation(attachmentRenderEntry, context, pose);
        if (context.useLowQuality()) {
            low.copyFrom(body);
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
        } else {
            context.render(body, context.getBuffer(RenderType.entityCutout(TEXTURE)));
        }
        renderArm(false, RenderAndMathUtils.copyPoseStack(context.poseStack), context, attachmentRenderEntry);
        context.popPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    protected ModelPart getLeftArm(GunRenderContext context) {
        return context.renderArmNew ? left_arm_new : left_arm;
    }

    @Override
    protected ModelPart getRightArm(GunRenderContext context) {
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

    @Override
    public byte getDirection() {
        return LOWER;
    }

    @Override
    public ModelPart root() {
        return getRoot();
    }
}
