package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onRenderHandFP(RenderHandEvent event) {
        ClientWeaponStatus status = Clients.mainHandStatus;
        status.equipProgress = event.getEquipProgress();
        status.equipProgressParticle = event.getEquipProgress() + event.getPartialTick() * 0.05f;
        if (Clients.mainHandStatus.holdingGun.get()) {
            if (Minecraft.getInstance().options.bobView().get()) {
                GlobalWeaponBobbing.INSTANCE.update(event.getPartialTick());
            }
        }
    }


}
