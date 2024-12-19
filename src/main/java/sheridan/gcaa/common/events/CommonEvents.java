package sheridan.gcaa.common.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.Commons;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.common.config.CommonConfig;
import sheridan.gcaa.common.damageTypes.ProjectileDamage;
import sheridan.gcaa.items.AutoRegister;
import sheridan.gcaa.items.NoRepairNoEnchantmentItem;
import sheridan.gcaa.items.UnknownAttachment;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.IGun;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class CommonEvents {
    private static final KnockBackHandler KNOCK_BACK_HANDLER = new KnockBackHandler();

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
                gun.getGun().onEquipped(stack, player);
                if (gun.shouldUpdate(stack)) {
                    gun.beforeGunDataUpdate(player, stack);
                    AttachmentsHandler.INSTANCE.checkAndUpdate(stack, gun, player);
                    gun.afterGunDataUpdate(player, stack);
                }
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
        if (event.getLeft().getItem() instanceof NoRepairNoEnchantmentItem || event.getRight().getItem() instanceof NoRepairNoEnchantmentItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source instanceof ProjectileDamage) {
            KNOCK_BACK_HANDLER.mark(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onKnockBack(LivingKnockBackEvent event) {
        if (!KNOCK_BACK_HANDLER.checkShouldKnockBackAndRemove(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static class KnockBackHandler {
        private final Map<LivingEntity, Object> knockBackMap = new WeakHashMap<>();

        public void mark(LivingEntity entity) {
            if (entity instanceof Player) {
                if (!CommonConfig.enableKnockBackToPlayer.get()) {
                    knockBackMap.put(entity, null);
                }
            } else {
                if (CommonConfig.enableKnockBackToEntity.get()) {
                    knockBackMap.put(entity, null);
                }
            }
        }

        public boolean checkShouldKnockBackAndRemove(LivingEntity entity) {
            if (knockBackMap.containsKey(entity)) {
                knockBackMap.remove(entity);
                return false;
            }
            return true;
        }
    }
}
