package sheridan.gcaa;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.items.ModItems;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> MOD_TABS;
    public static final RegistryObject<CreativeModeTab> GUNS_TAB;
    static {
        MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GCAA.MODID);

        GUNS_TAB = MOD_TABS.register("guns", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.guns"))
                .icon(() ->new ItemStack(ModItems.G19.get()))
                .displayItems((parameters, tab) -> {
                    tab.accept(ModItems.G19.get());
                    tab.accept(ModItems.AKM.get());
                }).build());
    }

}
