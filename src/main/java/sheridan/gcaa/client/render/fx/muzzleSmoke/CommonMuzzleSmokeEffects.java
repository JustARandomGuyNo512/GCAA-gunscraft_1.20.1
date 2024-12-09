package sheridan.gcaa.client.render.fx.muzzleSmoke;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2f;
import sheridan.gcaa.GCAA;

@OnlyIn(Dist.CLIENT)
public class CommonMuzzleSmokeEffects {
    public static final MuzzleSmoke COMMON = new MuzzleSmoke(100, 3, 1.75f, new Vector2f(1f, 0.3f),
            new ResourceLocation(GCAA.MODID, "textures/fx/muzzle_smoke/common.png"), 2).randomRotate();
}
