package sheridan.gcaa.client;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class SingleReloadTask extends ReloadingTask{
    public SingleReloadTask(ItemStack itemStack, IGun gun) {
        super(itemStack, gun);
    }
}
