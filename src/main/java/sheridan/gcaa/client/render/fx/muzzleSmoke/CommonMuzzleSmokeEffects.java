package sheridan.gcaa.client.render.fx.muzzleSmoke;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2f;
import sheridan.gcaa.GCAA;

@OnlyIn(Dist.CLIENT)
public class CommonMuzzleSmokeEffects {
    public static final MuzzleSmoke COMMON = new MuzzleSmoke(150, 3.5f, 3f, new Vector2f(0.9f, 0.7f),
            new ResourceLocation(GCAA.MODID, "textures/fx/muzzle_smoke/common_0.png"), 4).randomRotate();
}
