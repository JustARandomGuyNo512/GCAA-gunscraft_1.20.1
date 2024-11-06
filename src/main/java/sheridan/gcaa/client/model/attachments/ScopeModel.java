package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Vector3f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.utils.RenderAndMathUtils;

public abstract class ScopeModel extends SightModel {

    public float modelFovModifyWhenAds() {
        return 8.5f;
    }

    public final boolean useModelFovModifyWhenAds() {
        return true;
    }

    public abstract float handleMinZTranslation(PoseStack poseStack);

    protected float defaultHandleMinZTranslation(PoseStack poseStack, ModelPart back_glass, ModelPart min_z_dis) {
        PoseStack near = RenderAndMathUtils.copyPoseStack(poseStack);
        back_glass.translateAndRotate(near);
        float zStart = near.last().pose().getTranslation(new Vector3f(0 , 0, 0)).z;
        min_z_dis.translateAndRotate(poseStack);
        return poseStack.last().pose().getTranslation(new Vector3f(0 , 0, 0)).z - zStart;
    }

}
