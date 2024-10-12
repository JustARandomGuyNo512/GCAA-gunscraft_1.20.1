package sheridan.gcaa.client.model.attachments.scope;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.ScopeModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class ScopeX10Model extends ScopeModel {
    private final ModelPart root;
    private final ModelPart crosshair;
    private final ModelPart back_glass;
    private final ModelPart body;
    private final ModelPart glass_shape;
    private final ModelPart back_ground;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10/scope_x10.png");
    private static final ResourceLocation CROSSHAIR_TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10/scope_x10_crosshair.png");


    public ScopeX10Model() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10/scope_x10.geo.json")).bakeRoot().getChild("root");
        crosshair = root.getChild("crosshair");
        back_glass = root.getChild("back_glass").meshing();
        body = root.getChild("body").meshing();
        glass_shape = root.getChild("glass_shape").meshing();
        back_ground = root.getChild("back_ground");
    }

    @Override
    protected void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        boolean active = context.isEffectiveSight(attachmentRenderEntry) && Clients.isInAds() && Clients.getAdsProgress() == 1f;
        SightViewRenderer.renderScope(active, false, 0.75f, 0.95f, context,
                CROSSHAIR_TEXTURE, TEXTURE, crosshair, glass_shape, back_glass, back_ground, body);
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
    public float getMinDisZDistance(float prevAdsProgress) {
        return !useModelFovModifyWhenAds() ? 0.95f : calcMinDisZDistance(0.36f, Mth.lerp(prevAdsProgress, 70, modelFovModifyWhenAds()));
                //calcMinDisZDistance(0.36f, prevFov);
    }


}
