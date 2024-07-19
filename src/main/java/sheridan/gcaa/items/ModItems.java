package sheridan.gcaa.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.guns.Akm;
import sheridan.gcaa.items.gun.guns.G19;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GCAA.MODID);

    public static final RegistryObject<G19> G19 = ITEMS.register("g19", G19::new);
    public static final RegistryObject<Akm> AKM = ITEMS.register("akm", Akm::new);
}
