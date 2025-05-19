package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.gun.guns.RifleModels;
import org.lwjgl.opengl.GL30;
import sheridan.gcaa.client.render.RenderTypes;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glVertexAttribI2i;

import java.lang.Math;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class BufferedModelBone implements IAnimatedModelPart{
    private static int lastLightMapUV;
    public static boolean lastShaderEnabled = false;
    public RenderType renderType;
    public BufferBuilder buffer;
    public BufferBuilder.RenderedBuffer renderedBuffer;
    public VertexBuffer vertexBuffer;
    public ResourceLocation texture;
    public MultiBufferSource.BufferSource bufferSource;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    private final Map<String, BufferedModelBone> children;
    private PartPose initialPose = PartPose.ZERO;
    public MeshData meshData;
    public BufferedModelBone parent = null;

    public BufferedModelBone() {
        children = new Object2ObjectArrayMap<>();
    }

    public BufferedModelBone setInitialPose(PartPose initialPose) {
        this.initialPose = initialPose;
        return this;
    }

    public PartPose getInitialPose() {
        return this.initialPose;
    }

    public void resetPose() {
        x = initialPose.x;
        y = initialPose.y;
        z = initialPose.z;
        xRot = initialPose.xRot;
        yRot = initialPose.yRot;
        zRot = initialPose.zRot;
        xScale = 1.0F;
        yScale = 1.0F;
        zScale = 1.0F;
    }

    private void compileVertexBuffer(PoseStack.Pose pose) {
        if (meshData == null) {
            return;
        }
        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer = new BufferBuilder(256 * 256);
        bufferSource = MultiBufferSource.immediate(buffer);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        meshData.compile(pose, vertexConsumer);
        renderedBuffer = buffer.end();
        vertexBuffer.bind();
        vertexBuffer.upload(renderedBuffer);
        VertexBuffer.unbind();
    }

    public void compile(RenderType renderType) {
        this.renderType = renderType;
        PoseStack poseStack = new PoseStack();
        compileVertexBuffer(poseStack.last());
        for (BufferedModelBone child : children.values()) {
            child.compile(renderType);
        }
    }

    public Map<String, BufferedModelBone> getChildren() {
        return children;
    }

    public void render(int light) {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        translateAndRotate(modelViewStack);
        RenderSystem.applyModelViewMatrix();
        ShaderInstance shader = GameRenderer.getRendertypeEntityCutoutShader();
        if (vertexBuffer != null && shader != null) {
            renderType.setupRenderState();
            vertexBuffer.bind();
            if (Clients.IS_IRIS_SHADER_ENABLED) {
                int u = light & '\uffff', v = light >> 16 & '\uffff';
                glDisableVertexAttribArray(4);
                glVertexAttribI2i(4, u, v);
                vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shader);
                glEnableVertexAttribArray(4);
            } else if (Clients.IS_OPTIFINE_SHADER_ENABLED) {

            } else {
                float[] shaderColor = RenderSystem.getShaderColor();
                float r = shaderColor[0];
                float g = shaderColor[1];
                float b = shaderColor[2];
                float a = shaderColor[3];
                applyLightmapBrightness(light);
                vertexBuffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shader);
                VertexBuffer.unbind();
                RenderSystem.setShaderColor(r, g, b, a);
            }
            VertexBuffer.unbind();
            renderType.clearRenderState();
        }
        for (BufferedModelBone child : children.values()) {
            child.render(light);
        }
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

