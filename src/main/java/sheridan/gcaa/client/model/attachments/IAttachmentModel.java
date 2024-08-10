package sheridan.gcaa.client.model.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public interface IAttachmentModel {

    void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose);

    ModelPart getRoot();
}
