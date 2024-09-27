package sheridan.gcaa.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.entities.GrenadeModel;
import sheridan.gcaa.entities.projectiles.Grenade;

@OnlyIn(Dist.CLIENT)
public class GrenadeRenderer extends EntityRenderer<Grenade> {
    private static final float BASE_SCALE = 0.5f;
    private static final GrenadeModel<?> MODEL = new GrenadeModel<>(new ResourceLocation(GCAA.MODID, "model_assets/entities/grenade/grenade.geo.json"));
    private static final ResourceLocation TEXTURE = GrenadeModel.TEXTURE;

    public GrenadeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(Grenade pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.tickCount < 2) {
            return;
        }
        pPoseStack.pushPose();
        pPoseStack.scale(BASE_SCALE, BASE_SCALE, BASE_SCALE);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(pPartialTick, pEntity.yRotO + 180,
                        pEntity.getYRot() + 180
                )
        ));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(
                Mth.lerp(pPartialTick, pEntity.xRotO,
                        pEntity.getXRot()
                )
        ));
        MODEL.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(pEntity))), pPackedLight, 655360, 1, 1, 1, 1);
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Grenade pEntity) {
        return TEXTURE;
    }
}
