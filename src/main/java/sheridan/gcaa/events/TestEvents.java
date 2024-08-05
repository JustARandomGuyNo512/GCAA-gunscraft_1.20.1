package sheridan.gcaa.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.items.gun.IGun;

@Mod.EventBusSubscriber
public class TestEvents {

    /*static boolean b = true;
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (b && event.phase == TickEvent.Phase.END && !player.level().isClientSide) {
            if (player.getMainHandItem().getItem() instanceof IGun gun) {
                b = false;
                ListTag listTag = gun.getAttachmentsListTag(player.getMainHandItem());
                CompoundTag tag = new CompoundTag();
                tag.putString("id", "unknown");
                tag.putString("model_slot_name", "s_muzzle");
                tag.putString("parent_slot", "__ROOT__");
                tag.putString("slot_name", "muzzle");
                listTag.add(tag);
                System.out.println(tag);
                gun.setAttachmentsListTag(player.getMainHandItem(), listTag);
            }
        }
    }*/
}
