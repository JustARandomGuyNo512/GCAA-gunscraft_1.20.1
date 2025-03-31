package sheridan.gcaa.client.render.fx.bulletShell;


import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sheridan.gcaa.client.model.BulletShellModel;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.data.IJsonSyncable;
import sheridan.gcaa.utils.RenderAndMathUtils;

@OnlyIn(Dist.CLIENT)
public class BulletShellDisplayData implements IJsonSyncable {
    public static final float SPEED_RATE = 5.5f;
    public float[] pos = new float[] {0, 0, 0};
    public float[] scale = new float[] {1, 1, 1};
    public Vector3f velocity;
    public float randomRate = 0.25f;
    public float dropRate = - 6 * SPEED_RATE;
    public float rotateSpeed = 18 * SPEED_RATE;
    public String type;
    public int maxDisplayTime = 1000;

    public BulletShellDisplayData(float x, float y, float z, Vector3f velocity, String type) {
        pos[0] = - x / 16;
        pos[1] = - y / 16;
        pos[2] = z / 16;
        this.velocity = new Vector3f(
                -velocity.x * SPEED_RATE,
                -velocity.y * SPEED_RATE,
                -velocity.z * SPEED_RATE);
        this.type = type;
    }

    public BulletShellDisplayData() {}

    public BulletShellDisplayData setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
        return this;
    }

    public BulletShellDisplayData setScale(float scale) {
        return setScale(scale, scale, scale);
    }

    public BulletShellDisplayData setRandomRate(float rate) {
        randomRate = rate;
        return this;
    }

    public BulletShellDisplayData setDropRate(float rate) {
        dropRate = - rate * SPEED_RATE;
        return this;
    }

    public BulletShellDisplayData setRotateSpeed(float speed) {
        rotateSpeed = (float) Math.toRadians(speed * SPEED_RATE);
        return this;
    }

    public BulletShellDisplayData setMaxDisplayTime(int time) {
        maxDisplayTime = time;
        return this;
    }

    public void render(long start, PoseStack poseStack, GunRenderContext context, VertexConsumer globalVertexConsumer, float[] random) {
        float timeDis = RenderAndMathUtils.secondsFromNow(start);
        poseStack.translate(
                pos[0] + velocity.x * timeDis * random[0],
                pos[1] + (velocity.y * random[1] - dropRate * Math.pow(timeDis, 2)) * timeDis,
                pos[2] + velocity.z * timeDis * random[2]);
        if (rotateSpeed != 0) {
            poseStack.mulPose(new Quaternionf().rotateY(rotateSpeed * timeDis * random[3]));
        }
        if (scale[0] != 1 || scale[1] != 1 || scale[2] != 1) {
            poseStack.scale(scale[0], scale[1], scale[2]);
        }
        BulletShellModel.render(type, poseStack, globalVertexConsumer, context.packedLight, context.packedOverlay);
    }

    @Override
    public void writeData(JsonObject jsonObject) {
        jsonObject.addProperty("pos", -pos[0] * 16 + ", " + pos[1] * -16 + ", " + pos[2] * 16);
        jsonObject.addProperty("velocity", velocity.x / -SPEED_RATE + ", " + velocity.y / -SPEED_RATE + ", " + velocity.z / -SPEED_RATE);
        jsonObject.addProperty("scale", scale[0] + ", " + scale[1] + ", " + scale[2]);
        jsonObject.addProperty("randomRate", randomRate);
        jsonObject.addProperty("dropRate", dropRate / -SPEED_RATE);
        jsonObject.addProperty("rotateSpeed", (float) Math.toDegrees(rotateSpeed) / SPEED_RATE);
        jsonObject.addProperty("maxDisplayTime", maxDisplayTime);
        jsonObject.addProperty("type", type);
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        String[] posStr = jsonObject.get("pos").getAsString().split(",");
        float[] pos = new float[] {
                -Float.parseFloat(posStr[0].trim()) / 16,
                -Float.parseFloat(posStr[1].trim()) / 16,
                Float.parseFloat(posStr[2].trim()) / 16};
        this.pos[0] = pos[0];
        this.pos[1] = pos[1];
        this.pos[2] = pos[2];

        String[] velocityStr = jsonObject.get("velocity").getAsString().split(",");
        float[] velocity = new float[] {
                -Float.parseFloat(velocityStr[0].trim()) * SPEED_RATE,
                -Float.parseFloat(velocityStr[1].trim()) * SPEED_RATE,
                -Float.parseFloat(velocityStr[2].trim()) * SPEED_RATE};
        this.velocity = new Vector3f(velocity[0], velocity[1], velocity[2]);

        String[] scaleStr = jsonObject.get("scale").getAsString().split(",");
        float[] scale = new float[] {
                Float.parseFloat(scaleStr[0].trim()), Float.parseFloat(scaleStr[1].trim()), Float.parseFloat(scaleStr[2].trim())};
        this.scale[0] = scale[0];
        this.scale[1] = scale[1];
        this.scale[2] = scale[2];

        setRandomRate(jsonObject.get("randomRate").getAsFloat());
        setDropRate(jsonObject.get("dropRate").getAsFloat());
        setRotateSpeed(jsonObject.get("rotateSpeed").getAsFloat());
        setMaxDisplayTime(jsonObject.get("maxDisplayTime").getAsInt());
        this.type = jsonObject.get("type").getAsString();
    }
}
