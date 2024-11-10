package sheridan.gcaa.items.ammunition;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4i;

public interface IAmmunitionMod {
    int cost();
    /*
    *The id returned by this method must be a ResourceLocation object with unique content; if the id is duplicate, this mod overrides the previous one
    * */
    ResourceLocation getId();

    ResourceLocation getIconTexture();
    Vector4i getIconUV();
}
