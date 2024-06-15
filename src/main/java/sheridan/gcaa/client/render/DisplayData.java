package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class DisplayData {
    public enum DataType {
        POS, ROT, SCALE
    }
    public enum HandPos {
        MAIN_HAND_RIFLE, DOUBLE_PISTOL, RIGHT_PISTOL, LEFT_PISTOL
    }
    public static final int FIRST_PERSON_MAIN = 0, FIRST_PERSON_RIGHT = 1, FIRST_PERSON_LEFT = 2, THIRD_PERSON_RIGHT = 3, THIRD_PERSON_LEFT = 4, GROUND = 5, FIXED = 6;
    private final float[][] transforms = new float[][] {{}, {}, {}, {}, {}, {}, {}};
    private final boolean[][] emptyMarks = new boolean[][] {{}, {}, {}, {}, {}, {}, {}};
    public DisplayData() {}

    public void applyTransform(ItemDisplayContext displayContext, PoseStack poseStack, HandPos pos) {
        switch (displayContext) {
            case FIRST_PERSON_RIGHT_HAND -> handleFirstPersonRightTrans(poseStack, pos);
            case FIRST_PERSON_LEFT_HAND -> applyTransform(transforms[2], emptyMarks[2], poseStack);
            case THIRD_PERSON_RIGHT_HAND -> applyTransform(transforms[3], emptyMarks[3], poseStack);
            case THIRD_PERSON_LEFT_HAND -> applyTransform(transforms[4], emptyMarks[4], poseStack);
            case GROUND -> applyTransform(transforms[5], emptyMarks[5], poseStack);
            case FIXED -> applyTransform(transforms[6], emptyMarks[6], poseStack);
        }
    }

    void handleFirstPersonRightTrans(PoseStack poseStack, HandPos pos) {
        if (pos == HandPos.MAIN_HAND_RIFLE) {
            applyTransform(transforms[0], emptyMarks[0], poseStack);
        } else if (pos == HandPos.DOUBLE_PISTOL) {
            applyTransform(transforms[1], emptyMarks[1], poseStack);
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

    public float[] getFirstPersonRight() {
        return transforms[1];
    }

    public float[] getFirstPersonLeft() {
        return transforms[2];
    }

    public float[] getThirdPersonRight() {
        return transforms[3];
    }

    public float[] getThirdPersonLeft() {
        return transforms[4];
    }

    public float[] getGround() {
        return transforms[5];
    }

    public float[] getFrame() {
        return transforms[6];
    }

    public float[] get(int index) {
        if (index < 0 || index > 6) {
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
                Arrays.copyOf(transforms[4], transforms[4].length),
                Arrays.copyOf(transforms[5], transforms[5].length),
                Arrays.copyOf(transforms[6], transforms[6].length),
        };
    }

    public boolean[][] getEmptyMarks() {
        return emptyMarks;
    }

    public boolean[] emptyMarksOf(int index) {
        return emptyMarks[index];
    }

    public boolean isEmpty(int index) {
        return emptyMarks[index].length == 0;
    }

    public void set(int i, int j, float val) {
        transforms[i][j] = val;
    }

    public float get(int i, int j) {
        return transforms[i][j];
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

    public DisplayData setFirstPersonRight(float x, float y, float z, DataType type) {
        checkAndSet(1, x, y, z, type);
        return this;
    }

    public DisplayData setFirstPersonLeft(float x, float y, float z, DataType type) {
        checkAndSet(2, x, y, z, type);
        return this;
    }

    public DisplayData setThirdPersonRight(float x, float y, float z, DataType type) {
        checkAndSet(3, x, y, z, type);
        return this;
    }

    public DisplayData setThirdPersonLeft(float x, float y, float z, DataType type) {
        checkAndSet(4, x, y, z, type);
        return this;
    }

    public DisplayData setGround(float x, float y, float z, DataType type) {
        checkAndSet(5, x, y, z, type);
        return this;
    }

    public DisplayData setFrame(float x, float y, float z, DataType type) {
        checkAndSet(6, x, y, z, type);
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
}
