package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.screens.ClientSettingsScreen;
import sheridan.gcaa.client.screens.GunDebugAdjustScreen;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.OpenAttachmentScreenPacket;
import sheridan.gcaa.network.packets.c2s.SwitchFireModePacket;


@Mod.EventBusSubscriber(Dist.CLIENT)
public class ControllerEvents {

    static ScopeMagnificationTask scopeMagnificationTask = null;
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (shouldHandleInputEvent() && Clients.isInAds() && Clients.mainHandStatus.getEffectiveSight() instanceof Scope scope) {
            float scopeMagnification = Clients.mainHandStatus.getScopeMagnification();
            if (!Float.isNaN(scopeMagnification)) {
                boolean zoomIn = event.getScrollDelta() == -1;
                if (Clients.mainHandStatus.setScopeMagnification(scopeMagnification + (
                        zoomIn ? -0.1f : 0.1f
                ))) {
                    float magnification = Clients.mainHandStatus.getScopeMagnification();
                    scopeMagnificationTask = new ScopeMagnificationTask(
                            magnification,
                            System.currentTimeMillis(),
                            scope
                    );
                    RenderEvents.renderScopeMagnificationTip(scope, magnification, 0xffffff);
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (scopeMagnificationTask != null && scopeMagnificationTask.sendTask()) {
                scopeMagnificationTask = null;
            }
        }
    }

    private static class ScopeMagnificationTask {
        public float scopeMagnification;
        public long startTime;
        public Scope scope;

        public ScopeMagnificationTask(float scopeMagnification, long startTime, Scope scope) {
            this.scopeMagnification = scopeMagnification;
            this.startTime = startTime;
            this.scope = scope;
        }

        public boolean sendTask() {
            if (System.currentTimeMillis() - startTime < 300L) {
                return false;
            }
            RenderEvents.renderScopeMagnificationTip(scope, scopeMagnification, 0x00ff00);
            Clients.mainHandStatus.attachmentsStatus.sendSetScopeMagnificationPacket();
            return true;
        }
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (shouldHandleInputEvent()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                if (event.getButton() == 0) {
                    if (event.getAction() == 1) {
                        Clients.mainHandStatus.buttonDown.set(Clients.allowFireBtnDown(stack, gun, player));
                    } else if (event.getAction() == 0) {
                        Clients.mainHandStatus.buttonDown.set(false);
                    }
                    event.setCanceled(true);
                } else if (event.getButton() == 1) {
                    if (shouldHandleRightClick()) {
                        Clients.mainHandStatus.ads = (event.getAction() == 1 && Clients.allowAdsStart(stack, gun, player));
                        boolean cancel = ReloadingHandler.isReloading() || !gun.canUseWithShield() || !(player.getOffhandItem().getItem() instanceof ShieldItem);
                        event.setCanceled(cancel);
                    }
                }
            }
        } else {
            Clients.mainHandStatus.buttonDown.set(false);
            Clients.mainHandStatus.ads = false;
        }
    }

    private static boolean shouldHandleInputEvent() {
        return Minecraft.getInstance().isWindowActive() && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().screen == null;
    }

    private static boolean shouldHandleRightClick() {
        HitResult result = Minecraft.getInstance().hitResult;
        if (result != null && result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityRayTraceResult = (EntityHitResult) result;
            return !(entityRayTraceResult.getEntity() instanceof ItemFrame);
        }
        return true;
    }


    @SubscribeEvent
    public static void onButtonPress(InputEvent.Key event) {
        if(Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            ItemStack stackMain = player.getMainHandItem();
            if (KeyBinds.OPEN_DEBUG_SCREEN.isDown() && event.getAction() == 1) {
                if (stackMain.getItem() instanceof IGun) {
                    Minecraft.getInstance().setScreen(new GunDebugAdjustScreen());
                }
            } else if (KeyBinds.SWITCH_FIRE_MODE.isDown() && event.getAction() == 1) {
                if (stackMain.getItem() instanceof IGun gun) {
                    if (gun.getGunProperties().fireModes.size() > 1) {
                        Clients.mainHandStatus.buttonDown.set(false);
                        player.playSound(SoundEvents.LEVER_CLICK, 0.5f, 1.5f);
                        PacketHandler.simpleChannel.sendToServer(new SwitchFireModePacket());
                    }
                }
            } else if (KeyBinds.RELOAD.isDown() && event.getAction() == 1) {
                handleReload(stackMain, player);
            } else if (KeyBinds.OPEN_ATTACHMENTS_SCREEN.isDown() && event.getAction() == 1) {
                PacketHandler.simpleChannel.sendToServer(new OpenAttachmentScreenPacket());
            } else if (KeyBinds.OPEN_CLIENT_SETTINGS_SCREEN.isDown() && event.getAction() == 1) {
                Minecraft.getInstance().setScreen(new ClientSettingsScreen());
            } else if (KeyBinds.SWITCH_EFFECTIVE_SIGHT.isDown() && event.getAction() == 1) {
                Clients.mainHandStatus.attachmentsStatus.onSwitchEffectiveSight();
            }
            Clients.displayGunInfoDetails =
                    event.getKey() == KeyBinds.SHOW_FULL_GUN_INFO.getKey().getValue() && event.getAction() == 2;
            Clients.debugKeyDown =
                    event.getKey() == KeyBinds.DEBUG_KEY.getKey().getValue() && event.getAction() == 2;

        }
    }

    private static void handleReload(ItemStack stack, Player player) {
        if (stack.getItem() instanceof IGun gun) {
            if (gun.clientReload(stack, player)) {
                Clients.mainHandStatus.buttonDown.set(false);
                ReloadingHandler.INSTANCE.setTask(gun.getReloadingTask(stack));
            }
        }
    }
}
