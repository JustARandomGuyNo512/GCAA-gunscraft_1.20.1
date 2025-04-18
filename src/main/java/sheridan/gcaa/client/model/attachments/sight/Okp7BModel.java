package sheridan.gcaa.client.model.attachments.sight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.CommonSightModel;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

public class Okp7BModel extends CommonSightModel {
    public static final ResourceLocation CROSSHAIR = new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7_b.png");
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ModelPart low;
    private final ModelPart min_z_dis;

    public Okp7BModel() {
        super(ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7_b.geo.json")).bakeRoot().getChild("root"));
        this.body = this.root.getChild("body");
        this.low = this.root.getChild("low");
        this.crosshair = this.root.getChild("crosshair_rifle");
        this.min_z_dis = this.root.getChild("min_z_dis");
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
    protected void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        if (context.useLowQuality()) {
            context.render(low, context.getBuffer(RenderType.entityCutout(TEXTURE)));
            return;
        }
        SightViewRenderer.renderRedDot(context.isEffectiveSight(attachmentRenderEntry), 0.2f, context, TEXTURE, CROSSHAIR, crosshair, body);
    }

    @Override
    protected ModelPart getMinZDis() {
        return min_z_dis;
    }
}
