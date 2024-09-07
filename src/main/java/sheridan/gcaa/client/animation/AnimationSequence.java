package sheridan.gcaa.client.animation;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.animation.frameAnimation.Mark;
import sheridan.gcaa.client.model.modelPart.HierarchicalModel;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AnimationSequence {
    private final long startTime;
    private float length;
    private final List<Mark> animations = new ArrayList<>(10);

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
        if (length != 0) {
            return this;
        }
        for (Mark mark : animations) {
            mark.timeStamp = startTime + (long) (length * 1000);
            length += mark.loop() ? mark.lengthInSec() * mark.loopTimes : mark.lengthInSec();
        }
        return this;
    }

    public void apply(HierarchicalModel<?> root) {
        if (animations.isEmpty()) {
            return;
        }
        int prevIndex = getIndex();
        if (prevIndex >= 0 && prevIndex < animations.size()) {
            Mark mark = animations.get(prevIndex);
            KeyframeAnimations.animate(root, mark.animationDefinition, mark.timeStamp, mark.scales);
        }
    }

    private int getIndex() {
        return Mth.binarySearch(0, animations.size(), (index) -> animations.get(index).timeStamp >= System.currentTimeMillis()) - 1;
    }

    public long getStartTime() {
        return startTime;
    }

    public float getLength() {
        return length;
    }

    public boolean tick() {
        if (animations.isEmpty()) {
            return true;
        }
        long now = System.currentTimeMillis();
        int prevIndex = getIndex();
        if (prevIndex >= 0 && prevIndex < animations.size()) {
            Mark mark = animations.get(prevIndex);
            long length = mark.loop() ? mark.length() * mark.loopTimes : mark.length();
            long dis = now - mark.timeStamp;
            if (dis <= length) {
                if (mark.shouldLoop() && dis > mark.length() * mark.looped) {
                    mark.looped ++;
                    mark.reset();
                }
                mark.onClientTick();
            }
        }
        return now - startTime > (long) (this.length * 1000) + 1;
    }
}
