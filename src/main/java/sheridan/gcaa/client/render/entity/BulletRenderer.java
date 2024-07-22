package sheridan.gcaa.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.entities.projectiles.Projectile;

@OnlyIn(Dist.CLIENT)
public class BulletRenderer extends EntityRenderer<Projectile> {
    public static final float BASE_SCALE = 0.1f;

    public BulletRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        //this.shadowRadius = 0f;
    }

    @Override
    public void render(Projectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        //Minecraft.getInstance().getDeltaFrameTime();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Projectile pEntity) {
        return null;
    }
}
