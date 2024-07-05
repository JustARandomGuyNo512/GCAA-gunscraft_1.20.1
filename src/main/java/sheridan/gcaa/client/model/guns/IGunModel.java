package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public interface IGunModel {
    void render(GunRenderContext gunRenderContext);
    void handleGunTranslate(PoseStack poseStack);
    AnimationDefinition getRecoilAnimation();
}
