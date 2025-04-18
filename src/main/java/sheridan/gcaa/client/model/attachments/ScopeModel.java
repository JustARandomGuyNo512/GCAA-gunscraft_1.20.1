package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Vector3f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.utils.RenderAndMathUtils;

public abstract class ScopeModel extends SightModel {
    protected ModelPart back_glass;
    protected ModelPart min_z_dis;
    protected final ModelPart root;

    public ScopeModel(ModelPart root) {
        this.root = root;
        root.meshingAll();
    }

    public float modelFovModifyWhenAds() {
        return 12f;
    }

    public final boolean useModelFovModifyWhenAds() {
        return true;
    }

    @Override
    public float handleMinZTranslation(PoseStack poseStack) {
        return defaultHandleMinZTranslation(poseStack, back_glass, min_z_dis);
    }

    @Override
    public ModelPart getRoot(IGun gun) {
        return root;
    }
}
