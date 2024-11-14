package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
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
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.screens.ClientSettingsScreen;
import sheridan.gcaa.client.screens.GunDebugAdjustScreen;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.attachments.IInteractive;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.attachments.grips.Flashlight;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.OpenGunModifyScreenPacket;
import sheridan.gcaa.network.packets.c2s.SwitchFireModePacket;
import sheridan.gcaa.network.packets.c2s.TurnFlashlightPacket;


@Mod.EventBusSubscriber(Dist.CLIENT)
public class ControllerEvents {

    static ScopeMagnificationTask scopeMagnificationTask = null;
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (shouldHandleInputEvent() && Clients.isInAds() && Clients.MAIN_HAND_STATUS.getEffectiveSight() instanceof Scope scope) {
            float scopeMagnification = Clients.MAIN_HAND_STATUS.getScopeMagnificationRate();
            if (!Float.isNaN(scopeMagnification)) {
                boolean zoomIn = event.getScrollDelta() == -1;
                if (Clients.MAIN_HAND_STATUS.setScopeMagnificationRate(scopeMagnification + (
                        zoomIn ? -0.1f : 0.1f
                ))) {
                    float magnification = Clients.MAIN_HAND_STATUS.getScopeMagnificationRate();
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
            Clients.MAIN_HAND_STATUS.attachmentsStatus.sendSetScopeMagnificationPacket();
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
                        Clients.MAIN_HAND_STATUS.buttonDown.set(Clients.allowFireBtnDown(stack, gun, player));
                    } else if (event.getAction() == 0) {
                        Clients.MAIN_HAND_STATUS.buttonDown.set(false);
                    }
                    event.setCanceled(true);
                } else if (event.getButton() == 1) {
                    if (shouldHandleRightClick()) {
                        Clients.MAIN_HAND_STATUS.ads = (event.getAction() == 1 && Clients.allowAdsStart(stack, gun, player));
                        boolean cancel = ReloadingHandler.isReloading() || !gun.canUseWithShield() || !(player.getOffhandItem().getItem() instanceof ShieldItem);
                        event.setCanceled(cancel);
                    }
                }
                AttachmentsHandler.INSTANCE.getAttachments(stack, gun).forEach((attachment) -> {
                    if (attachment instanceof IInteractive iInteractive) {
                        iInteractive.onMouseButton(event.getButton(), event.getAction(), stack, gun, player);
                    }
                });
            } else if (stack.getItem() instanceof Ammunition ammunition) {
                ammunition.onRightClick(player, stack);
                event.setCanceled(true);
            }
        } else {
            Clients.MAIN_HAND_STATUS.buttonDown.set(false);
            Clients.MAIN_HAND_STATUS.ads = false;
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
                if (stackMain.getItem() instanceof IGun gun && !ReloadingHandler.isReloading()) {
                    if (gun.getGunProperties().fireModes.size() > 1) {
                        Clients.MAIN_HAND_STATUS.buttonDown.set(false);
                        player.playSound(SoundEvents.LEVER_CLICK, 0.5f, 1.5f);
                        PacketHandler.simpleChannel.sendToServer(new SwitchFireModePacket());
                    }
                }
            } else if (KeyBinds.TURN_FLASHLIGHT.isDown() && event.getAction() == 1) {
                ItemStack stack = player.getMainHandItem();
                if (!ReloadingHandler.isReloading() && !HandActionHandler.INSTANCE.hasTask() && stack.getItem() instanceof IGun gun) {
                    if (Flashlight.getFlashlightNum(stack, gun) <= 0) {
                        return;
                    }
                    boolean on = Flashlight.getFlashlightTurnOn(stack, gun);
                    PacketHandler.simpleChannel.sendToServer(new TurnFlashlightPacket(!on));
                    Flashlight.switchFlashlightMode(stack, gun);
                    int mode = Flashlight.getFlashlightMode(stack, gun);
                    if (mode == Flashlight.SPREAD) {
                        Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("tooltip.screen_info.spread_mode"), false);
                    }
                    if (mode == Flashlight.SEARCHLIGHT) {
                        Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("tooltip.screen_info.searchlight_mode"), false);
                    }
                }
            } else if (KeyBinds.RELOAD.isDown() && event.getAction() == 1) {
                handleReload(stackMain, player);
            } else if (KeyBinds.OPEN_GUN_MODIFY_SCREEN.isDown() && event.getAction() == 1) {
                PacketHandler.simpleChannel.sendToServer(new OpenGunModifyScreenPacket());
                ReloadingHandler.INSTANCE.breakTask();
                HandActionHandler.INSTANCE.breakTask();
            } else if (KeyBinds.OPEN_CLIENT_SETTINGS_SCREEN.isDown() && event.getAction() == 1) {
                Minecraft.getInstance().setScreen(new ClientSettingsScreen());
            } else if (KeyBinds.SWITCH_EFFECTIVE_SIGHT.isDown() && event.getAction() == 1) {
                Clients.MAIN_HAND_STATUS.attachmentsStatus.onSwitchEffectiveSight();
            }
            //45
            if (event.getAction() == 1 && stackMain.getItem() instanceof IGun gun) {
                AttachmentsHandler.INSTANCE.getAttachments(stackMain, gun).forEach((attachment) -> {
                    if (attachment instanceof IInteractive iInteractive) {
                        iInteractive.onKeyPress(event.getKey(), event.getAction(), stackMain, gun, player);
                    }
                });
            }
            Clients.displayGunInfoDetails =
                    event.getKey() == KeyBinds.SHOW_FULL_GUN_INFO.getKey().getValue() && event.getAction() == 2;
            Clients.debugKeyDown =
                    event.getKey() == KeyBinds.DEBUG_KEY.getKey().getValue() && event.getAction() == 2;
        }
    }

    private static void handleReload(ItemStack stack, Player player) {
        if (stack.getItem() instanceof IGun gun && !ReloadingHandler.isReloading()) {
            if (gun.clientReload(stack, player)) {
                Clients.MAIN_HAND_STATUS.buttonDown.set(false);
                ReloadingHandler.INSTANCE.setTask(gun.getReloadingTask(stack, player));
            }
        }
    }

    private static void handleUnload(ItemStack stack, Player player) {
        if (stack.getItem() instanceof IGun gun && !ReloadingHandler.isReloading() && gun.getAmmoLeft(stack) > 0) {
            ReloadingHandler.INSTANCE.setTask(gun.getUnloadingTask(stack, player));
        }
    }
}
