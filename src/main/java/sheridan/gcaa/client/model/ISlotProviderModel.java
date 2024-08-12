package sheridan.gcaa.client.model;

import com.mojang.blaze3d.vertex.PoseStack;

public interface ISlotProviderModel {
    void handleSlotTranslate(PoseStack poseStack, String modelSlotName);
    boolean hasSlot(String modelSlotName);
}
