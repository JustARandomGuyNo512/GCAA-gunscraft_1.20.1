package sheridan.gcaa.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.entities.projectiles.Bullet;

@OnlyIn(Dist.CLIENT)
public class BulletRenderer extends EntityRenderer<Bullet> {
    public static final float BASE_SCALE = 0.1f;

    public BulletRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0f;
    }

    @Override
    public void render(Bullet pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft.getInstance().getDeltaFrameTime();
    }

    @Override
    public ResourceLocation getTextureLocation(Bullet pEntity) {
        return null;
    }
}
