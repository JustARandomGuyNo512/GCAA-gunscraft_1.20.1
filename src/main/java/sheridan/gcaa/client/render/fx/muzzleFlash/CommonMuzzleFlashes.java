package sheridan.gcaa.client.render.fx.muzzleFlash;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CommonMuzzleFlashes {
    public static final MuzzleFlash COMMON = new MuzzleFlash(
            List.of(new MuzzleFlashTexture(new ResourceLocation(GCAA.MODID, "textures/fx/muzzle_flash/common.png"), 4)), true, 4);
}
