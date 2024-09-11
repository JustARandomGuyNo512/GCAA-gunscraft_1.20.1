package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.AttachmentRenderEntry;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public interface IAttachmentModel {
    enum Direction {
        UPPER("_upper_"),
        LOWER("_lower_"),
        NO_DIRECTION("_no_direction_");
        private final String name;
        Direction(String name)  {
            this.name = name;
        }
    }
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

    default Direction getDirection() {
        return Direction.NO_DIRECTION;
    }

    default void initTranslation(ModelPart pose, PoseStack poseStack) {
        if (this == EMPTY) {
            return;
        }
        ModelPart root = getRoot();
        if (root != null) {
            root.copyFrom(pose);
            root.translateAndRotate(poseStack);
            if (getDirection() != Direction.NO_DIRECTION) {
                ModelPart normal = pose.getChildNoThrow(getDirection().name);
                if (normal != null) {
                    normal.translateAndRotate(poseStack);
                }
            }
            root.resetPose();
        }
    }
}
