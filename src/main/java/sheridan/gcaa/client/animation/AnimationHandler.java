package sheridan.gcaa.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilHandler;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class AnimationHandler {
    public static final String RELOAD = "reload";
    public static final String HAND_ACTION = "hand_action";
    public static final String INSPECT = "inspect";
    private final Deque<KeyframeAnimations.Mark> recoils = new ArrayDeque<>();
    private static final Timer timer = new Timer();
    public static final InertialRecoilHandler INERTIAL_RECOIL_HANDLER = new InertialRecoilHandler();
    public static final AnimationHandler INSTANCE = new AnimationHandler();
    private static final int MAX_KEYFRAME_ANIMATION_LEN = 12;
    private static final AtomicBoolean enableInertialRecoil = new AtomicBoolean(true);
    private static final Map<String, KeyframeAnimations.Mark> animations = new ConcurrentHashMap<>();

    protected AnimationHandler() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (enableInertialRecoil.get()) {
                        INERTIAL_RECOIL_HANDLER.update();
                    }
                    RecoilCameraHandler.INSTANCE.handle();
                } catch (Exception ignore) {}
            }
        }, 0, 10L);
    }

    /**
     * push keyframe animation recoil
     * */
    public void pushRecoil(AnimationDefinition recoilAnimation, long shootTime) {
        if (recoils.size() < MAX_KEYFRAME_ANIMATION_LEN) {
            recoils.add(new KeyframeAnimations.Mark(recoilAnimation, shootTime));
        } else {
            recoils.poll();
            recoils.add(new KeyframeAnimations.Mark(recoilAnimation, shootTime));
        }
    }

    /**
     * push inertial recoil
     * */
    public void pushRecoil(InertialRecoilData inertialRecoilData, float randomDirectionX, float randomDirectionY, float pRate,  float yRate)  {
        INERTIAL_RECOIL_HANDLER.onShoot(inertialRecoilData, randomDirectionX, randomDirectionY,
                      pRate, yRate);
    }

    /**
     * apply keyframe recoil animations to a model
     * */
    public void applyRecoil(HierarchicalModel<?> root) {
        for (KeyframeAnimations.Mark mark : recoils) {
            if (!KeyframeAnimations.checkIfOutOfTime(mark.timeStamp, 0, mark.animationDefinition)) {
                KeyframeAnimations.animate(root, mark.animationDefinition, mark.timeStamp, mark.scales);
            } else {
                recoils.remove(mark);
            }
        }
    }

    /**
     * apply inertial recoil transform to a pose stack
     * */
    public void applyInertialRecoil(PoseStack poseStack, InertialRecoilData data) {
        INERTIAL_RECOIL_HANDLER.applyTransform(poseStack, data.id, Clients.isInAds());
    }

    /**
     * false to stop inertial recoil handler update work and clear data
     * */
    public void setEnableInertialRecoil(boolean enable) {
        enableInertialRecoil.set(enable);
        if (!enable) {
            INERTIAL_RECOIL_HANDLER.clear();
        }
    }

    public void applyReload(HierarchicalModel<?> root) {
        apply(root, RELOAD);
    }

    public void applyHandAction(HierarchicalModel<?> root) {
        apply(root, HAND_ACTION);
    }

    public void applyInspect(HierarchicalModel<?> root) {
        apply(root, INSPECT);
    }

    public void apply(HierarchicalModel<?> root, String name) {
        KeyframeAnimations.Mark mark = animations.get(name);
        if (mark != null) {
            KeyframeAnimations.animate(root, mark.animationDefinition, mark.timeStamp, mark.scales);
        }
    }

    public void clearReload() {
        clearAnimation(RELOAD);
    }

    public void clearHandAction() {
        clearAnimation(HAND_ACTION);
    }

    public void clearInspect() {
        clearAnimation(INSPECT);
    }

    public void clearAnimation(String channel) {
        animations.remove(channel);
    }

    public void startReload(AnimationDefinition animationDefinition) {
        startAnimation(RELOAD, animationDefinition, true, true);
    }

    public void startHandAction(AnimationDefinition animationDefinition) {
        startAnimation(HAND_ACTION, animationDefinition, true, true);
    }

    public void startInspect(AnimationDefinition animationDefinition) {
        startAnimation(INSPECT, animationDefinition, true, false);
    }

    public void startAnimation(String channel, AnimationDefinition animationDefinition, boolean enableSound, boolean soundOnServer) {
        if (animationDefinition == null) {
            return;
        }
        animations.put(channel, new KeyframeAnimations.Mark(animationDefinition, System.currentTimeMillis()).enableSound(enableSound).soundOnServer(soundOnServer));
    }

    public void onClientTick() {
        if (animations.isEmpty()) {
            return;
        }
        Set<String> finished = new HashSet<>();
        for (Map.Entry<String, KeyframeAnimations.Mark> entry : animations.entrySet()) {
            KeyframeAnimations.Mark mark = entry.getValue();
            if ((System.currentTimeMillis() - mark.timeStamp) * 0.001f > mark.animationDefinition.lengthInSeconds()) {
                finished.add(entry.getKey());
            } else {
                entry.getValue().onClientTick();
            }
        }
        if (!finished.isEmpty()) {
            for (String id : finished)  {
                animations.remove(id);
            }
        }
    }

}
