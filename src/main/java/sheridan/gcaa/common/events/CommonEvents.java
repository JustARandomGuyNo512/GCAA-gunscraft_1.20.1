package sheridan.gcaa.common.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.Commons;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.items.AutoRegister;
import sheridan.gcaa.items.NoRepair;
import sheridan.gcaa.items.UnknownAttachment;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

import java.util.UUID;

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
    public static void checkAndUpdateAmmunition(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack stack = event.getTo();
            if (stack.getItem() instanceof IAmmunition ammunition) {
                ammunition.get().checkAndGet(stack);
            }
        }
    }

        @SubscribeEvent
    public static void checkAndUpdateGun(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = event.getTo();
            if (stack.getItem() instanceof IGun gun) {
                if (gun.shouldUpdate(stack)) {
                    gun.beforeGunDataUpdate(stack);
                    AttachmentsHandler.INSTANCE.checkAndUpdate(stack, gun, player);
                    gun.afterGunDataUpdate(stack);
                }
                gun.getGun().onEquipped(stack, player);
            }
            if (stack.getItem() instanceof UnknownAttachment) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("id")) {
                    String id = tag.getString("id");
                    IAttachment attachment = AttachmentsRegister.get(id);
                    if (attachment != null) {
                        ItemStack attachmentStack = new ItemStack(attachment.get(), 1);
                        player.setItemInHand(InteractionHand.MAIN_HAND, attachmentStack);
                    }
                }
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
