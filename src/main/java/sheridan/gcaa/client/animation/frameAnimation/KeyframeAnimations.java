package sheridan.gcaa.client.animation.frameAnimation;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.PlayerSoundPacket;
import sheridan.gcaa.sounds.ModSounds;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class KeyframeAnimations {
    private static final Vector3f INTERPOLATION_RESULT_CACHE = new Vector3f(0,0,0);

    public static void _animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, float scaleX, float scaleY, float scaleZ, boolean stopIfOutOfTime) {
        float timeDis = (float)(System.currentTimeMillis() - (startTime + shift)) * 0.001F;
        if (stopIfOutOfTime && (!definition.looping() && timeDis > definition.lengthInSeconds())) {
            return;
        }
        float f = definition.looping() ? timeDis % definition.lengthInSeconds() : timeDis;

        for(Map.Entry<String, List<AnimationChannel>> entry : definition.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = root.getAnyDescendantWithName(entry.getKey());
            List<AnimationChannel> list = entry.getValue();
            optional.ifPresent((modelPart) -> list.forEach((channel) -> {
                Keyframe[] keyframes = channel.keyframes();
                int currentIndex = Math.max(0, Mth.binarySearch(0, keyframes.length, (index) -> f <= keyframes[index].timestamp()) - 1);
                int nextIndex = Math.min(keyframes.length - 1, currentIndex + 1);
                Keyframe prevFrame = keyframes[currentIndex];
                Keyframe nextFrame = keyframes[nextIndex];
                float f1 = f - prevFrame.timestamp();
                float f2;
                if (nextIndex != currentIndex) {
                    f2 = Mth.clamp(f1 / (nextFrame.timestamp() - prevFrame.timestamp()), 0.0F, 1.0F);
                } else {
                    f2 = 0.0F;
                }
                nextFrame.interpolation().apply(INTERPOLATION_RESULT_CACHE, f2, keyframes, currentIndex, nextIndex, scaleX, scaleY, scaleZ);
                channel.target().apply(modelPart, INTERPOLATION_RESULT_CACHE);
            }));
        }

    }

    public static void _animateToModelPart(ModelPart pose, AnimationDefinition definition, long startTime, long shift, float scaleX, float scaleY, float scaleZ, boolean stopIfOutOfTime) {
        float timeDis = (float)(System.currentTimeMillis() - (startTime + shift)) * 0.001F;
        if (stopIfOutOfTime && (!definition.looping() && timeDis > definition.lengthInSeconds())) {
            return;
        }
        float f = definition.looping() ? timeDis % definition.lengthInSeconds() : timeDis;
        for(Map.Entry<String, List<AnimationChannel>> entry : definition.boneAnimations().entrySet()) {
            List<AnimationChannel> list = entry.getValue();
            list.forEach((channel) -> {
                Keyframe[] keyframes = channel.keyframes();
                int currentIndex = Math.max(0, Mth.binarySearch(0, keyframes.length, (index) -> f <= keyframes[index].timestamp()) - 1);
                int nextIndex = Math.min(keyframes.length - 1, currentIndex + 1);
                Keyframe prevFrame = keyframes[currentIndex];
                Keyframe nextFrame = keyframes[nextIndex];
                float f1 = f - prevFrame.timestamp();
                float f2;
                if (nextIndex != currentIndex) {
                    f2 = Mth.clamp(f1 / (nextFrame.timestamp() - prevFrame.timestamp()), 0.0F, 1.0F);
                } else {
                    f2 = 0.0F;
                }
                nextFrame.interpolation().apply(INTERPOLATION_RESULT_CACHE, f2, keyframes, currentIndex, nextIndex, scaleX, scaleY, scaleZ);
                channel.target().apply(pose, INTERPOLATION_RESULT_CACHE);
            });
        }

    }

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime) {
        _animate(root, definition, startTime, 0, 1, 1, 1, true);
    }

    public static void animateKeepLastPose(HierarchicalModel<?> root, AnimationDefinition definition, long startTime) {
        _animate(root, definition, startTime, 0, 1, 1, 1, false);
    }

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, float scale) {
        _animate(root, definition, startTime, 0, scale, scale, scale, true);
    }

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, float scaleX, float scaleY, float scaleZ) {
        _animate(root, definition, startTime, 0, scaleX, scaleY, scaleZ, true);
    }
    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, Vector3f scales) {
        _animate(root, definition, startTime, 0, scales.x, scales.y, scales.z, true);
    }

    public static void animateKeepLastPose(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, Vector3f scales) {
        _animate(root, definition, startTime, 0, scales.x, scales.y, scales.z, false);
    }
    public static void animateLooping(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, Vector3f scales) {
        _animate(root, definition, startTime, 0, scales.x, scales.y, scales.z, false);
    }

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, float scale) {
        _animate(root, definition, startTime, shift, scale, scale, scale, true);
    }
    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, float scaleX, float scaleY, float scaleZ) {
        _animate(root, definition, startTime, shift, scaleX, scaleY, scaleZ, true);
    }
    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, Vector3f scales) {
        _animate(root, definition, startTime, shift, scales.x, scales.y, scales.z, true);
    }

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, float scale, boolean stopIfOutOfTime)  {
        _animate(root, definition, startTime, shift, scale, scale, scale, stopIfOutOfTime);
    }

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, float scaleX, float scaleY, float scaleZ, boolean stopIfOutOfTime) {
        _animate(root, definition, startTime, shift, scaleX, scaleY, scaleZ, stopIfOutOfTime);
    }
    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, Vector3f scales, boolean stopIfOutOfTime) {
        _animate(root, definition, startTime, shift, scales.x, scales.y, scales.z, stopIfOutOfTime);
    }

    public static boolean checkIfOutOfTime(long startTime, long shift, AnimationDefinition definition) {
        float timeDis = (float)(System.currentTimeMillis() - (startTime + shift)) * 0.001F;
        return  (!definition.looping() && timeDis > definition.lengthInSeconds());
    }

    public static Vector3f posVec(float pX, float pY, float pZ) {
        return new Vector3f(pX, -pY, pZ);
    }

    public static Vector3f degreeVec(float pXDegrees, float pYDegrees, float pZDegrees) {
        return new Vector3f(pXDegrees * ((float)Math.PI / 180F), pYDegrees * ((float)Math.PI / 180F), pZDegrees * ((float)Math.PI / 180F));
    }

    public static Vector3f scaleVec(double pXScale, double pYScale, double pZScale) {
        return new Vector3f((float)(pXScale - 1.0D), (float)(pYScale - 1.0D), (float)(pZScale - 1.0D));
    }

    @OnlyIn(Dist.CLIENT)
    public static class SoundPoint{
        public static final ResourceLocation EMPTY_SOUND = new ResourceLocation(GCAA.MODID, "empty_sound");
        public int tick;
        public ResourceLocation soundName;

        public SoundPoint(int tick, ResourceLocation soundName) {
            this.tick = tick;
            this.soundName = soundName;
        }

        public void playSound(boolean soundOnServer) {
            if (EMPTY_SOUND.equals(this.soundName)) {
                return;
            }
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ModSounds.sound(1,1, player, soundName);
                if (soundOnServer) {
                    PacketHandler.simpleChannel.sendToServer(new PlayerSoundPacket(soundName.toString()));
                }
            }
        }

        @Override
        public String toString() {
            return "SoundPoint{" +
                    "tick=" + tick +
                    ", soundName=" + soundName +
                    '}';
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Mark{
        private static final Vector3f DEFAULT_SCALE = new Vector3f(1,1,1);
        public AnimationDefinition animationDefinition;
        public long timeStamp;
        public Vector3f scales = DEFAULT_SCALE;
        private int tick = 0;
        private int prevSoundIndex = -1;
        private boolean enableSound = false;
        public boolean soundOnServer = false;
        public boolean stopAtLastFrame = false;
        public int loopTimes = 0;
        public int looped = 0;

        public Mark(AnimationDefinition animationDefinition, long timeStamp, Vector3f scales) {
            this.animationDefinition = animationDefinition;
            this.timeStamp = timeStamp;
            this.scales = scales;
        }

        public Mark(AnimationDefinition animationDefinition, long timeStamp) {
            this.animationDefinition = animationDefinition;
            this.timeStamp = timeStamp;
        }

        public Mark(AnimationDefinition animationDefinition) {
            this.animationDefinition = animationDefinition;
            this.timeStamp = 0L;
        }

        public Mark enableSound(boolean enableSound) {
            this.enableSound = enableSound;
            return this;
        }

        public Mark setLoopTimes(int loopTimes) {
            this.loopTimes = loopTimes;
            return this;
        }

        public Mark soundOnServer(boolean soundOnServer) {
            this.soundOnServer = soundOnServer;
            return this;
        }

        public Mark stopAtLastFrame() {
            this.stopAtLastFrame = true;
            return this;
        }

        public boolean loop() {
            return animationDefinition.looping() && loopTimes != 0 && !stopAtLastFrame;
        }

        public void reset() {
            prevSoundIndex = -1;
            tick = 0;
            timeStamp = System.currentTimeMillis();
        }

        public void onClientTick() {
            if (enableSound) {
                List<SoundPoint> soundPoints = animationDefinition.soundPoints();
                if (soundPoints == null || !(soundPoints.size() > 0)) {
                    return;
                }
                int soundIndex = Math.max(0, Mth.binarySearch(0, soundPoints.size(),
                        (index) -> tick < soundPoints.get(index).tick) - 1);
                if (soundIndex != prevSoundIndex) {
                    soundPoints.get(soundIndex).playSound(soundOnServer);
                    prevSoundIndex = soundIndex;
                }
            }
            tick++;
        }
    }

}
