package sheridan.gcaa.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.lib.ArsenalLib;

@OnlyIn(Dist.CLIENT)
public class BulletShellModel {
    private static final BulletShellModel INSTANCE = new BulletShellModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/bullet_shell.png");
    public static final String PISTOL = "pistol", RIFLE = "rifle", SHOTGUN = "shotgun";
    public ModelPart root;

    private BulletShellModel() {
        root = ArsenalLib.loadBedRockGunModel(new ResourceLocation(GCAA.MODID, "model_assets/bullet_shell.json")).bakeRoot().getChild("root");
    }

    public static void render(String type, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        ModelPart model = INSTANCE.root.getChild(type);
        model.render(poseStack, bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)), light, overlay);
    }
}
