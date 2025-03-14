package sheridan.gcaa.client.model.attachments.sight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class Okp7AModel extends SightModel {
    public static final ResourceLocation CROSSHAIR = new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7_a.png");
    private final ModelPart root;
    private final ModelPart crosshair;
    private final ModelPart body;
    private final ModelPart low;

    public Okp7AModel() {
        this.root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/attachments/sights/okp7_a.geo.json")).bakeRoot().getChild("root");
        this.body = this.root.getChild("body").meshing();
        this.low = this.root.getChild("low").meshing();
        this.crosshair = this.root.getChild("crosshair_rifle");
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
}
