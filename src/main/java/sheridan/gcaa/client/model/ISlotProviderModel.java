package sheridan.gcaa.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import sheridan.gcaa.items.gun.IGun;

public interface ISlotProviderModel {
    void handleSlotTranslate(PoseStack poseStack, String modelSlotName, IGun gun);
    boolean hasSlot(String modelSlotName);
}
