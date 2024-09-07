package sheridan.gcaa.client.model.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public interface IMuzzleFlashRendererModel {
    void renderMuzzleFlash(GunRenderContext context);
}
