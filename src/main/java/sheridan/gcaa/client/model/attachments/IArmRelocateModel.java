package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IArmRelocateModel {
    void handleRelocateTranslation(PoseStack poseStack, boolean mainHand);
}
