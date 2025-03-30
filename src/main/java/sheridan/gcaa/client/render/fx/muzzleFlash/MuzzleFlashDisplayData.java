package sheridan.gcaa.client.render.fx.muzzleFlash;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import sheridan.gcaa.data.IJsonSyncable;

@OnlyIn(Dist.CLIENT)
public class MuzzleFlashDisplayData  implements IJsonSyncable {
    public int length = 30;
    public float[] translate = null;
    public float[] scale = new float[]{1,1,1};
    public float[] rotate = null;

    @Deprecated
    public MuzzleFlashDisplayData setDefaultTranslate(float[] translate) {
        this.translate = new float[] {translate[0], translate[1], translate[2]};
        return this;
    }

    public MuzzleFlashDisplayData setDefaultTranslate(float x, float y, float z) {
        this.translate = new float[] {-x / 16f, -y / 16f, z / 16f};
        return this;
    }


    public MuzzleFlashDisplayData setScale(float scale) {
        this.scale[0] = scale;
        this.scale[1] = scale;
        this.scale[2] = scale;
        return this;
    }

    public MuzzleFlashDisplayData setScale(float x, float y, float z) {
        this.scale[0] = x;
        this.scale[1] = y;
        this.scale[2] = z;
        return this;
    }

    @Deprecated
    public MuzzleFlashDisplayData setDefaultRotate(float[] rotate) {
        this.rotate = new float[] {rotate[0], rotate[1], rotate[2]};
        return this;
    }

    public MuzzleFlashDisplayData setDefaultRotate(float x, float y, float z) {
        this.rotate = new float[] {
                (float) Math.toRadians(x),
                (float) Math.toRadians(y),
                (float) Math.toRadians(z)};
        return this;
    }

    public MuzzleFlashDisplayData setLength(int length) {
        this.length = length;
        return this;
    }

    public void applyTrans(PoseStack stack, float size) {
        if (translate != null  && (translate[0] != 0 || translate[1] != 0 || translate[2] != 0)) {
            stack.translate(translate[0],
                    translate[1],
                    translate[2]);
        }
        if (rotate != null && (rotate[0] != 0 || rotate[1] != 0 || rotate[2] != 0)) {
            stack.mulPose(new Quaternionf().rotateXYZ(rotate[0], rotate[1], rotate[2]));
        }
        if (size != 1 || (scale[0] != 1 || scale[1] != 1 || scale[2] != 1)) {
            stack.scale(scale[0] * size, scale[1] * size, scale[2] * size);
        }
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty("length", length);
        if (translate != null) {
            jsonObject.addProperty("translate", translate[0] + "," + translate[1] + "," + translate[2]);
        }
        jsonObject.addProperty("scale", scale[0] + "," + scale[1] + "," + scale[2]);
        if (rotate != null) {
            jsonObject.addProperty("rotate", rotate[0] + "," + rotate[1] + "," + rotate[2]);
        }
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        length = jsonObject.get("length").getAsInt();
        if (jsonObject.has("translate")) {
            String[] translate = jsonObject.get("translate").getAsString().split(",");
            this.translate = new float[] {Float.parseFloat(translate[0]), Float.parseFloat(translate[1]), Float.parseFloat(translate[2])};
        }
        String[] scale = jsonObject.get("scale").getAsString().split(",");
        this.scale = new float[] {Float.parseFloat(scale[0].trim()), Float.parseFloat(scale[1].trim()), Float.parseFloat(scale[2].trim())};
        if (jsonObject.has("rotate")) {
            String[] rotate = jsonObject.get("rotate").getAsString().split(",");
            this.rotate = new float[] {Float.parseFloat(rotate[0].trim()), Float.parseFloat(rotate[1].trim()), Float.parseFloat(rotate[2].trim())};
        }
    }
}
