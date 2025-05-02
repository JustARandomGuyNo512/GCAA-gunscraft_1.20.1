package sheridan.gcaa.client.model.attachments.other;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class CantedSightSetModel implements IAttachmentModel, ISlotProviderModel {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/others/canted_sight_set.png");
    private final ModelPart root, s_canted_scope;

    public CantedSightSetModel() {
        root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/others/canted_sight_set.geo.json")).bakeRoot().getChild("root");
        root.meshingAll();
        s_canted_scope = root.getChild("s_canted_scope");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
        root.translateAndRotate(poseStack);
        s_canted_scope.translateAndRotate(poseStack);
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_canted_scope".equals(modelSlotName);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        root.copyFrom(pose);
        context.render(root, context.solidMipMap(TEXTURE));
        AttachmentRenderEntry sight = attachmentRenderEntry.getChild("s_canted_scope");
        if (sight != null) {
            context.pushPose().translateTo(root);
            context.renderEntry(sight, s_canted_scope);
            context.popPose();
        }
        root.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return root;
    }
}
