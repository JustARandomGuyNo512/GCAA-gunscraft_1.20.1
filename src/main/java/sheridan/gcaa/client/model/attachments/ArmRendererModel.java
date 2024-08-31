package sheridan.gcaa.client.model.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public abstract class ArmRendererModel {
    protected static final String LEFT_ARM_RENDER_REPLACE = GunRenderContext.LEFT_ARM_RENDER_REPLACE;
    protected static final String RIGHT_ARM_RENDER_REPLACE = GunRenderContext.RIGHT_ARM_RENDER_REPLACE;
}
