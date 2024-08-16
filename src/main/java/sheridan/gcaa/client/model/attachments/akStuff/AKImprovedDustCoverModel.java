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
public class AKImprovedDustCoverModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart dust_cover;
    private final ModelPart slot_scope;
    private final ResourceLocation texture = new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/improved_dust_cover.png");

    public AKImprovedDustCoverModel() {
        this.dust_cover = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/ak_stuff/improved_dust_cover.geo.json"))
                .bakeRoot().getChild("root").getChild("dust_cover").meshing();
        this.slot_scope = this.dust_cover.getChild("s_dust_cover_scope");
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
        dust_cover.copyFrom(pose);
        context.render(dust_cover, context.getBuffer(RenderType.entityCutout(texture)));
        context.translateTo(dust_cover);
        AttachmentRenderEntry scope = attachmentRenderEntry.getChild("dust_cover_scope");
        if (scope != null) {
            scope.render(context, slot_scope);
        }
        dust_cover.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return dust_cover;
    }
}
