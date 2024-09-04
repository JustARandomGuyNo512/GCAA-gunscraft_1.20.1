package sheridan.gcaa.client.animation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.Mark;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AnimationSequence {
    private final long startTime;
    private float length;
    private int index = 0;
    private final List<Mark> animations = new ArrayList<>(10);
    private Mark lasting = null;

    public AnimationSequence() {
        startTime = System.currentTimeMillis();
    }

    public AnimationSequence(AnimationDefinition definition, long startTime) {
        animations.add(new Mark(definition, startTime));
        this.startTime = startTime;
    }

    public AnimationSequence(Mark mark) {
        animations.add(mark);
        startTime = mark.timeStamp;
    }

    public AnimationSequence append(AnimationDefinition definition) {
        animations.add(new Mark(definition, 0L));
        return this;
    }

    public AnimationSequence append(Mark mark) {
        animations.add(mark);
        return this;
    }

    public AnimationSequence finishBuild() {
        for (Mark mark : animations) {
            mark.timeStamp = startTime + (long) (length * 1000);
            if (mark.animationDefinition.looping() && mark.loopTimes != 0) {
                length += mark.animationDefinition.lengthInSeconds() * mark.loopTimes;
            } else {
                length += mark.animationDefinition.lengthInSeconds();
            }
            if (mark.stopAtLastFrame && lasting == null) {
                lasting = mark;
            }
        }
        return this;
    }

    public void apply(HierarchicalModel<?> root) {
        if (index >= animations.size()) {
            return;
        }
        Mark mark = animations.get(index);
        if (mark == lasting) {
            KeyframeAnimations.animateKeepLastPose(root, lasting.animationDefinition, lasting.timeStamp, lasting.scales);
        } else {
            KeyframeAnimations.animate(root, mark.animationDefinition, mark.timeStamp, mark.scales);
        }
        if (lasting != null && lasting != mark) {
            KeyframeAnimations.animateKeepLastPose(root, lasting.animationDefinition, lasting.timeStamp, lasting.scales);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public float getLength() {
        return length;
    }

    public boolean tick() {
        if (animations.size() == 0 || index >= animations.size()) {
            return true;
        }
        Mark mark = animations.get(index);
        if ((System.currentTimeMillis() - mark.timeStamp) * 0.001f >
                mark.animationDefinition.lengthInSeconds()) {
            if (mark.loop()) {
                mark.looped ++;
                if (mark.looped >= mark.loopTimes) {
                    index ++;
                } else {
                    mark.reset();
                    mark.onClientTick();
                }
            } else {
                index ++;
            }
        } else {
            mark.onClientTick();
        }
        if (index < animations.size()) {
            Mark mark1 = animations.get(index);
            if (mark1.stopAtLastFrame) {
                lasting = mark1;
            }
        }
        return index >= animations.size();
    }
}
