//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package sheridan.gcaa.client.animation.frameAnimation;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.client.model.modelPart.IAnimatedModelPart;

@OnlyIn(Dist.CLIENT)
public record AnimationChannel(AnimationChannel.Target target, Keyframe... keyframes) {

    public AnimationChannel.Target target() {
        return this.target;
    }

    public Keyframe[] keyframes() {
        return this.keyframes;
    }

    @OnlyIn(Dist.CLIENT)
    public interface Target {
        void apply(IAnimatedModelPart var1, Vector3f var2);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Targets {
        public static final AnimationChannel.Target POSITION = IAnimatedModelPart::offsetPos;
        public static final AnimationChannel.Target ROTATION = IAnimatedModelPart::offsetRotation;
        public static final AnimationChannel.Target SCALE = IAnimatedModelPart::offsetScale;

        public Targets() {}
    }

    @OnlyIn(Dist.CLIENT)
    public static class Interpolations {
        public static final AnimationChannel.Interpolation LINEAR = (p_253292_, p_253293_, p_253294_, p_253295_, p_253296_, scaleX, scaleY, scaleZ) -> {
            Vector3f vector3f = p_253294_[p_253295_].target();
            Vector3f vector3f1 = p_253294_[p_253296_].target();
            return vector3f.lerp(vector3f1, p_253293_, p_253292_).mul(scaleX, scaleY, scaleZ);
        };
        public static final AnimationChannel.Interpolation CATMULLROM = (p_254076_, p_232235_, p_232236_, p_232237_, p_232238_, scaleX, scaleY, scaleZ) -> {
            Vector3f vector3f = p_232236_[Math.max(0, p_232237_ - 1)].target();
            Vector3f vector3f1 = p_232236_[p_232237_].target();
            Vector3f vector3f2 = p_232236_[p_232238_].target();
            Vector3f vector3f3 = p_232236_[Math.min(p_232236_.length - 1, p_232238_ + 1)].target();
            p_254076_.set(Mth.catmullrom(p_232235_, vector3f.x(), vector3f1.x(), vector3f2.x(), vector3f3.x()) * scaleX,
                    Mth.catmullrom(p_232235_, vector3f.y(), vector3f1.y(), vector3f2.y(), vector3f3.y()) * scaleY,
                    Mth.catmullrom(p_232235_, vector3f.z(), vector3f1.z(), vector3f2.z(), vector3f3.z()) * scaleZ);
            return p_254076_;
        };

        public Interpolations() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface Interpolation {
        Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float scaleX, float scaleY, float scaleZ);
    }

}
