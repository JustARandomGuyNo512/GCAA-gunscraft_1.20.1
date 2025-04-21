package sheridan.gcaa.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import sheridan.gcaa.client.model.attachments.SightModel;
import sheridan.gcaa.client.model.attachments.SightViewRenderer;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

public class CommonSightModel extends SightModel {
    protected final ModelPart root;
    protected ResourceLocation texture;
    protected ResourceLocation lowTexture;
    protected ResourceLocation crosshairTexture;
    private final ModelPart crosshair;
    private final ModelPart body;
    private ModelPart low;
    private final ModelPart min_z_dis;
    private final float crosshairScale;

    public CommonSightModel(ModelPart root,
                            ResourceLocation texture,
                            ResourceLocation lowTexture,
                            ResourceLocation crosshairTexture,
                            String low, String min_z_dis, String body, String crosshair, float crosshairScale)  {
        this.root = root;
        this.root.meshingAll();
        this.low = low != null && root.hasChild(low) ? root.getChild(low) : null;
        this.min_z_dis = min_z_dis != null && root.hasChild(min_z_dis) ? root.getChild(min_z_dis) : null;
        this.body = root.getChild(body);
        this.crosshair = root.getChild(crosshair);
        this.texture = texture;
        this.crosshairScale = crosshairScale;
        this.crosshairTexture = crosshairTexture;
        this.lowTexture = lowTexture;
    }

    public CommonSightModel(ResourceLocation root,
                            ResourceLocation texture,
                            ResourceLocation lowTexture,
                            ResourceLocation crosshairTexture,
                            String low, String min_z_dis, String body, String crosshair, float crosshairScale)  {
        this(ArsenalLib.loadBedRockGunModel(root).bakeRoot().getChild("root"),
                texture, lowTexture, crosshairTexture, low, min_z_dis, body, crosshair, crosshairScale);
    }

    public CommonSightModel(ResourceLocation root,
                            ResourceLocation texture,
                            ResourceLocation crosshairTexture,
                            String min_z_dis, String body, String crosshair, float crosshairScale)  {
        this(root, texture, null, crosshairTexture, null, min_z_dis, body, crosshair, crosshairScale);
    }

    public CommonSightModel(ModelPart root,
                            ResourceLocation texture,
                            ResourceLocation crosshairTexture,
                            String min_z_dis, String body, String crosshair, float crosshairScale)  {
        this(root, texture, null, crosshairTexture, null, min_z_dis, body, crosshair, crosshairScale);
    }

    protected void setLow(ModelPart low, ResourceLocation lowTexture) {
        this.low = low;
        this.lowTexture = lowTexture;
    }

    @Override
    protected void renderModel(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {
        if (context.useLowQuality() && low != null && lowTexture != null) {
            context.render(low, context.getBuffer(RenderType.entityCutout(lowTexture)));
            return;
        }
        SightViewRenderer.renderRedDot(context.isEffectiveSight(attachmentRenderEntry), crosshairScale, context, texture, crosshairTexture, crosshair, body);
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return root;
    }

    Vector3f t1 = new Vector3f();
    Vector3f t2 = new Vector3f();
    @Override
    public float handleMinZTranslation(PoseStack poseStack) {
        if (min_z_dis != null) {
            float zStart = poseStack.last().pose().getTranslation(t1).z;
            min_z_dis.translateAndRotate(poseStack);
            float dis = poseStack.last().pose().getTranslation(t2).z - zStart;
            return Math.max(poseStack.last().pose().getTranslation(t2).z, dis);
        }
        return Float.NaN;
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
}
