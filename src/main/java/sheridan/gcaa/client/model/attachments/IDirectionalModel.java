package sheridan.gcaa.client.model.attachments;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.attachmentSys.AttachmentSlot;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public interface IDirectionalModel extends IAttachmentModel{
    byte UPPER = AttachmentSlot.UPPER;
    byte LOWER = AttachmentSlot.LOWER;
    byte NO_DIRECTION = AttachmentSlot.NO_DIRECTION;
    float R_180 = (float) Math.toRadians(180);

    byte getDirection();

    default void initTranslation(AttachmentRenderEntry attachmentRenderEntry, GunRenderContext context, ModelPart pose) {
        ModelPart root = getRoot(context.gun);
        root.copyFrom(pose);
        byte slotDirection = attachmentRenderEntry.direction;
        if (slotDirection != NO_DIRECTION) {
            byte thisDirection = getDirection();
            if (thisDirection == NO_DIRECTION) {
                throw new IllegalStateException("Directional Attachment model must have a direction: UPPER(" + UPPER + ") or LOWER(" + LOWER + ")");
            }
            if (thisDirection != slotDirection) {
                root.zRot += R_180;
            }
        }
        root.translateAndRotate(context.poseStack);
        root.resetPose();
    }
}
