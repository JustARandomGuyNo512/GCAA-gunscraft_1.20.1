package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import sheridan.gcaa.client.render.GunRenderContext;

public interface IGunModel {
    void render(GunRenderContext gunRenderContext);
    void handleRootTranslate(PoseStack poseStack);
}
