package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.*;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.AdventurersArsenalLib;

import java.util.Arrays;
import java.util.HashSet;

public class G19Model extends HierarchicalModel<Entity> implements IGunModel{
    private final ModelPart root;
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "custom_models/guns/g19/g19.png");

    public G19Model() {
        this.root = AdventurersArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "custom_models/guns/g19/g19.geo.json")).bakeRoot().getChild("root");
        root.getChild("right_arm").visible = false;
        root.getChild("left_arm_right_side").visible = false;
        root.getChild("left_arm_left_side").visible = false;
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void render(GunRenderContext gunRenderContext) {
        VertexConsumer vertexConsumer = gunRenderContext.bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        gunRenderContext.render(root, vertexConsumer);
    }

    @Override
    public void handleRootTranslate(PoseStack poseStack) {

    }
}
