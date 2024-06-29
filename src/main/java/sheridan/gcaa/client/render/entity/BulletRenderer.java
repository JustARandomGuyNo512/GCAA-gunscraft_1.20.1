package sheridan.gcaa.client.render.entity;

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
    public ResourceLocation getTextureLocation(Bullet pEntity) {
        return null;
    }
}
