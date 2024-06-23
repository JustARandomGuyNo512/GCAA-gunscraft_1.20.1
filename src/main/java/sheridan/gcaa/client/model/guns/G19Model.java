package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class G19Model extends GenericGunModel{
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.png");
    private final ModelPart root;
    private final ModelPart barrel;
    private final ModelPart muzzle_point;
    private final ModelPart grid;
    private final ModelPart slide;
    private final ModelPart mag;
    private final ModelPart mag_point;
    private final ModelPart right_arm;
    private final ModelPart left_arm;

    private final ModelPart gun_arm;
    private final ModelPart gun;

    public G19Model() {
        this.root = ArsenalLib.loadBedRockGunModel(
                new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"))
                .bakeRoot().getChild("root");
        left_arm = root.getChild("left_arm");
        gun_arm = root.getChild("gun_arm");
        gun = gun_arm.getChild("gun");
        right_arm = gun_arm.getChild("right_arm");
        barrel = gun.getChild("barrel").meshing();
        muzzle_point = barrel.getChild("muzzle_point");
        grid = gun.getChild("grid").meshing();
        slide = gun.getChild("slide").meshing();
        mag = gun.getChild("mag").meshing();
        mag_point = mag.getChild("mag_point");
    }

    @Override
    public ModelPart getRoot() {
        return root;
    }

    @Override
    public ModelPart getGunArmLayer() {
        return gun_arm;
    }

    @Override
    public ModelPart getGunLayer() {
        return gun;
    }

    @Override
    public ModelPart getLeftArm() {
        return left_arm;
    }

    @Override
    public ModelPart getRightArm() {
        return right_arm;
    }

    @Override
    public void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        context.render(barrel, vertexConsumer);
        context.render(slide, vertexConsumer);
        context.render(grid, vertexConsumer);
        context.render(mag, vertexConsumer);
    }

    @Override
    public void renderAttachmentsModel(GunRenderContext context) {

    }

    @Override
    protected boolean longArm() {
        return false;
    }

    @Override
    public void handleGunTranslate(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        gun_arm.translateAndRotate(poseStack);
        gun.translateAndRotate(poseStack);
    }
}
