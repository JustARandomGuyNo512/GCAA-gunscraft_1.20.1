package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.items.guns.IGun;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onRenderHandFP(RenderHandEvent event) {
        ClientWeaponStatus status = Clients.mainHandStatus;
        status.equipProgress = event.getEquipProgress();
        if (Clients.mainHandStatus.holdingGun.get()) {
            if (Minecraft.getInstance().options.bobView().get()) {
                GlobalWeaponBobbing.INSTANCE.update(event.getPartialTick(), status.equipProgress);
            }
        }
    }

}
