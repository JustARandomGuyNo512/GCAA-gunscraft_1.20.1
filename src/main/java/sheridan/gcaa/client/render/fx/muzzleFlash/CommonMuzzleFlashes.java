package sheridan.gcaa.client.render.fx.muzzleFlash;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CommonMuzzleFlashes {
    public static final MuzzleFlash COMMON = new MuzzleFlash("COMMON",
            List.of(new MuzzleFlashTexture(new ResourceLocation(GCAA.MODID, "textures/fx/muzzle_flash/common.png"), 4)), true, 4);

    public static final MuzzleFlash SUPPRESSOR_COMMON = new MuzzleFlash("SUPPRESSOR_COMMON",
            List.of(new MuzzleFlashTexture(new ResourceLocation(GCAA.MODID, "textures/fx/muzzle_flash/suppressor.png"), 3)), true, 4);

    public static final MuzzleFlash AK_COMPENSATOR = new MuzzleFlash("AK_COMPENSATOR",
            List.of(new MuzzleFlashTexture(new ResourceLocation(GCAA.MODID, "textures/fx/muzzle_flash/ak_compensator.png"), 4)), true, 2);

    public static final MuzzleFlash AR_COMPENSATOR = new MuzzleFlash("AR_COMPENSATOR",
            List.of(new MuzzleFlashTexture(new ResourceLocation(GCAA.MODID, "textures/fx/muzzle_flash/ar_compensator.png"), 4)), true, 4);
}
