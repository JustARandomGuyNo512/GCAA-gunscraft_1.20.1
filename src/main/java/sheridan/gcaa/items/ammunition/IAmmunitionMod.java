package sheridan.gcaa.items.ammunition;

import net.minecraft.resources.ResourceLocation;

public interface IAmmunitionMod {
    int cost();
    /*
    *The id returned by this method must be a ResourceLocation object with unique content; if the id is duplicate, this mod overrides the previous one
    * */
    ResourceLocation getId();
}
