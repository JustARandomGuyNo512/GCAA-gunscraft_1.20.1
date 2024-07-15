package sheridan.gcaa.client.model.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.lib.ArsenalLib;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class G19Model extends GCAAStyleGunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.png");
    private ModelPart grid, slide, mag, barrel;
    private ModelPart slot_grip, slot_scope, slot_mag, slot_muzzle;

    private final AnimationDefinition recoil;
    private final AnimationDefinition shoot;

    public G19Model() {
        super(ArsenalLib.loadBedRockGunModel(
                        new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"))
                .bakeRoot().getChild("root"));
        Map<String, AnimationDefinition> animations = ArsenalLib.loadBedRockAnimation(new ResourceLocation(GCAA.MODID, "model_assets/guns/g19/g19.animation.json"));
        recoil = animations.get("recoil");
        shoot = animations.get("shoot");
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        grid = gun.getChild("grid").meshing();
        slide = gun.getChild("slide").meshing();
        mag = gun.getChild("mag").meshing();

        slot_grip = gun.getChild("s_grip");
        slot_mag = gun.getChild("s_mag");
        slot_muzzle = gun.getChild("s_muzzle");
        slot_scope = gun.getChild("s_scope");
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        context.render(vertexConsumer, barrel, slide, grid, mag);
        context.renderArm(left_arm, false);
        context.renderArm(right_arm, true);
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {

    }

    @Override
    protected void animationGlobal(GunRenderContext context) {
        if (context.isFirstPerson || context.isThirdPerson()) {
            if (context.isFirstPerson) {
                AnimationHandler.INSTANCE.applyRecoil(this);
            }
            KeyframeAnimations.animate(this, shoot, Clients.lastShootMain(), 1);
        }
    }

    @Override
    protected void afterRender(GunRenderContext gunRenderContext) {
        root.resetPose();
        slide.resetPose();
        barrel.resetPose();
    }

    @Override
    public AnimationDefinition getRecoilAnimation() {
        return recoil;
    }

    @Override
    public AnimationDefinition getReload() {
        return null;
    }

    @Override
    public AnimationDefinition getFullReload() {
        return null;
    }
}
