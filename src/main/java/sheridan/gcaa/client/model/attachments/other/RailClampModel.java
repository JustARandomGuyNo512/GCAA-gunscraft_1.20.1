package sheridan.gcaa.client.model.attachments.other;

import com.mojang.blaze3d.vertex.PoseStack;
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
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class RailClampModel implements IAttachmentModel, ISlotProviderModel {
    private final ModelPart beretta_686;
    private final ModelPart mp5;
    private final ModelPart g3;
    private final ModelPart beretta_scope;
    private final ModelPart rail_scope_mp5;
    private final ModelPart rail_scope_g3;
    private final ResourceLocation TEXTURE = StatisticModel.RAIL_CLAMP.texture;

    public RailClampModel() {
        beretta_686 = StatisticModel.RAIL_CLAMP.get("beretta_686").meshing();
        mp5 = StatisticModel.RAIL_CLAMP.get("mp5").meshing();
        g3 = StatisticModel.RAIL_CLAMP.get("g3").meshing();
        beretta_scope = beretta_686.getChild("s_beretta_scope");
        rail_scope_mp5 = mp5.getChild("s_rail_scope_mp5");
        rail_scope_g3 = g3.getChild("s_rail_scope_g3");
    }

    @Override
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
        if (gun == ModItems.BERETTA_686.get()) {
            beretta_686.translateAndRotate(poseStack);
            beretta_scope.translateAndRotate(poseStack);
        } else if (gun.getGunType() == IGun.GunType.SMG){
            mp5.translateAndRotate(poseStack);
            rail_scope_mp5.translateAndRotate(poseStack);
        } else {
            g3.translateAndRotate(poseStack);
            rail_scope_g3.translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_rail_clamp_scope".equals(modelSlotName);
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        IGun gun = context.gun;
        ModelPart model = getRoot(gun);
        model.copyFrom(pose);
        context.render(model, context.getBuffer(RenderType.entityCutout(TEXTURE)));
        context.pushPose().translateTo(model).renderEntry(attachmentRenderEntry.getChild("s_rail_clamp_scope"), model.getUniqueChild());
        context.popPose();
        model.resetPose();
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        if (gun == ModItems.BERETTA_686.get()) {
            return beretta_686;
        } else if (gun.getGunType() == IGun.GunType.SMG){
            return mp5;
        } else {
            return g3;
        }
    }
}
