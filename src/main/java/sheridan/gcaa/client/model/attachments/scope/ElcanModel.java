package sheridan.gcaa.client.model.attachments.scope;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.ScopeModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class ElcanModel extends ScopeModel {
    private ModelPart body, crosshair, background, glass_shape, body_low;
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/elcan/elcan.png");
    private static final ResourceLocation CROSSHAIR_TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/elcan/elcan_crosshair.png");

    public ElcanModel() {
        super(ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/scopes/elcan/elcan.geo.json")).bakeRoot().getChild("root"));
        body = root.getChild("body");
        crosshair = root.getChild("crosshair");
        min_z_dis = root.getChild("min_dis_z");
        background = root.getChild("back_ground");
        glass_shape = root.getChild("glass_shape");
        back_glass = root.getChild("back_glass");
        body_low = root.getChild("body_low");
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
            context.render(body_low, context.getBuffer(RenderType.entityCutout(TEXTURE)));
        } else {
            boolean active = context.isEffectiveSight(attachmentRenderEntry) && Clients.isInAds() && Clients.getAdsProgress() > 0.95f;
            SightViewRenderer.renderScope(active, false, 0.7f, 0.86f, context,
                    CROSSHAIR_TEXTURE, TEXTURE, crosshair, glass_shape, back_glass, background, body);
        }
    }
}
