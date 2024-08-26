package sheridan.gcaa.client;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class SingleLoadTask extends ReloadingTask{
    public SingleLoadTask(ItemStack itemStack, IGun gun) {
        super(itemStack, gun);
    }
}
