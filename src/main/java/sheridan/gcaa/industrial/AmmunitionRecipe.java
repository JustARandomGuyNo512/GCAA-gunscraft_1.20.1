package sheridan.gcaa.industrial;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.data.IDataPacketGen;
import sheridan.gcaa.items.ammunition.Ammunition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmmunitionRecipe extends Recipe{
    public final Ammunition ammunition;

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

    @Override
    public AmmunitionRecipe addIngredients(Set<Item> listItem, List<Integer> listAmount) {
        return this;
    }
}
