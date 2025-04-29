package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class MeshedModelBone {
    public static final float NORMAL_DELTA_TO_EQUIV = 1e-5f;
    public static final int VERTEX_PACK_SIZE = 5;
    public static final int NORMAL_PACK_SIZE = 3;
    public static final int NORMAL_TO_VERTEX_PACK_SIZE = 2;
    //x, y, z, u, v
    public float[] vertices;
    //x, y, z
    public float[] normals;
    //vertices start index, vertices end index
    public int[] normalToVertices;

    private final Vector3f normalDest = new Vector3f();

    public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer,
                        int light, int overlay,
                        float r, float g, float b, float a) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        int normalToVertexMark = 0;
        for (int i = 0; i < normals.length; i += NORMAL_PACK_SIZE) {
            matrix3f.transform(normals[i], normals[i + 1], normals[i + 2], normalDest);
            int vertexStart = normalToVertices[normalToVertexMark];
            int vertexEnd = normalToVertices[normalToVertexMark + 1];
            normalToVertexMark += NORMAL_TO_VERTEX_PACK_SIZE;
            for (int j = vertexStart; j < vertexEnd; j += VERTEX_PACK_SIZE) {
                float x = vertices[j];
                float y = vertices[j + 1];
                float z = vertices[j + 2];
                vertexConsumer.vertex(
                        matrix4f.m00() * x + matrix4f.m10() * y + matrix4f.m20() * z + matrix4f.m30(),
                        matrix4f.m01() * x + matrix4f.m11() * y + matrix4f.m21() * z + matrix4f.m31(),
                        matrix4f.m02() * x + matrix4f.m12() * y + matrix4f.m22() * z + matrix4f.m32(),
                        r, g, b, a,
                        vertices[j + 3], vertices[j + 4], overlay, light,
                        normalDest.x, normalDest.y, normalDest.z);
            }
        }
    }

    public static MeshedModelBone convert(ModelPart modelPart) {
        List<ModelPart.Cube> cubes = modelPart.getCubes();
        Map<Normal, List<ModelPart.Vertex>> normalToVertices = new HashMap<>();
        int vertexCount = 0;
        for (ModelPart.Cube cube : cubes) {
            for (ModelPart.Polygon polygon : cube.getPolygons()) {
                Normal of = Normal.of(polygon);
                List<ModelPart.Vertex> vertices = normalToVertices.getOrDefault(of, null);
                if (vertices == null) {
                    vertices = new ArrayList<>();
                    normalToVertices.put(of, vertices);
                }
                vertices.addAll(Arrays.asList(polygon.vertices));
                vertexCount += polygon.vertices.length;
            }
        }
        if (vertexCount == 0 || normalToVertices.isEmpty()) {
            return null;
        }
        MeshedModelBone meshedModelBone = new MeshedModelBone();
        meshedModelBone.normals = new float[normalToVertices.size() * NORMAL_PACK_SIZE];
        meshedModelBone.vertices = new float[vertexCount * VERTEX_PACK_SIZE];
        meshedModelBone.normalToVertices = new int[normalToVertices.size() * NORMAL_TO_VERTEX_PACK_SIZE];
        int vertexIndex = 0;
        int normalIndex = 0;
        for (Map.Entry<Normal, List<ModelPart.Vertex>> entry : normalToVertices.entrySet()) {
            Normal key = entry.getKey();
            List<ModelPart.Vertex> value = entry.getValue();
            if (value == null || value.isEmpty()) {
                continue;
            }
            meshedModelBone.normals[normalIndex * NORMAL_PACK_SIZE] = key.normal.x;
            meshedModelBone.normals[normalIndex * NORMAL_PACK_SIZE + 1] = key.normal.y;
            meshedModelBone.normals[normalIndex * NORMAL_PACK_SIZE + 2] = key.normal.z;
            meshedModelBone.normalToVertices[normalIndex * NORMAL_TO_VERTEX_PACK_SIZE] = vertexIndex;
            for (ModelPart.Vertex vertex : value) {
                int start = vertexIndex;
                meshedModelBone.vertices[start] = vertex.pos.x;
                meshedModelBone.vertices[start + 1] = vertex.pos.y;
                meshedModelBone.vertices[start + 2] = vertex.pos.z;
                meshedModelBone.vertices[start + 3] = vertex.u;
                meshedModelBone.vertices[start + 4] = vertex.v;
                vertexIndex += VERTEX_PACK_SIZE;
            }
            meshedModelBone.normalToVertices[normalIndex * NORMAL_TO_VERTEX_PACK_SIZE + 1] = vertexIndex;
            normalIndex ++;
        }
        return meshedModelBone;
    }

    private static class Normal {
        public Vector3f normal;

        private Normal(Vector3f normal) {
            this.normal = normal;
        }

        public static Normal of(ModelPart.Polygon polygon) {
            return new Normal(polygon.normal);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Normal other) {
                return normal.equals(other.normal, NORMAL_DELTA_TO_EQUIV);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return normal.hashCode();
        }
    }
}
