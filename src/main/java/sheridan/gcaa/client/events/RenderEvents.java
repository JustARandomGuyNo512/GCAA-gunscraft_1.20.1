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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.model.attachments.ScopeModel;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.client.render.GunRenderer;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellRenderer;
import sheridan.gcaa.client.render.gui.crosshair.CrossHairRenderer;
import sheridan.gcaa.client.screens.AttachmentsGuiContext;
import sheridan.gcaa.client.screens.AttachmentsScreen;
import sheridan.gcaa.client.screens.GunDebugAdjustScreen;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30C.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderEvents {
    private static final ResourceLocation CHAMBER_EMPTY = new ResourceLocation(GCAA.MODID, "textures/gui/screen_layout_icon/chamber_empty.png");
    private static final ResourceLocation CHAMBER_FILLED = new ResourceLocation(GCAA.MODID, "textures/gui/screen_layout_icon/chamber_filled.png");
    private static final ResourceLocation HAS_GRENADE = new ResourceLocation(GCAA.MODID, "textures/gui/screen_layout_icon/has_grenade.png");
    private static final Map<String, Long> TEMP_TIMERS = new HashMap<>();
    private static final String MAGNIFICATION = "magnification_tip";
    private static final String HEADSHOT = "headshot";
    private static float magnificationTip = 0;
    private static int magnificationTipColor = 0;

    static {
        TEMP_TIMERS.put(MAGNIFICATION, 0L);
        TEMP_TIMERS.put(HEADSHOT, 0L);
    }

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
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Clients.mainHandStatus.isHoldingGun()) {
                BulletShellRenderer.update();
            } else {
                BulletShellRenderer.clear();
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
                    if (stack.getItem() instanceof IGun gun) {
                        if (!Clients.mainHandStatus.ads && !Clients.shouldHideFPRender) {
                            CrossHairRenderer.INSTANCE.render(16, gun, event.getGuiGraphics(), player, stack, event.getPartialTick());
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

    public static void renderScopeMagnificationTip(Scope scope, float rate, int color) {
        if (Float.isNaN(rate)) {
            return;
        }
        magnificationTip = Mth.lerp(rate, scope.maxMagnification, scope.minMagnification);
        magnificationTipColor = color;
        TEMP_TIMERS.put(MAGNIFICATION, System.currentTimeMillis());
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
            long now = System.currentTimeMillis();
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
                ArrayList<ResourceLocation> rightBottomIcons = new ArrayList<>();
                if (stack.getItem() instanceof HandActionGun handActionGun) {
                    ResourceLocation texture = (handActionGun.needHandAction(stack) || gun.getAmmoLeft(stack) == 0) ? CHAMBER_EMPTY : CHAMBER_FILLED;
                    rightBottomIcons.add(texture);
                }
                if (GrenadeLauncher.hasGrenade(stack, gun)) {
                    rightBottomIcons.add(HAS_GRENADE);
                }
                event.getGuiGraphics().flush();
                RenderSystem.enableBlend();
                for (int i = 0; i < rightBottomIcons.size(); i++) {
                    event.getGuiGraphics().blit(rightBottomIcons.get(i),
                            (int) ((0.8f + 0.1f * i) * window.getGuiScaledWidth()), (int) (0.9f * window.getGuiScaledHeight()),
                            0,0, 8,8, 8, 8);
                }
                RenderSystem.disableBlend();
                if (now - TEMP_TIMERS.get(MAGNIFICATION) < 500) {
                    String str = "x" + Math.round(magnificationTip * 10.0) / 10.0;
                    guiGraphics.drawString(font, str, (width - font.width(str)) * 0.5f, 0.75f * height, magnificationTipColor,  true);
                }
                if (now - TEMP_TIMERS.get(HEADSHOT) < 300) {
                    String str = Component.translatable("tooltip.screen_info.headshot").getString();
                    float alpha = (now - TEMP_TIMERS.get(HEADSHOT)) / 300f;
                    event.getGuiGraphics().setColor(1,0,0,alpha);
                    guiGraphics.drawString(font, str, (width - font.width(str)) * 0.5f, 0.8f * height, -1,  true);
                    event.getGuiGraphics().setColor(1,1,1,1);
                }
            }
        }
    }

    public static void callHeadShotFeedBack() {
        TEMP_TIMERS.put(HEADSHOT, System.currentTimeMillis());
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

    @SubscribeEvent
    public static void onFovCompute(ViewportEvent.ComputeFov event) {
        if (Clients.isInAds() && Clients.mainHandStatus.getEffectiveSight() instanceof Scope scope) {
            float adsProgress = Clients.mainHandStatus.getLerpAdsProgress(event.getPartialTick());
            double prevFov = event.getFOV();
            if (event.usedConfiguredFov()) {
                float magnificationRate = Clients.mainHandStatus.attachmentsStatus.getScopeMagnificationRate();
                if (Float.isNaN(magnificationRate)) {
                    magnificationRate = 0;
                }
                float magnification = Mth.lerp(magnificationRate, scope.maxMagnification, scope.minMagnification);
                float newFov = (float) Mth.lerp(Math.pow(adsProgress, 3), prevFov, Scope.getFov(magnification));
                event.setFOV(newFov);
                Clients.fovModify = newFov;
                return;
            } else {
                if (AttachmentsRegister.getModel(scope) instanceof ScopeModel scopeModel) {
                    if (scopeModel.useModelFovModifyWhenAds()) {
                        double newFov = Mth.lerp(Math.pow(adsProgress, 4), prevFov, scopeModel.modelFovModifyWhenAds());
                        event.setFOV(newFov);
                        Clients.gunModelFovModify = (float) newFov;
                    }
                    Clients.weaponAdsZMinDistance = scopeModel.getMinDisZDistance(adsProgress);
                    return;
                }
            }
        }
        Clients.weaponAdsZMinDistance = Float.NaN;
        Clients.fovModify = Float.NaN;
        Clients.gunModelFovModify = Float.NaN;
    }

}
