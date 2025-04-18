package sheridan.gcaa.client.model.attachments.scope;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.ISlotProviderModel;
import sheridan.gcaa.client.model.attachments.ScopeModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.attachments.StatisticModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class AcogModel extends ScopeModel implements ISlotProviderModel {
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ModelPart glass_shape;
    private final ModelPart back_ground;
    private final ModelPart low;
    private final ModelPart sub_scope_adapter;
    private final ModelPart sub_scope;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/acog/acog.png");
    private static final ResourceLocation CROSSHAIR_TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/acog/acog_crosshair.png");

    public AcogModel() {
        super(ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/acog/acog.geo.json")).bakeRoot().getChild("root"));
        crosshair = root.getChild("crosshair");
        back_glass = root.getChild("back_glass");
        body = root.getChild("body");
        glass_shape = root.getChild("glass_shape");
        sub_scope_adapter = root.getChild("sub_scope_adapter");
        sub_scope = root.getChild("s_sub_scope");
        back_ground = root.getChild("back_ground");
        min_z_dis = root.getChild("min_z_dis");
        low = StatisticModel.ATTACHMENTS_LOW_COLLECTION1.get("acogX4");
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
    protected void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        if (context.useLowQuality()) {
            context.render(low, context.getBuffer(RenderType.entityCutout(StatisticModel.ATTACHMENTS_LOW_COLLECTION1.texture)));
        } else {
            AttachmentRenderEntry subScope = attachmentRenderEntry.getChild("s_sub_scope");
            sub_scope_adapter.visible = subScope != null;
            boolean active = context.isEffectiveSight(attachmentRenderEntry) && Clients.isInAds() && Clients.getAdsProgress() > 0.95f;
            SightViewRenderer.renderScope(active, false, 0.35f, 0.635f, context,
                    CROSSHAIR_TEXTURE, TEXTURE, crosshair, glass_shape, back_glass, back_ground, body, sub_scope_adapter);
            context.renderEntry(subScope, sub_scope);
        }
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
