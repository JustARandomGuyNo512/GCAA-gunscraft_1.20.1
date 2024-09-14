package sheridan.gcaa.client.model.attachments.akStuff;

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
public class AKImprovedDustCoverBModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart root;
    private final ModelPart slot_scope;
    private final ResourceLocation texture = new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/improved_dust_cover_b.png");

    public AKImprovedDustCoverBModel() {
        this.root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/improved_dust_cover_b.geo.json"))
                .bakeRoot().getChild("root").meshing();
        this.slot_scope = this.root.getChild("s_dust_cover_scope");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName) {
        if ("s_dust_cover_scope".equals(modelSlotName)) {
            slot_scope.translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_dust_cover_scope".equals(modelSlotName);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        root.copyFrom(pose);
        context.pushPose();
        context.render(root, context.getBuffer(RenderType.entityCutout(texture)));
        context.translateTo(root);
        context.renderEntry(attachmentRenderEntry.getChild("s_dust_cover_scope"), slot_scope);
        context.popPose();
        root.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }
}
