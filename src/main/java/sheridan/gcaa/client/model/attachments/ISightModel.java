package sheridan.gcaa.client.model.attachments;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;

@OnlyIn(Dist.CLIENT)
public interface ISightModel {
    void handleCrosshairTranslation(PoseStack poseStack);
    ModelPart getCrosshair();
}
