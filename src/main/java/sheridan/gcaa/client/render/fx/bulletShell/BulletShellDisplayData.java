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
public class BulletShellDisplayData{
    private static final float SPEED_RATE = 5.5f;
    public float[] pos = new float[] {0, 0, 0};
    public float[] scale = new float[] {1, 1, 1};
    public Vector3f velocity;
    public float randomRate = 0.25f;
    public float dropRate = - 6 * SPEED_RATE;
    public float rotateSpeed = 18 * SPEED_RATE;
    public final String type;
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

}
