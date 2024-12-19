package sheridan.gcaa.client.model.gun.guns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.RenderTypes;

public class HkG28Model extends GunModel {
    private final ResourceLocation TEXTURE = new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.png");
    private ModelPart barrel, IS_front, handguard, muzzle, stock, charge, body, safety, bolt, IS, grip, mag, bullet;


    public HkG28Model() {
        super(new ResourceLocation(GCAA.MODID, "model_assets/guns/hk_g28/hk_g28.geo.json"), new ResourceLocation(""));
    }

    @Override
    protected void postInit(ModelPart gun, ModelPart root) {
        barrel = gun.getChild("barrel").meshing();
        handguard = gun.getChild("handguard").meshing();
        IS_front = handguard.getChild("IS_front").meshing();
        muzzle = gun.getChild("muzzle").meshing();
        stock = gun.getChild("stock").meshing();
        charge = gun.getChild("charge").meshing();
        body = gun.getChild("body").meshing();
        safety = gun.getChild("safety").meshing();
        bolt = gun.getChild("bolt").meshing();
        IS = gun.getChild("IS").meshing();
        grip = gun.getChild("grip").meshing();
        mag = gun.getChild("mag").meshing();
        bullet = mag.getChild("bullet").meshing();
    }

    @Override
    protected void renderGunModel(GunRenderContext context) {
        VertexConsumer vertexConsumer = context.getBuffer(RenderType.entityCutout(TEXTURE));
        bullet.visible = context.shouldBulletRender();
        context.renderIf(IS, vertexConsumer, context.notContainsScope());
        context.renderIf(muzzle, vertexConsumer, context.notHasMuzzle());
        context.renderIf(mag, vertexConsumer, context.notHasMag());
        context.renderIf(stock, vertexConsumer, context.notHasStock());
        context.render(vertexConsumer, barrel, charge, body, safety, bolt, grip);
        vertexConsumer = context.getBuffer(RenderTypes.getCutOutNoCullMipmap(TEXTURE));
        context.render(vertexConsumer, handguard);
        if (context.isFirstPerson) {
            context.renderArm(right_arm, true);
            context.renderArm(left_arm, false);
        }
    }

    @Override
    protected void renderAttachmentsModel(GunRenderContext context) {
        context.renderMagAttachmentIf(mag, !context.notHasMag());
        context.renderAllAttachmentsLeft(gun);
    }

    @Override
    protected void renderPostEffect(GunRenderContext context) {
        context.renderBulletShell();
        context.renderMuzzleFlash(1.0f);
    }

    @Override
    protected void animationGlobal(GunRenderContext context) {

    }

    @Override
    protected void afterRender(GunRenderContext context) {

    }
}
