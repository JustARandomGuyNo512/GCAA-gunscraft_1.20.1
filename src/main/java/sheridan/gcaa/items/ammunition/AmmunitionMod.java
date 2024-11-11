package sheridan.gcaa.items.ammunition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;

import java.awt.*;

public class AmmunitionMod implements IAmmunitionMod{
    public static final ResourceLocation ICONS_0 = new ResourceLocation(GCAA.MODID, "textures/gui/ammunition_mod_icon/icons_0.png");
    public final ResourceLocation id;
    public final ResourceLocation icon;
    public final Vector4i iconUV;
    public final int cost;
    public final String descriptionId;
    public final int themeColor;

    public AmmunitionMod(ResourceLocation id, int cost, ResourceLocation icon, Vector4i iconUV, String descriptionId, int themeColor)  {
        this.id = id;
        this.cost = cost;
        this.icon = icon;
        this.iconUV = iconUV;
        this.descriptionId = descriptionId;
        this.themeColor = themeColor;
        AmmunitionModRegister.registerAmmunitionMod(this);
    }

    @Override
    public int defaultCost() {
        return cost;
    }

    @Override
    public int getCostFor(IAmmunition ammunition) {
        return defaultCost();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ResourceLocation getIconTexture() {
        return icon;
    }

    @Override
    public Vector4i getIconUV() {
        return iconUV;
    }

    @Override
    public int getThemeColor() {
        return themeColor;
    }

    @Override
    public String getDescriptionId() {
        return descriptionId;
    }

    @Override
    public Component getSpecialDescription() {
        return null;
    }

    @Override
    public void onModifyAmmunition(IAmmunition ammunition, CompoundTag dataRateTag) {

    }

}
