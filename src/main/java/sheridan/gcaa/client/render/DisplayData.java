package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class DisplayData {
    public enum DataType {
        POS, ROT, SCALE
    }
    public enum HandPos {
        MAIN_HAND_RIFLE, DOUBLE_PISTOL, RIGHT_PISTOL, LEFT_PISTOL
    }
    float[] firstPersonMain;
    float[] firstPersonRight;
    float[] firstPersonLeft;
    float[] thirdPersonRight;
    float[] thirdPersonLeft;
    float[] ground;
    float[] frame;
    boolean[] FPM;
    boolean[] FPR;
    boolean[] FPL;
    boolean[] TPR;
    boolean[] TPL;
    boolean[] G;
    boolean[] F;
    public DisplayData() {}

    public void applyTransform(ItemDisplayContext displayContext, PoseStack poseStack, HandPos pos) {
        switch (displayContext) {
            case FIRST_PERSON_RIGHT_HAND -> handleFirstPersonRightTrans(poseStack, pos);
            case FIRST_PERSON_LEFT_HAND -> applyTransform(firstPersonLeft, FPL, poseStack);
            case THIRD_PERSON_RIGHT_HAND -> applyTransform(thirdPersonRight, FPR, poseStack);
            case THIRD_PERSON_LEFT_HAND -> applyTransform(thirdPersonLeft, FPL, poseStack);
            case GROUND -> applyTransform(ground, G, poseStack);
            case FIXED -> applyTransform(frame, F, poseStack);
        }
    }

    void handleFirstPersonRightTrans(PoseStack poseStack, HandPos pos) {
        if (pos == HandPos.MAIN_HAND_RIFLE) {
            applyTransform(firstPersonMain, FPM, poseStack);
        } else if (pos == HandPos.DOUBLE_PISTOL) {
            applyTransform(firstPersonRight, FPR, poseStack);
        }
    }

    void applyTransform(float[] transform, boolean[] mark, PoseStack poseStack) {
        if (mark[0]) {poseStack.translate(transform[0], transform[1], transform[2]);}
        if (mark[1]) {poseStack.mulPose(new Quaternionf().rotateXYZ(transform[3], transform[4], transform[5]));}
        if (mark[2]) {poseStack.scale(transform[6], transform[7], transform[8]);}
    }

    public DisplayData setFirstPersonMain(float x, float y, float z, DataType type) {
        setData(firstPersonMain, x, y, z, type, FPM);
        return this;
    }

    public DisplayData setFirstPersonRight(float x, float y, float z, DataType type) {
        setData(firstPersonRight, x, y, z, type, FPR);
        return this;
    }

    public DisplayData setFirstPersonLeft(float x, float y, float z, DataType type) {
        setData(firstPersonLeft, x, y, z, type, FPL);
        return this;
    }

    public DisplayData setThirdPersonRight(float x, float y, float z, DataType type) {
        setData(thirdPersonRight, x, y, z, type, TPR);
        return this;
    }

    public DisplayData setThirdPersonLeft(float x, float y, float z, DataType type) {
        setData(thirdPersonLeft, x, y, z, type, TPL);
        return this;
    }

    public DisplayData setGround(float x, float y, float z, DataType type) {
        setData(ground, x, y, z, type, G);
        return this;
    }

    public DisplayData setFrame(float x, float y, float z, DataType type) {
        setData(frame, x, y, z, type, F);
        return this;
    }

    void setData(float[] transform, float x, float y, float z, DataType type, boolean[] mark) {
        if (mark == null) {
            mark = new boolean[] {false, false, false};
        }
        if (transform == null) {
            transform = new float[] {0, 0, 0, 0, 0, 0, 1, 1, 1};
        }
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
