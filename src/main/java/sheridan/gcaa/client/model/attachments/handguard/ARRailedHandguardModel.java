package sheridan.gcaa.client.model.attachments.handguard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class ARRailedHandguardModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart root, body, s_handguard_grip, s_handguard_scope, s_handguard_left, s_handguard_right;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/ar_stuff/ar_railed_handguard.png");

    public ARRailedHandguardModel() {
        root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/ar_stuff/ar_railed_handguard.geo.json")).bakeRoot().getChild("root");
        body = root.getChild("body").meshing();
        s_handguard_grip = root.getChild("s_handguard_grip");
        s_handguard_scope = root.getChild("s_handguard_scope");
        s_handguard_left = root.getChild("s_handguard_left");
        s_handguard_right = root.getChild("s_handguard_right");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName) {
        if (root.hasChild(modelSlotName)) {
            root.getChild(modelSlotName).translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return root.hasChild(modelSlotName);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        root.copyFrom(pose);
        context.pushPose().translateTo(root);
        context.render(body, context.getBuffer(RenderType.entityCutout(TEXTURE)));
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_scope"), s_handguard_scope);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_left"), s_handguard_left);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_right"), s_handguard_right);
        context.renderEntry(attachmentRenderEntry.getChild("s_handguard_grip"), s_handguard_grip);
        context.popPose();
        root.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }
}
