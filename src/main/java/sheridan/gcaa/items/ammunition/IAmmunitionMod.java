package sheridan.gcaa.items.ammunition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector4i;

public interface IAmmunitionMod {
    int defaultCost();
    int getCostFor(IAmmunition ammunition);
    /*
    *The id returned by this method must be a ResourceLocation object with unique content; if the id is duplicate, this mod overrides the previous one
    * */
    ResourceLocation getId();

    ResourceLocation getIconTexture();
    Vector4i getIconUV();

    int getThemeColor();

    String getDescriptionId();

    Component getSpecialDescription();

    void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag);
}