//IRIS:
//    Attribute #0: iris_Color (location=1, size=1, type=vec4)
//    Attribute #1: iris_Entity (location=7, size=1, type=Unknown(35668))
//    Attribute #2: iris_Normal (location=5, size=1, type=vec3)
//    Attribute #3: iris_Position (location=0, size=1, type=vec3)
//    Attribute #4: iris_UV0 (location=2, size=1, type=vec2)
//    Attribute #5: iris_UV1 (location=3, size=1, type=Unknown(35667))
//    Attribute #6: iris_UV2 (location=4, size=1, type=Unknown(35667))


    public static void printAllAttributes(int shaderProgram) {
        // 获取活动attribute数量
        int numAttributes = glGetProgrami(shaderProgram, GL_ACTIVE_ATTRIBUTES);

        System.out.println("Active Attributes: " + numAttributes);

        // 准备缓冲区
        IntBuffer sizeBuf = BufferUtils.createIntBuffer(1);
        IntBuffer typeBuf = BufferUtils.createIntBuffer(1);

        for (int i = 0; i < numAttributes; i++) {
            // 获取attribute信息
            String attrName = glGetActiveAttrib(shaderProgram, i, sizeBuf, typeBuf);
            int attrSize = sizeBuf.get(0);
            int attrType = typeBuf.get(0);

            // 获取attribute的位置(slot)
            int location = glGetAttribLocation(shaderProgram, attrName);

            System.out.printf("Attribute #%d: %s (location=%d, size=%d, type=%s)%n",
                    i, attrName, location, attrSize, getTypeName(attrType));
        }
    }

    private static String getTypeName(int type) {
        switch (type) {
            case GL_FLOAT: return "float";
            case GL_FLOAT_VEC2: return "vec2";
            case GL_FLOAT_VEC3: return "vec3";
            case GL_FLOAT_VEC4: return "vec4";
            case GL_FLOAT_MAT2: return "mat2";
            case GL_FLOAT_MAT3: return "mat3";
            case GL_FLOAT_MAT4: return "mat4";
            default: return "Unknown(" + type + ")";
        }
    }

    public void updateBufferLightmapUV(int lightmap) {

    }

    public static void applyLightmapBrightness(int packedLight) {
        Vector3f init = LightmapColorHelper.init(packedLight);
        RenderSystem.setShaderColor(init.x, init.y, init.z, 1.0f);
    }

    static long lastRender;
    static float timer;
    public static BufferedModelBone TEST;
    public static void __test_render__(int light) {
        if (TEST != null) {
            if (lastShaderEnabled != Clients.IS_SHADER_ENABLED) {
                TEST.compile(RenderTypes.getMeshCutOut(new ResourceLocation(GCAA.MODID, "model_assets/test_gltf/m1a2.png")));
            }
            lastShaderEnabled = Clients.IS_SHADER_ENABLED;
            if (lastRender == 0) {
                lastRender = System.currentTimeMillis();
                return;
            }
            float timeDis = (System.currentTimeMillis() - lastRender) / 50f;
            lastRender = System.currentTimeMillis();
            timer += timeDis;
            TEST.z -= 24 / 16f;
            TEST.y -= 6f / 16f;
            TEST.yRot = (float) Math.toRadians(timer % 360);
            IAnimatedModelPart turret = TEST.getChild("visual").getChild("turret");
            ((BufferedModelBone) turret).yRot = (float) Math.toRadians((timer * 2) % 360);
            IAnimatedModelPart power_wheel_l = TEST.getChild("visual").getChild("body").getChild("wheels_l").getChild("power_wheel_l");
            ((BufferedModelBone) power_wheel_l).xRot = (float) Math.toRadians((timer * 5) % 360);
            IAnimatedModelPart power_wheel_r = TEST.getChild("visual").getChild("body").getChild("wheels_r").getChild("power_wheel_r");
            ((BufferedModelBone) power_wheel_r).xRot = (float) Math.toRadians((-timer * 5) % 360);

            IAnimatedModelPart cannon = TEST.getChild("visual").getChild("turret").getChild("cannon");
            ((BufferedModelBone) cannon).xRot = (float) Math.toRadians(10);

            TEST.xScale = 0.2f;
            TEST.yScale = 0.2f;
            TEST.zScale = 0.2f;
            TEST.render(light);
            TEST.resetPose();
        }
    }


    @Override
    public Stream<IAnimatedModelPart> getAllParts() {
        return Stream.concat(Stream.of(this), this.children.values().stream().flatMap(BufferedModelBone::getAllParts));
    }

    @Override
    public boolean hasChild(String pName) {
        return children.containsKey(pName);
    }

    @Override
    public IAnimatedModelPart getChild(String pName) {
        return children.get(pName);
    }

    @Override
    public void offsetPos(Vector3f vector3f) {
        this.x += vector3f.x();
        this.y += vector3f.y();
        this.z += vector3f.z();
    }

    @Override
    public void offsetRotation(Vector3f vector3f) {
        this.xRot += vector3f.x();
        this.yRot += vector3f.y();
        this.zRot += vector3f.z();
    }

    @Override
    public void offsetScale(Vector3f vector3f) {
        this.xScale += vector3f.x();
        this.yScale += vector3f.y();
        this.zScale += vector3f.z();
    }

    public void addChild(String name, BufferedModelBone bone) {
        this.children.put(name, bone);
        bone.parent = this;
    }

    public void translateAndRotate(PoseStack pPoseStack) {
        pPoseStack.translate(this.x , this.y, this.z);
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
            pPoseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
        }
        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            pPoseStack.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    public void print() {
        System.out.println(this);
        if (meshData != null) {
//            for (Vert vert : meshData.vertices) {
//                System.out.println(vert);
//            }
            System.out.println("vertex count: " + meshData.vertices.size());
        }
        for (Map.Entry<String, BufferedModelBone> partEntry : children.entrySet()) {
            System.out.print(partEntry.getKey());
            partEntry.getValue().print();
        }
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", xRot=" + xRot +
                ", yRot=" + yRot +
                ", zRot=" + zRot +
                ", xScale=" + xScale +
                ", yScale=" + yScale +
                ", zScale=" + zScale +
                '}';
    }

    public BufferedModelBone getParent() {
        return parent;
    }

    public static class MeshData {
        public List<Vert> vertices = new ArrayList<>();
        private final Vector3f normalDest = new Vector3f();

        public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer) {
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            for (Vert vert : vertices) {
                matrix3f.transform(vert.normalX, vert.normalY, vert.normalZ, normalDest);
                vertexConsumer.vertex(
                        matrix4f.m00() * vert.x + matrix4f.m10() * vert.y + matrix4f.m20() * vert.z + matrix4f.m30(),
                        matrix4f.m01() * vert.x + matrix4f.m11() * vert.y + matrix4f.m21() * vert.z + matrix4f.m31(),
                        matrix4f.m02() * vert.x + matrix4f.m12() * vert.y + matrix4f.m22() * vert.z + matrix4f.m32(),
                        1, 1, 1, 1,
                        vert.u, vert.v, OverlayTexture.NO_OVERLAY, LightTexture.pack(15,15),
                        normalDest.x, normalDest.y, normalDest.z);
            }
        }

        public void pushVertex(float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int index) {
            vertices.add(new Vert(x, y, z, u, v, normalX, normalY, normalZ, index));
        }

        public void pushVertex(Vert vert) {
            vertices.add(vert);
        }
    }

    public static class Vert {
        public float x, y, z, u, v, normalX, normalY, normalZ;
        public int index;
        public Vert(float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int index) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
            this.normalX = normalX;
            this.normalY = normalY;
            this.normalZ = normalZ;
            this.index = index;
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + ", " + z + ", " + u + ", " + v + ", " + normalX + ", " + normalY + ", " + normalZ + "] index: " + index;
        }
    }

}
