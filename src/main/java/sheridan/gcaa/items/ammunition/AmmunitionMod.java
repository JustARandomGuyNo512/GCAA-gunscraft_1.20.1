package sheridan.gcaa.items.ammunition;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;

public class AmmunitionMod implements IAmmunitionMod{
    public static final ResourceLocation ICONS_0 = new ResourceLocation(GCAA.MODID, "textures/gui/ammunition_mod_icon/icons_0.png");
    public final ResourceLocation id;
    public final ResourceLocation icon;
    public final Vector4i iconUV;
    public final int cost;

    public AmmunitionMod(ResourceLocation id, int cost, ResourceLocation icon, Vector4i iconUV)  {
        this.id = id;
        this.cost = cost;
        this.icon = icon;
        this.iconUV = iconUV;
        AmmunitionModRegister.registerAmmunitionMod(this);
    }

    @Override
    public int cost() {
        return cost;
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

}
