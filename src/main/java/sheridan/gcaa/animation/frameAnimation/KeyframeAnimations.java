package sheridan.gcaa.animation.frameAnimation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class KeyframeAnimations {
    public static final Vector3f DEFAULT_DIRECTION = new Vector3f(1,1,1f);

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, float scale, Vector3f direction, boolean stopIfOutOfTime) {
        float timeDis = (float)(System.currentTimeMillis() - (startTime + shift)) * 0.001F;
        if (stopIfOutOfTime && (!definition.looping() && timeDis > definition.lengthInSeconds())) {
            return;
        }
        float f = definition.looping() ? timeDis % definition.lengthInSeconds() : timeDis;

        for(Map.Entry<String, List<AnimationChannel>> entry : definition.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = root.getAnyDescendantWithName(entry.getKey());
            List<AnimationChannel> list = entry.getValue();
            optional.ifPresent((modelPart) -> {
                list.forEach((channel) -> {
                    Keyframe[] keyframes = channel.keyframes();
                    int currentIndex = Math.max(0, Mth.binarySearch(0, keyframes.length, (p_232315_) -> f <= keyframes[p_232315_].timestamp()) - 1);
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
                    nextFrame.interpolation().apply(direction, f2, keyframes, currentIndex, nextIndex, scale);
                    channel.target().apply(modelPart, direction);
                });
            });
        }

    }

    public static void animate(HierarchicalModel<?> root, AnimationDefinition definition, long startTime, long shift, float scale, Vector3f direction) {
        animate(root, definition, startTime, shift, scale, direction, true);
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
        public int tick;
        public String soundName;

        public SoundPoint(int tick, String soundName) {
            this.tick = tick;
            this.soundName = soundName;
        }

        public void playSound() {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                //SoundEvents.playSound(soundName, player, 1, 1);
            }
        }
    }


}
