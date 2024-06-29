package sheridan.gcaa.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.Commons;
import sheridan.gcaa.items.AutoRegister;
import sheridan.gcaa.items.NoRepair;
import sheridan.gcaa.items.guns.IGun;

@Mod.EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        Commons.SERVER_START_TIME = System.currentTimeMillis();
        ForgeRegistries.ITEMS.getEntries().forEach(entry -> {
            if (entry.getValue() instanceof AutoRegister autoRegister) {
                autoRegister.serverRegister(entry);
            }
        });
    }

    @SubscribeEvent
    public static void checkAndUpdateGun(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = event.getTo();
            if (stack.getItem() instanceof IGun gun) {
                //gun.getGun().onCraftedBy(stack, null, null);
                //AttachmentsHandler.INSTANCE.checkAndUpdate(stack);

            }
        }
    }

    @SubscribeEvent
    public static void anvilChangeEvent(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() instanceof NoRepair || event.getRight().getItem() instanceof NoRepair) {
            event.setCanceled(true);
        }
    }

}
