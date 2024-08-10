package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;

public interface ISubSlotProviderModel {
    void handleSubSlotTranslation(PoseStack poseStack, String modelSlotName);
}
