package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlash;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DisplayData {
    public enum DataType {
        POS, ROT, SCALE
    }
    public static final int
            FIRST_PERSON_MAIN = 0,
            THIRD_PERSON_RIGHT = 1,
            GROUND = 2,
            FIXED = 3;
    private final float[][] transforms = new float[][] {{}, {}, {}, {}, {0, 0, 0, 0, 0, 0, 1, 1, 1}};
    private final boolean[][] emptyMarks = new boolean[][] {{}, {}, {}, {}};
    private final Map<String, MuzzleFlashEntry> muzzleFlashMap = new HashMap<>();
    private InertialRecoilData inertialRecoilData;
    public DisplayData() {}

    public void applyTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case FIRST_PERSON_RIGHT_HAND -> applyTransform(transforms[0], emptyMarks[0], poseStack);
            case THIRD_PERSON_RIGHT_HAND -> applyTransform(transforms[1], emptyMarks[1], poseStack);
            case GROUND -> applyTransform(transforms[2], emptyMarks[2], poseStack);
            case FIXED -> applyTransform(transforms[3], emptyMarks[3], poseStack);
        }
    }


    void applyTransform(float[] transform, boolean[] mark, PoseStack poseStack) {
        if (mark.length == 0 || transform.length == 0) {return;}
        if (mark[0]) {poseStack.translate(transform[0], transform[1], transform[2]);}
        if (mark[1]) {poseStack.mulPose(new Quaternionf().rotateXYZ(transform[3], transform[4], transform[5]));}
        if (mark[2]) {poseStack.scale(transform[6], transform[7], transform[8]);}
    }

    public float[] getFirstPersonMain() {
        return transforms[0];
    }

    public float[] getThirdPersonRight() {
        return transforms[1];
    }

    public float[] getGround() {
        return transforms[3];
    }

    public float[] getFrame() {
        return transforms[4];
    }

    public float[] get(int index) {
        if (index < 0 || index > 4) {
            return null;
        }
        return transforms[index];
    }

    public float[][] copy() {
        return new float[][] {
                Arrays.copyOf(transforms[0], transforms[0].length),
                Arrays.copyOf(transforms[1], transforms[1].length),
                Arrays.copyOf(transforms[2], transforms[2].length),
                Arrays.copyOf(transforms[3], transforms[3].length),
        };
    }

    public void set(int i, int j, float val) {
        transforms[i][j] = val;
    }

    public float get(int i, int j) {
        return transforms[i][j];
    }

    public DisplayData setInertialRecoilData(InertialRecoilData inertialRecoilData) {
        this.inertialRecoilData = inertialRecoilData;
        return this;
    }

    public InertialRecoilData getInertialRecoilData() {
        return this.inertialRecoilData;
    }

    public DisplayData set(int index, float val, DataType type) {
        checkAndSet(index, val, val, val, type);
        return this;
    }

    public DisplayData set(int index, float x, float y, float z, DataType type) {
        checkAndSet(index, x, y, z, type);
        return this;
    }

    public DisplayData set(int index, float[] transform) {
        int len = Math.min(transforms[index].length, transform.length);
        System.arraycopy(transform, 0, transforms[index], 0, len);
        return this;
    }

    public DisplayData setFirstPersonMain(float x, float y, float z, DataType type) {
        checkAndSet(0, x, y, z, type);
        return this;
    }

    public DisplayData setThirdPersonRight(float x, float y, float z, DataType type) {
        checkAndSet(1, x, y, z, type);
        return this;
    }

    public DisplayData setGround(float x, float y, float z, DataType type) {
        checkAndSet(2, x, y, z, type);
        return this;
    }

    public DisplayData setFrame(float x, float y, float z, DataType type) {
        checkAndSet(3, x, y, z, type);
        return this;
    }

    public DisplayData setAttachmentScreen(float x, float y, float z, float rx, float ry, float rz, float sx, float sy, float sz) {
        transforms[4] = new float[] {x / 16f, y / 16f, z / 16f,
                (float) Math.toRadians(rx), (float) Math.toRadians(ry), (float) Math.toRadians(rz),
                sx, sy, sz};
        return this;
    }

    public Map<String, MuzzleFlashEntry> getMuzzleFlashMap() {
        return muzzleFlashMap;
    }

    public MuzzleFlash getMuzzleFlash(String status) {
        return muzzleFlashMap.get(status).muzzleFlash;
    }
    public MuzzleFlashEntry getMuzzleFlashEntry(String status) {
        return muzzleFlashMap.get(status);
    }

    public DisplayData addMuzzleFlash(String statusName, MuzzleFlash muzzleFlash, MuzzleFlashDisplayData data) {
        muzzleFlashMap.put(statusName, new MuzzleFlashEntry(data, muzzleFlash));
        return this;
    }

    protected void checkAndSet(int index, float x, float y, float z, DataType type) {
        emptyMarks[index] = emptyMarks[index].length == 0 ? new boolean[] {false, false, false} : emptyMarks[index];
        transforms[index] = transforms[index].length == 0 ? new float[] {0, 0, 0, 0, 0, 0, 1, 1, 1} : transforms[index];
        setData(transforms[index], x, y, z, type, emptyMarks[index]);
    }

    void setData(float[] transform, float x, float y, float z, DataType type, boolean[] mark) {
        switch (type) {
            case POS -> {setPos(transform, x, y, z); mark[0] = true;}
            case ROT -> {setRot(transform, x, y, z); mark[1] = true;}
            case SCALE -> {setScale(transform, x, y, z); mark[2] = true;}
        }
    }

    void setPos(float[] transform, float sx, float sy, float sz) {
        transform[0] = sx / 16;
        transform[1] = sy / 16;
        transform[2] = sz / 16;
    }

    void setRot(float[] transform, float rx, float ry, float rz) {
        transform[3] = (float) Math.toRadians(rx);
        transform[4] = (float) Math.toRadians(ry);
        transform[5] = (float) Math.toRadians(rz);
    }

    void setScale(float[] transform, float sx, float sy, float sz) {
        transform[6] = sx;
        transform[7] = sy;
        transform[8] = sz;
    }

    @OnlyIn(Dist.CLIENT)
    public static class MuzzleFlashEntry {
        public MuzzleFlashDisplayData displayData;
        public MuzzleFlash muzzleFlash;

        public MuzzleFlashEntry(MuzzleFlashDisplayData displayData, MuzzleFlash muzzleFlash) {
            this.displayData = displayData;
            this.muzzleFlash = muzzleFlash;
        }
    }
}
