package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.items.guns.IGun;

@OnlyIn(Dist.CLIENT)
public class GunRenderContext {
    public MultiBufferSource bufferSource;
    public PoseStack poseStack;
    public ItemStack itemStack;
    public IGun gun;
    public ItemDisplayContext transformType;
    public boolean isFirstPerson;
    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;
    public int packedLight;
    public int packedOverlay;

    public GunRenderContext(MultiBufferSource bufferSource, PoseStack poseStack, ItemStack itemStack, IGun gun, ItemDisplayContext transformType,int packedLight, int packedOverlay) {
        this.bufferSource = bufferSource;
        this.poseStack = poseStack;
        this.itemStack = itemStack;
        this.transformType = transformType;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
        this.gun = gun;
        this.isFirstPerson = transformType.firstPerson();
    }

    public void render(ModelPart part, VertexConsumer vertexConsumer) {
        part.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
    }

    public void renderIf(ModelPart part, VertexConsumer vertexConsumer, boolean condition)  {
        if (condition) {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
        }
    }

    public void renderArmLong(ModelPart pose, boolean mainHand) {
        if (isFirstPerson) {
            PlayerArmRenderer.INSTANCE.renderLong(pose, packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }

    public void renderArm(ModelPart pose, boolean mainHand) {
        if (isFirstPerson) {
            PlayerArmRenderer.INSTANCE.render(pose, packedLight, packedOverlay, mainHand, bufferSource, poseStack);
        }
    }
}
