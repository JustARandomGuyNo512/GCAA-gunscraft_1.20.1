package sheridan.gcaa.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.ClientWeaponStatus;
import sheridan.gcaa.client.animation.recoilAnimation.InertialRecoilData;
import sheridan.gcaa.client.model.BulletShellModel;
import sheridan.gcaa.client.render.fx.bulletShell.BulletShellDisplayData;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlash;
import sheridan.gcaa.client.render.fx.muzzleFlash.MuzzleFlashDisplayData;
import sheridan.gcaa.data.IJsonSyncable;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DisplayData implements IJsonSyncable {

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
            ATTACHMENT_SCREEN = 6,
            SPRINTING = 7;
    private final float[][] transforms = new float[][]{
            {0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1, 1}};
    private final boolean[][] emptyMarks = new boolean[][]{
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}};
    private final Map<String, MuzzleFlashEntry> muzzleFlashMap = new HashMap<>();
    private InertialRecoilData inertialRecoilData;
    private BulletShellDisplayData bulletShellDisplayData;

    public DisplayData() {}


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
        ClientWeaponStatus status = Clients.MAIN_HAND_STATUS;
        float particleTick = Minecraft.getInstance().getPartialTick();
        float progress = status.getLerpAdsProgress(particleTick);
        float[] sightAimPos = Clients.MAIN_HAND_STATUS.getSightAimPos(particleTick);
        if (progress != 0) {
            float lerpProgress = RenderAndMathUtils.sLerp(progress);
            float yLerp = Clients.isInAds() ? lerpProgress * lerpProgress : lerpProgress;

            if (emptyMarks[0][1] ||  emptyMarks[5][1] || sightAimPos != null) {
                float rx = Mth.lerp(lerpProgress, transforms[0][3], transforms[5][3]);
                float ry = Mth.lerp(lerpProgress, transforms[0][4], transforms[5][4]);
                float rz = Mth.lerp(lerpProgress, transforms[0][5], (sightAimPos == null ? transforms[5][5] : - sightAimPos[2]));
                poseStack.mulPose(new Quaternionf().rotateXYZ(rx, ry, rz));
            }

            if (emptyMarks[0][0] || emptyMarks[5][0]) {
                float x = Mth.lerp(lerpProgress, transforms[0][0], (sightAimPos == null ? transforms[5][0] : - sightAimPos[0]));
                float y = Mth.lerp(yLerp, transforms[0][1], (sightAimPos == null ? transforms[5][1] : - sightAimPos[1]));
                float z = getMinDisZ(lerpProgress);
                poseStack.translate(x, y, z);
            }

        } else {
            if (emptyMarks[0][0]) {
                poseStack.translate(transforms[0][0], transforms[0][1], transforms[0][2]);
            }
            if (emptyMarks[0][1]) {
                poseStack.mulPose(new Quaternionf().rotateXYZ(transforms[0][3], transforms[0][4], transforms[0][5]));
            }
        }
        //boolean modifyZ = !Float.isNaN(Clients.gunModelFovModify);
        System.out.println(getGunModelFovRatio(particleTick));
        if (emptyMarks[0][2]) {
            poseStack.scale(transforms[0][6], transforms[0][7], transforms[0][8] * getGunModelFovRatio(particleTick));
        } else {
            poseStack.scale(1F, 1F, getGunModelFovRatio(particleTick));
        }
        if (Clients.isInSprintingTransAdjust) {
            poseStack.translate(transforms[7][0], transforms[7][1], transforms[7][2]);
            poseStack.mulPose(new Quaternionf().rotateXYZ(transforms[7][3], transforms[7][4], transforms[7][5]));
        }
    }

    protected float getMinDisZ(float progress) {
        float minZ = lerpMinZ(transforms[AIMING][2]);
        return (float) Mth.lerp(Math.pow(progress, 3), transforms[0][2], minZ);
    }

    static float lastGunModelFovRatio = 1;
    protected float getGunModelFovRatio(float particleTick) {
        if (Float.isNaN(Clients.gunModelFovModify)) {
            if (Clients.isInAds()) {
                float lerpedSwitchingSightProgress = Clients.MAIN_HAND_STATUS.getLerpedSwitchingSightProgress(particleTick);
                return (float) Mth.lerp(Math.sqrt(lerpedSwitchingSightProgress), lastGunModelFovRatio, 1);
            }
            return 1;
        } else {
            lastGunModelFovRatio = (float) (Math.tan(Math.toRadians(Clients.gunModelFovModify / 2f)) / 0.7002075382097097f);
            return lastGunModelFovRatio;
        }
    }

    protected float lerpMinZ(float defaultVal) {
        if (Float.isNaN(Clients.weaponAdsZMinDistance)
                //|| Float.isNaN(Clients.gunModelFovModify)
        ) {
            return defaultVal;
        }
        return - Clients.weaponAdsZMinDistance;
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
                Arrays.copyOf(transforms[7], transforms[7].length),
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

    public DisplayData setSprintingTrans(float x, float y, float z, float rx, float ry, float rz) {
        emptyMarks[SPRINTING] = emptyMarks[SPRINTING].length == 0 ? new boolean[] {false, false, false} : emptyMarks[SPRINTING];
        setData(transforms[SPRINTING], x, y, z, DataType.POS, emptyMarks[SPRINTING]);
        setData(transforms[SPRINTING], rx, ry, rz, DataType.ROT, emptyMarks[SPRINTING]);
        return this;
    }

    public DisplayData usePistolDefaultSprintingTrans() {
        return setSprintingTrans(2.5f, 8f, 1, 15, -6, 9);
    }

    public float[] getSprintingTrans() {
        return transforms[7];
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

    @Override
    public void writeData(JsonObject jsonObject) {
        JsonObject translation = new JsonObject();
        for (int i = 0; i < transforms.length; i ++) {
            JsonObject transData = new JsonObject();
            float[] transform = transforms[i];
            boolean[] emptyMark = emptyMarks[i];
            String trans = transform[0] + ", " + transform[1] + ", " + transform[2] + ", " + transform[3] + ", " + transform[4] + ", " + transform[5] + ", " + transform[6] + ", " + transform[7] + ", " + transform[8];
            String marks = emptyMark[0] + ", " + emptyMark[1] + ", " + emptyMark[2];
            transData.addProperty("transform", trans);
            transData.addProperty("emptyMark", marks);
            translation.add("" + i, transData);
        }
        jsonObject.add("translation", translation);
        JsonObject muzzleFlashes = new JsonObject();
        for (Map.Entry<String, MuzzleFlashEntry> entry : muzzleFlashMap.entrySet()) {
            String status = entry.getKey();
            JsonObject muzzleFlash = new JsonObject();
            MuzzleFlashEntry value = entry.getValue();
            muzzleFlash.addProperty("name", value.muzzleFlash.name);
            JsonObject display = new JsonObject();
            value.displayData.writeData(display);
            muzzleFlash.add("display", display);
            muzzleFlashes.add(status, muzzleFlash);
        }
        jsonObject.add("muzzleFlashes", muzzleFlashes);
        if (this.bulletShellDisplayData != null) {
            JsonObject bulletShell = new JsonObject();
            this.bulletShellDisplayData.writeData(bulletShell);
            jsonObject.add("bulletShell", bulletShell);
        }
        if (this.inertialRecoilData != null) {
            JsonObject recoilData = new JsonObject();
            this.inertialRecoilData.writeData(recoilData);
            jsonObject.add("recoilData", recoilData);
        }
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        JsonObject translation = jsonObject.get("translation").getAsJsonObject();
        for (int i = 0; i < transforms.length; i ++) {
            if (!translation.has("" + i)) {
                continue;
            }
            JsonObject transData = translation.get("" + i).getAsJsonObject();
            String[] transStr = transData.get("transform").getAsString().split(",");
            String[] marksStr = transData.get("emptyMark").getAsString().split(",");
            float[] transform = new float[]{
                    Float.parseFloat(transStr[0].trim()), Float.parseFloat(transStr[1].trim()), Float.parseFloat(transStr[2].trim()),
                    Float.parseFloat(transStr[3].trim()), Float.parseFloat(transStr[4].trim()), Float.parseFloat(transStr[5].trim()),
                    Float.parseFloat(transStr[6].trim()), Float.parseFloat(transStr[7].trim()), Float.parseFloat(transStr[8].trim())};
            boolean[] emptyMark = new boolean[] {
                    Boolean.parseBoolean(marksStr[0].trim()),
                    Boolean.parseBoolean(marksStr[1].trim()),
                    Boolean.parseBoolean(marksStr[2].trim())};
            transforms[i] = transform;
            emptyMarks[i] = emptyMark;
        }
        JsonObject MuzzleFlashes = jsonObject.get("muzzleFlashes").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : MuzzleFlashes.entrySet()) {
            String status = entry.getKey();
            JsonObject muzzleFlash = entry.getValue().getAsJsonObject();
            String name = muzzleFlash.get("name").getAsString();
            MuzzleFlash muzzleFlashObject = MuzzleFlash.get(name);
            JsonObject display = muzzleFlash.get("display").getAsJsonObject();
            MuzzleFlashDisplayData muzzleFlashDisplayData = new MuzzleFlashDisplayData();
            muzzleFlashDisplayData.loadData(display);
            muzzleFlashMap.put(status, new MuzzleFlashEntry(muzzleFlashDisplayData, muzzleFlashObject));
        }
        if (jsonObject.has("bulletShell")) {
            JsonObject bulletShell = jsonObject.get("bulletShell").getAsJsonObject();
            if (this.bulletShellDisplayData == null) {
                this.bulletShellDisplayData = new BulletShellDisplayData();
            }
            this.bulletShellDisplayData.loadData(bulletShell);
        }
        if (jsonObject.has("recoilData")) {
            JsonObject recoilData = jsonObject.get("recoilData").getAsJsonObject();
            if (this.inertialRecoilData == null) {
                this.inertialRecoilData = new InertialRecoilData();
            }
            this.inertialRecoilData.loadData(recoilData);
        }
    }
}
