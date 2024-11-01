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
import net.minecraft.network.chat.Style;
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
import org.joml.*;
import org.lwjgl.opengl.GL11;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.SprintingHandler;
import sheridan.gcaa.client.animation.CameraAnimationHandler;
import sheridan.gcaa.client.model.attachments.ScopeModel;
import sheridan.gcaa.client.model.guns.IGunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GlobalWeaponBobbing;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.GunRenderer;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellRenderer;
import sheridan.gcaa.client.render.gui.crosshair.CrossHairRenderer;
import sheridan.gcaa.client.render.postEffect.PostChain;
import sheridan.gcaa.client.render.postEffect.PostPass;
import sheridan.gcaa.client.screens.AttachmentsGuiContext;
import sheridan.gcaa.client.screens.AttachmentsScreen;
import sheridan.gcaa.client.screens.GunDebugAdjustScreen;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.attachments.grips.Flashlight;
import sheridan.gcaa.items.gun.HandActionGun;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30C.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderEvents {
    private static final ResourceLocation CHAMBER_EMPTY = new ResourceLocation(GCAA.MODID, "textures/gui/screen_layout_icon/chamber_empty.png");
    private static final ResourceLocation CHAMBER_FILLED = new ResourceLocation(GCAA.MODID, "textures/gui/screen_layout_icon/chamber_filled.png");
    private static final ResourceLocation HAS_GRENADE = new ResourceLocation(GCAA.MODID, "textures/gui/screen_layout_icon/has_grenade.png");
    private static final ResourceLocation FEED_BACK = new ResourceLocation(GCAA.MODID, "textures/gui/crosshair/feed_back.png");
    private static final Map<String, Long> TEMP_TIMERS = new HashMap<>();
    private static final String MAGNIFICATION = "magnification_tip";
    private static final String SHOT = "shot";
    private static final String HEADSHOT = "headshot";
    private static float magnificationTip = 0;
    private static int magnificationTipColor = 0;

    static {
        TEMP_TIMERS.put(MAGNIFICATION, 0L);
        TEMP_TIMERS.put(SHOT, 0L);
        TEMP_TIMERS.put(HEADSHOT, 0L);
    }

    static PostChain flashlight;
    static boolean failedLoadingFlashLightShader = false;
    static int lastWidth;
    static int lastHeight;

    static Vector3f ZERO = new Vector3f(0,0,0.1f);
    static Vector3f To = ZERO;
    static boolean doFlashlightEffect = false;
    static float Luminance = 0f;
    static float range = 20f;
    static float lightFov = 5f;
    static float MinZ = 0;

    public static void clearFlashlightEffectData() {
        doFlashlightEffect = false;
        Luminance = 0f;
        range = 20f;
        lightFov = 5f;
        MinZ = 0;
    }

    static boolean canNotCallFlashlight() {
        return failedLoadingFlashLightShader ||
                Minecraft.getInstance().screen instanceof AttachmentsScreen ||
                Minecraft.getInstance().screen instanceof GunDebugAdjustScreen;
    }

    public static void callFlashlightEffect(GunRenderContext context, ModelPart near, ModelPart far, float luminance) {
        if (canNotCallFlashlight()) {
            return;
        }
        PoseStack nearPose = RenderAndMathUtils.copyPoseStack(context.poseStack);
        near.translateAndRotate(nearPose);
        Vector3f from = nearPose.last().pose().getTranslation(new Vector3f(0,0,0));
        PoseStack farPose = RenderAndMathUtils.copyPoseStack(context.poseStack);
        far.translateAndRotate(farPose);
        Vector3f end = farPose.last().pose().getTranslation(new Vector3f(0,0,0));
        RenderEvents.To = new Vector3f(end.x - from.x, end.y - from.y, end.z - from.z - from.z * 0.1f);
        if (Clients.currentStage != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Matrix4f m0 = new Matrix4f(RenderSystem.getModelViewMatrix());
            Matrix4f m1 = new Matrix4f(RenderSystem.getProjectionMatrix());
            Vector4f vector4f = new Vector4f(end.x, end.y, end.z, 1.0f);
            Vector4f ndc = vector4f.mul(m0).mul(m1);
            float depth = (ndc.z / ndc.w) / 2f + 0.5f;
            if (depth > MinZ) {
                MinZ = depth;
            }
        } else {
            MinZ = 0;
        }
        Luminance += luminance;
        range += luminance * 5.25f;
        lightFov += luminance / 1.5f;
        doFlashlightEffect = true;
    }
    @SubscribeEvent
    public static void renderFlashLight(RenderLevelStageEvent event) {
        Clients.currentStage = event.getStage();
        if (canNotCallFlashlight() || !doFlashlightEffect) {
            clearFlashlightEffectData();
            return;
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && stack.getItem() instanceof IGun gun) {
                if (flashlight == null) {
                    try {
                        PostChain postChain = new PostChain(Minecraft.getInstance().textureManager,
                                Minecraft.getInstance().getResourceManager(),
                                Minecraft.getInstance().getMainRenderTarget(),
                                new ResourceLocation(GCAA.MODID, "shaders/post/flashlight.json"));
                        lastWidth = Minecraft.getInstance().getWindow().getWidth();
                        lastHeight = Minecraft.getInstance().getWindow().getHeight();
                        postChain.resize(lastWidth, lastHeight);
                        flashlight = postChain;
                    } catch (Exception e) {
                        e.printStackTrace();
                        GCAA.LOGGER.info(e.getMessage());
                        failedLoadingFlashLightShader = true;
                        player.sendSystemMessage(Component.literal("Error loading flashlight shader!!!")
                                .setStyle(Style.EMPTY.withColor(0xFF0000)));
                    }
                } else {
                    int mode = Flashlight.getFlashlightMode(stack, gun);
                    if (Luminance == 0 || mode == Flashlight.OFF) {
                        clearFlashlightEffectData();
                        return;
                    }
                    int width = Minecraft.getInstance().getWindow().getWidth();
                    int height = Minecraft.getInstance().getWindow().getHeight();
                    if (width != lastWidth || height != lastHeight) {
                        lastHeight = height;
                        lastWidth = width;
                        flashlight.resize(width, height);
                    }
                    List<PostPass> passes = flashlight.passes;
                    Matrix4f inversePerspectiveProjMat = new Matrix4f(RenderSystem.getProjectionMatrix().invert());
                    Matrix4f inverseModelViewMat = new Matrix4f(RenderSystem.getModelViewMatrix().invert());
                    if (mode == Flashlight.SPREAD) {
                        lightFov *= 1.1f;
                        range *= 0.9f;
                    } else if (mode == Flashlight.SEARCHLIGHT) {
                        lightFov *= 0.9f;
                        range *= 1.5f;
                        Luminance *= 1.6f;
                    }
                    for (PostPass pass : passes) {
                        pass.getEffect().safeGetUniform("InversePerspectiveProjMat").set(inversePerspectiveProjMat);
                        pass.getEffect().safeGetUniform("InverseModelViewMat").set(inverseModelViewMat);
                        pass.getEffect().safeGetUniform("To").set(To);
                        pass.getEffect().safeGetUniform("Angle").set((float) Math.toRadians(Mth.clamp(lightFov, 1, 20)));
                        pass.getEffect().safeGetUniform("Range").set(Mth.clamp(range, 10, mode == Flashlight.SEARCHLIGHT ? 180 : 95));
                        pass.getEffect().safeGetUniform("Luminance").set(Mth.clamp(Luminance, 0.1f,  mode == Flashlight.SEARCHLIGHT ? 20f : 13f));
                        pass.getEffect().safeGetUniform("MinZ").set(MinZ);
                        pass.getEffect().safeGetUniform("Mode").set(mode);
                    }
                    flashlight.process(event.getPartialTick());
                    Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                    clearFlashlightEffectData();
                }
            }
        }
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
                if (player == null) {
                    return;
                }
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof IGun gun) {
                    if (!Clients.mainHandStatus.ads && !Clients.shouldHideFPRender && !SprintingHandler.INSTANCE.isSprinting()) {
                        CrossHairRenderer.INSTANCE.render(16, gun, event.getGuiGraphics(), player, stack, event.getPartialTick(),
                                System.currentTimeMillis() - TEMP_TIMERS.get(SHOT) < 100, System.currentTimeMillis() - TEMP_TIMERS.get(HEADSHOT) < 100);
                    }
                    event.setCanceled(true);
                }
                if (Clients.mainHandStatus.ads || Clients.shouldHideFPRender || SprintingHandler.INSTANCE.isSprinting()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderInventoryTab(RenderGuiOverlayEvent.Pre event) {
        ResourceLocation id = event.getOverlay().id();
        if (id.equals(VanillaGuiOverlay.HOTBAR.id()) || id.equals(VanillaGuiOverlay.EXPERIENCE_BAR.id()) ||
                id.equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) || id.equals(VanillaGuiOverlay.FOOD_LEVEL.id())) {
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
                    guiGraphics.drawString(font, str, (width - font.width(str)) * 0.5f, 0.74f * height, magnificationTipColor,  true);
                }
                if (now - TEMP_TIMERS.get(HEADSHOT) < 300) {
                    String str = Component.translatable("tooltip.screen_info.headshot").getString();
                    float alpha = (now - TEMP_TIMERS.get(SHOT)) / 300f;
                    event.getGuiGraphics().setColor(1,0,0,alpha);
                    guiGraphics.drawString(font, str, (width - font.width(str)) * 0.5f, 0.725f * height, -1,  true);
                    event.getGuiGraphics().setColor(1,1,1,1);
                }
            }
        }
    }

    public static void callHeadShotFeedBack(boolean isHeadshot) {
        TEMP_TIMERS.put(SHOT, System.currentTimeMillis());
        if (isHeadshot) {
            TEMP_TIMERS.put(HEADSHOT, System.currentTimeMillis());
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
