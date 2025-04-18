package sheridan.gcaa.client.model.attachments.scope;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.ScopeModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class ScopeX10Model extends ScopeModel implements ISlotProviderModel {
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ModelPart glass_shape;
    private final ModelPart back_ground;
    private final ModelPart sub_scope_adapter;
    private final ModelPart sub_scope;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10/scope_x10.png");
    private static final ResourceLocation CROSSHAIR_TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10/scope_x10_crosshair.png");


    public ScopeX10Model() {
        super(ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/scope_x10/scope_x10.geo.json")).bakeRoot().getChild("root"));
        crosshair = root.getChild("crosshair");
        back_glass = root.getChild("back_glass");
        body = root.getChild("body").meshing();
        glass_shape = root.getChild("glass_shape");
        back_ground = root.getChild("back_ground");
        sub_scope_adapter = root.getChild("sub_scope_adapter");
        min_z_dis = root.getChild("min_z_dis");
        sub_scope = root.getChild("s_sub_scope");
    }

    @Override
    protected void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        boolean active = context.isEffectiveSight(attachmentRenderEntry) && Clients.isInAds() && Clients.getAdsProgress() > 0.95f;
        AttachmentRenderEntry subScope = attachmentRenderEntry.getChild("s_sub_scope");
        sub_scope_adapter.visible = subScope != null;
        SightViewRenderer.renderScope(active, false, 0.75f, 1.03f, context,
                CROSSHAIR_TEXTURE, TEXTURE, crosshair, glass_shape, back_glass, back_ground, body, sub_scope_adapter);
        context.renderEntry(subScope, sub_scope);
    }

    @Override
    public ModelPart getRoot(IGun gun) {
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
    public void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun) {
        if (root.hasChild(modelSlotName)) {
            root.getChild(modelSlotName).translateAndRotate(poseStack);
        }
    }

    @Override
    public boolean hasSlot(String modelSlotName) {
        return "s_sub_scope".equals(modelSlotName);
    }
}
