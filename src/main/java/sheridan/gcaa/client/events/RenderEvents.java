package sheridan.gcaa.client.events;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.client.render.GunRenderer;
import sheridan.gcaa.client.render.gui.crosshair.CrossHairRenderer;
import sheridan.gcaa.client.screens.AttachmentsGuiContext;
import sheridan.gcaa.client.screens.AttachmentsScreen;
import sheridan.gcaa.client.screens.GunDebugAdjustScreen;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.utils.RenderAndMathUtils;

import static org.lwjgl.opengl.GL30C.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onRenderHandFP(RenderHandEvent event) {
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            ClientWeaponStatus status = Clients.mainHandStatus;
            status.equipProgress = event.getEquipProgress();
            if (Clients.mainHandStatus.holdingGun.get()) {
                if (Minecraft.getInstance().options.bobView().get()) {
                    GlobalWeaponBobbing.INSTANCE.update(event.getPartialTick(), status.equipProgress);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderCrossHair(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            if (Clients.holdingGun()) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player != null) {
                    ItemStack stack = player.getMainHandItem();
                    if (stack.getItem() instanceof IGun gun && !gun.isSniper()) {
                        if (!Clients.mainHandStatus.ads && !Clients.shouldHideFPRender) {
                            CrossHairRenderer.INSTANCE.render(0, 16, gun, event.getGuiGraphics(), player, stack, event.getWindow(), event.getPartialTick());
                        }
                        event.setCanceled(true);
                    }
                    if (Clients.mainHandStatus.ads) {
                        event.setCanceled(true);
                    }
                    if (Clients.shouldHideFPRender) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderInventoryTab(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof AttachmentsScreen) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void handleCameraAnimation(ViewportEvent.ComputeCameraAngles event) {
        CameraAnimationHandler.INSTANCE.apply(event);
        CameraAnimationHandler.INSTANCE.clear();
    }

    @SubscribeEvent
    public static void renderGunInfo(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof AttachmentsScreen) {
            return;
        }
        Player player = minecraft.player;
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IGun gun) {
                Font font = minecraft.font;
                GuiGraphics guiGraphics = event.getGuiGraphics();
                Window window = Minecraft.getInstance().getWindow();
                int width = window.getGuiScaledWidth();
                int height = window.getGuiScaledHeight();
                renderAmmoCounter(stack, guiGraphics, gun, font, width, height);
                IGunFireMode gunFireMode = gun.getFireMode(stack);
                if (gunFireMode != null) {
                    Component tooltipName = gunFireMode.getTooltipName();
                    if (tooltipName != null) {
                        guiGraphics.drawString(font, tooltipName.getString(), 0.8f * width, 0.85f * height, -1,  true);
                    }
                }
            }
        }
    }

    private static void renderAmmoCounter(ItemStack stack, GuiGraphics guiGraphics, IGun gun, Font font, int width, int height)  {
        int magSize = gun.getMagSize(stack);
        int ammoLeft = gun.getAmmoLeft(stack);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(0, 1, 0, 1);
        float leftRate = ammoLeft / (float) magSize;
        if (leftRate < 0.5f) {
            guiGraphics.setColor(1, 1, 0, 1);
            if (leftRate < 0.21f) {
                guiGraphics.setColor(1, 0, 0, 1);
            }
        }
        guiGraphics.drawString(font, ammoLeft + "", 0.8f * width, 0.8f * height, -1,  true);
        guiGraphics.setColor(1, 1, 1, 1);
        guiGraphics.drawString(font, " / " + magSize, 0.8f * width + font.width(ammoLeft + ""), 0.8f * height, -1,  true);
    }


    private static final BufferBuilder GUN_MODEL_BUFFER = new BufferBuilder(1024);
    private static float x, y, rx, ry, scale = 1;
    public static void setAttachmentScreenModelPos(float x, float y) {
        RenderEvents.x = x;
        RenderEvents.y = y;
    }

    public static void setAttachmentScreenModelScale(float scale) {
        RenderEvents.scale = scale;
    }

    public static void setAttachmentScreenModelRot(float rx, float ry) {
        RenderEvents.rx = rx;
        RenderEvents.ry = ry;
    }

    public static float getAttachmentScreenModelScale() {
        return scale;
    }

    public static Vector2f getAttachmentScreenModelPos() {
        return new Vector2f(x, y);
    }

    public static Vector2f getAttachmentScreenModelRot() {
        return new Vector2f(rx, ry);
    }

    public static void resetAttachmentScreenModelState() {
        x = y = rx = ry = 0;
        scale = 1;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderAttachmentScreenModel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            Screen screen = minecraft.screen;
            if (player != null && (screen instanceof AttachmentsScreen || screen instanceof GunDebugAdjustScreen)) {
                if (screen instanceof GunDebugAdjustScreen gunDebugAdjustScreen) {
                    if (!"AttachmentScreen".equals(gunDebugAdjustScreen.getViewModeName())) {
                        Clients.shouldHideFPRender = false;
                        resetAttachmentScreenModelState();
                        return;
                    }
                }
                ItemStack itemStack = player.getMainHandItem();
                if (itemStack.getItem() instanceof IGun gun) {
                    Clients.shouldHideFPRender = true;
                    IGunModel model = GunModelRegister.getModel(gun);
                    DisplayData displayData = GunModelRegister.getDisplayData(gun);
                    MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(GUN_MODEL_BUFFER);
                    PoseStack poseStack = new PoseStack();
                    AttachmentsGuiContext context = screen instanceof AttachmentsScreen ? ((AttachmentsScreen) screen).getContext() : null;
                    GL11.glDepthMask(true);

                    int currentFrameBuffer = RenderAndMathUtils.getCurrentFramebuffer();
                    RenderAndMathUtils.copyDepthBuffer(currentFrameBuffer,minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
                    glBindFramebuffer(GL_FRAMEBUFFER, currentFrameBuffer);
                    RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
                    RenderSystem.enableDepthTest();

                    GunRenderer.renderInAttachmentScreen(poseStack, itemStack, gun, model, bufferSource, displayData, x, y, rx, ry, scale, context);
                    bufferSource.endBatch();
                    GUN_MODEL_BUFFER.clear();

                    RenderAndMathUtils.restoreDepthBuffer(currentFrameBuffer,minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
                    glBindFramebuffer(GL_FRAMEBUFFER, currentFrameBuffer);
                    return;
                }
            }
            Clients.shouldHideFPRender = false;
            resetAttachmentScreenModelState();
        }
    }

}
