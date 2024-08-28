package sheridan.gcaa.client.model.attachments.scope;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.IAttachmentModel;
import sheridan.gcaa.client.model.attachments.IScopeModel;
import sheridan.gcaa.client.model.attachments.ISightModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class ScopeX10Model implements IAttachmentModel, IScopeModel {
    public final ModelPart root;
    public final ModelPart crosshair;
    public final ModelPart back_glass;
    public final ModelPart body;
    public final ModelPart glass_shape;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10.png");

    public ScopeX10Model() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10.geo.json")).bakeRoot().getChild("root");
        crosshair = root.getChild("crosshair");
        back_glass = root.getChild("back_glass").meshing();
        body = root.getChild("body").meshing();
        glass_shape = root.getChild("glass_shape").meshing();
    }

    @Override
    public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        root.copyFrom(pose);
        //root.translateAndRotate(context.poseStack);
        context.render(root, context.getBuffer(RenderType.entityCutout(TEXTURE)));
        root.resetPose();
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    public void handleCrosshairTranslation(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        crosshair.translateAndRotate(poseStack);
    }

    @Override
    public ModelPart getCrosshair() {
        return crosshair;
    }

    @Override
    public float getMinDisZDistance() {
        return 1f;
    }
}
