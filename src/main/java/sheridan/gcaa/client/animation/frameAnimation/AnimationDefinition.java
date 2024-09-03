package sheridan.gcaa.client.animation.frameAnimation;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;

@OnlyIn(Dist.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations, List<KeyframeAnimations.SoundPoint> soundPoints)  {
    public float lengthInSeconds() {
        return this.lengthInSeconds;
    }
    public boolean looping() {
        return this.looping;
    }
    public Map<String, List<AnimationChannel>> boneAnimations() {
        return this.boneAnimations;
    }

    public List<KeyframeAnimations.SoundPoint> soundPoints() {
        return this.soundPoints;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;
        private List<KeyframeAnimations.SoundPoint> soundPoints;

        public static AnimationDefinition.Builder withLength(float pLengthInSeconds) {
            return new AnimationDefinition.Builder(pLengthInSeconds);
        }

        private Builder(float pLengthInSeconds) {
            this.length = pLengthInSeconds;
        }

        public AnimationDefinition.Builder looping() {
            this.looping = true;
            return this;
        }

        public AnimationDefinition.Builder setLooping(boolean looping) {
            this.looping = looping;
            return this;
        }

        public AnimationDefinition.Builder addAnimation(String pBone, AnimationChannel pAnimationChannel) {
            (this.animationByBone.computeIfAbsent(pBone, (p_232278_) -> Lists.newArrayList())).add(pAnimationChannel);
            return this;
        }

        public AnimationDefinition.Builder addSoundPoint(KeyframeAnimations.SoundPoint soundPoint) {
            if (soundPoints == null) {
                soundPoints = new ArrayList<>();
                soundPoints.add(new KeyframeAnimations.SoundPoint(0, KeyframeAnimations.SoundPoint.EMPTY_SOUND));
            }
            soundPoints.add(soundPoint);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone, soundPoints);
        }
    }
}
