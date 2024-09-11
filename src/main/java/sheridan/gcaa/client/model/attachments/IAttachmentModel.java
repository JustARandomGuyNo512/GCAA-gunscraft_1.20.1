package sheridan.gcaa.client.model.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public interface IAttachmentModel {
    IAttachmentModel EMPTY = new IAttachmentModel() {
        @Override
        public void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose) {}

        @Override
        public ModelPart getRoot() {
            return ModelPart.EMPTY;
        }
    };
    void render(GunRenderContext context, AttachmentRenderEntry attachmentRenderEntry, ModelPart pose);
    ModelPart getRoot();
}
