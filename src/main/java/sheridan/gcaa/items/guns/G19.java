package sheridan.gcaa.items.guns;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sheridan.gcaa.items.GunProperties;
import sheridan.gcaa.items.guns.fireModes.Semi;

import java.util.List;

public class G19 extends Gun {
    public G19() {
        super(new GunProperties(List.of(Semi.SEMI), null));

    }

    @Override
    public @Nullable String getCreatorModId(ItemStack itemStack) {
        return super.getCreatorModId(itemStack);
    }
}
