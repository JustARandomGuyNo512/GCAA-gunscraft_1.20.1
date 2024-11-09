package sheridan.gcaa.items.ammunition;

import net.minecraft.resources.ResourceLocation;

public class AmmunitionMod implements IAmmunitionMod{
    public final ResourceLocation id;
    public final int cost;

    public AmmunitionMod(ResourceLocation id, int cost) {
        this.id = id;
        this.cost = cost;
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
}
