package sheridan.gcaa.client.model.attachments.akStuff;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class AKImprovedDustCoverModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart root;
    private final ModelPart dust_cover;
    private final ModelPart slot_scope;
    private final ModelPart low;
    private final ResourceLocation texture = new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/improved_dust_cover.png");

    public AKImprovedDustCoverModel() {
        this.root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/improved_dust_cover.geo.json"))
                .bakeRoot().getChild("root");
        this.dust_cover = root.getChild("dust_cover").meshing();
        this.slot_scope = dust_cover.getChild("s_dust_cover_scope");
        this.low = root.getChild("low").meshing();
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName) {
        if ("s_dust_cover_scope".equals(modelSlotName)) {
            root.translateAndRotate(poseStack);
            dust_cover.translateAndRotate(poseStack);
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
        low.visible = context.useLowQuality();
        dust_cover.visible = !low.visible;
        context.render(root, context.getBuffer(RenderType.entityCutout(texture)));
        context.pushPose().translateTo(root, dust_cover).renderEntry(attachmentRenderEntry.getChild("s_dust_cover_scope"), slot_scope);
        context.popPose();
        root.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }
}
