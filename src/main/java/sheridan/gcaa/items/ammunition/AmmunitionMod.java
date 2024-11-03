package sheridan.gcaa.items.ammunition;

import net.minecraft.resources.ResourceLocation;

public class AmmunitionMod implements IAmmunitionMod{
    protected final ResourceLocation id;

    public AmmunitionMod(ResourceLocation id) {
        this.id = id;
        AmmunitionModRegister.registerAmmunitionMod(this);
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
