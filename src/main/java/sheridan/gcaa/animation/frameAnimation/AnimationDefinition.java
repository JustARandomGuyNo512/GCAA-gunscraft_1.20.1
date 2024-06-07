package sheridan.gcaa.animation.frameAnimation;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;

@OnlyIn(Dist.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
    public float lengthInSeconds() {
        return this.lengthInSeconds;
    }
    public boolean looping() {
        return this.looping;
    }
    public Map<String, List<AnimationChannel>> boneAnimations() {
        return this.boneAnimations;
    }

    public void print() {
        System.out.println(lengthInSeconds);
        System.out.println(looping);
        for (Map.Entry<String, List<AnimationChannel>> entry : boneAnimations.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(boneAnimations);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;

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
            (this.animationByBone.computeIfAbsent(pBone, (p_232278_) -> {
                return Lists.newArrayList();
            })).add(pAnimationChannel);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone);
        }
    }
}
