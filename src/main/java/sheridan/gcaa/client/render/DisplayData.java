package sheridan.gcaa.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellDisplayData;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlash;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.utils.RenderAndMathUtils;

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
            FRAME = 3,
            GUI = 4,
            AIMING = 5,
            ATTACHMENT_SCREEN = 6;
    private final float[][] transforms = new float[][]{{0, 0, 0, 0, 0, 0, 1, 1, 1}, {}, {}, {}, {}, {0, 0, 0, 0, 0, 0, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 1, 1, 1}};
    private final boolean[][] emptyMarks = new boolean[][]{{}, {}, {}, {}, {}, {}, {}};
    private final Map<String, MuzzleFlashEntry> muzzleFlashMap = new HashMap<>();
    private InertialRecoilData inertialRecoilData;
    private BulletShellDisplayData bulletShellDisplayData;

    public DisplayData() {
    }

    public void applyTransform(ItemDisplayContext displayContext, PoseStack poseStack) {
        switch (displayContext) {
            case FIRST_PERSON_RIGHT_HAND -> applyTransformFirstPerson(poseStack);
            case THIRD_PERSON_RIGHT_HAND -> applyTransform(transforms[1], emptyMarks[1], poseStack);
            case GROUND -> applyTransform(transforms[2], emptyMarks[2], poseStack);
            case FIXED -> applyTransform(transforms[3], emptyMarks[3], poseStack);
            case GUI -> applyTransform(transforms[4], emptyMarks[4], poseStack);
        }
    }

    public void applyAttachmentScreenTransform(PoseStack poseStack, float x, float y, float rx, float ry, float scale) {
        poseStack.translate(transforms[6][0] + x, transforms[6][1] + y, transforms[6][2]);
        poseStack.mulPose(new Quaternionf().rotateXYZ(transforms[6][3], (float) (transforms[6][4] + Math.toRadians(ry)), (float) (transforms[6][5] + Math.toRadians(rx))));
        poseStack.scale(transforms[6][6] * scale, transforms[6][7] * scale, transforms[6][8] * scale);
    }

    void applyTransformFirstPerson(PoseStack poseStack) {
        ClientWeaponStatus status = Clients.mainHandStatus;
        float particleTick = Minecraft.getInstance().getPartialTick();
        float progress = status.getLerpAdsProgress(particleTick);
        float[] sightAimPos = Clients.mainHandStatus.getSightAimPos(particleTick);
        if (progress != 0) {
            float lerpProgress = RenderAndMathUtils.sLerp(progress);
            float yLerp = Clients.isInAds() ? lerpProgress * lerpProgress : lerpProgress;

            if (emptyMarks[0][0] || emptyMarks[5][0]) {
                float x = Mth.lerp(lerpProgress, transforms[0][0], (sightAimPos == null ? transforms[5][0] : -sightAimPos[0]));
                float y = Mth.lerp(yLerp, transforms[0][1], (sightAimPos == null ? transforms[5][1] : -sightAimPos[1]));
                float z = Mth.lerp(lerpProgress, transforms[0][2], (Float.isNaN(Clients.weaponAdsZMinDistance) ? transforms[5][2] : -Clients.weaponAdsZMinDistance));
                poseStack.translate(x, y, z);
            }

            if (emptyMarks[0][1] || emptyMarks[5][1]) {
                float rx = Mth.lerp(lerpProgress, transforms[0][3], transforms[5][3]);
                float ry = Mth.lerp(lerpProgress, transforms[0][4], transforms[5][4]);
                float rz = Mth.lerp(lerpProgress, transforms[0][5], (sightAimPos == null ? transforms[5][5] : -sightAimPos[2]));
                poseStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, rz));
            }

        } else {
            if (emptyMarks[0][0]) {
                poseStack.translate(transforms[0][0], transforms[0][1], transforms[0][2]);
            }
            if (emptyMarks[0][1]) {
                poseStack.mulPose(new Quaternionf().rotateXYZ(transforms[0][3], transforms[0][4], transforms[0][5]));
            }
        }
        if (emptyMarks[0][2]) {
            poseStack.scale(transforms[0][6], transforms[0][7], transforms[0][8]);
        }
    }

    void applyTransform(float[] transform, boolean[] mark, PoseStack poseStack) {
        if (mark.length == 0 || transform.length == 0) {
            return;
        }
        if (mark[0]) {
            poseStack.translate(transform[0], transform[1], transform[2]);
        }
        if (mark[1]) {
            poseStack.mulPose(new Quaternionf().rotateXYZ(transform[3], transform[4], transform[5]));
        }
        if (mark[2]) {
            poseStack.scale(transform[6], transform[7], transform[8]);
        }
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
        if (index < 0 || index > transforms.length - 1) {
            return null;
        }
        return transforms[index];
    }

    public float[][] copy() {
        return new float[][]{
                Arrays.copyOf(transforms[0], transforms[0].length),
                Arrays.copyOf(transforms[1], transforms[1].length),
                Arrays.copyOf(transforms[2], transforms[2].length),
                Arrays.copyOf(transforms[3], transforms[3].length),
                Arrays.copyOf(transforms[4], transforms[4].length),
                Arrays.copyOf(transforms[5], transforms[5].length),
                Arrays.copyOf(transforms[6], transforms[6].length),
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

    public BulletShellDisplayData getBulletShellDisplayData() {
        return bulletShellDisplayData;
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
        checkAndSet(FIRST_PERSON_MAIN, x, y, z, type);
        return this;
    }

    public DisplayData setThirdPersonRight(float x, float y, float z, DataType type) {
        checkAndSet(THIRD_PERSON_RIGHT, x, y, z, type);
        return this;
    }

    public DisplayData setBulletShellDisplayData(BulletShellDisplayData bulletShellDisplayData) {
        this.bulletShellDisplayData = bulletShellDisplayData;
        return this;
    }

    public DisplayData setGround(float x, float y, float z, DataType type) {
        checkAndSet(GROUND, x, y, z, type);
        return this;
    }

    public DisplayData setFrame(float x, float y, float z, DataType type) {
        checkAndSet(FRAME, x, y, z, type);
        return this;
    }

    public DisplayData setGUI(float x, float y, float z, DataType type) {
        checkAndSet(GUI, x, y, z, type);
        return this;
    }

    public DisplayData setAds(float x, float y, float z, DataType type) {
        checkAndSet(AIMING, x, y, z, type);
        return this;
    }

    public DisplayData setAttachmentScreen(float x, float y, float z, DataType type) {
        checkAndSet(ATTACHMENT_SCREEN, x, y, z, type);
        return this;
    }

    public DisplayData setAttachmentScreen(float x, float y, float z, float rx, float ry, float rz, float sx, float sy, float sz) {
        checkAndSet(ATTACHMENT_SCREEN, x, y, z, DataType.POS);
        checkAndSet(ATTACHMENT_SCREEN, rx, ry, rz, DataType.ROT);
        checkAndSet(ATTACHMENT_SCREEN, sx, sy, sz, DataType.SCALE);
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
        emptyMarks[index] = emptyMarks[index].length == 0 ? new boolean[]{false, false, false} : emptyMarks[index];
        transforms[index] = transforms[index].length == 0 ? new float[]{0, 0, 0, 0, 0, 0, 1, 1, 1} : transforms[index];
        setData(transforms[index], x, y, z, type, emptyMarks[index]);
    }

    void setData(float[] transform, float x, float y, float z, DataType type, boolean[] mark) {
        switch (type) {
            case POS -> {
                setPos(transform, x, y, z);
                mark[0] = true;
            }
            case ROT -> {
                setRot(transform, x, y, z);
                mark[1] = true;
            }
            case SCALE -> {
                setScale(transform, x, y, z);
                mark[2] = true;
            }
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
