package sheridan.gcaa.industrial;

import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.Ammunition;

public class AmmunitionRecipe extends Recipe{
    public Ammunition ammunition;

    public AmmunitionRecipe() {
    }

    public AmmunitionRecipe(Ammunition ammunition, int ms) {
        super(ammunition, ms);
        this.ammunition = ammunition;
    }

    @Override
    public ItemStack getResult() {
        ItemStack stack = new ItemStack(ammunition);
        ammunition.checkAndGet(stack);
        return stack;
    }
}
