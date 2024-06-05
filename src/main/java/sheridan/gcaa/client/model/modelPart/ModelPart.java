package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.util.*;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public final class ModelPart {
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    public boolean visible = true;
    private boolean meshed = false;
    private Polygon[] polygons;
    public boolean skipDraw;
    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;
    private PartPose initialPose = PartPose.ZERO;

    public ModelPart(List<Cube> cubes, Map<String, ModelPart> children) {
        this.cubes = cubes;
        this.children = children;
    }

    public PartPose storePose() {
        return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
    }

    public PartPose getInitialPose() {
        return this.initialPose;
    }

    public void setInitialPose(PartPose partPose) {
        this.initialPose = partPose;
    }

    public void resetPose() {
        this.loadPose(this.initialPose);
    }

    public void loadPose(PartPose partPose) {
        this.x = partPose.x;
        this.y = partPose.y;
        this.z = partPose.z;
        this.xRot = partPose.xRot;
        this.yRot = partPose.yRot;
        this.zRot = partPose.zRot;
        this.xScale = 1.0F;
        this.yScale = 1.0F;
        this.zScale = 1.0F;
    }

    public void copyFrom(ListTag tag) {
        this.x = tag.getFloat(0);
        this.y = tag.getFloat(1);
        this.z = tag.getFloat(2);
        this.xRot = tag.getFloat(3);
        this.yRot = tag.getFloat(4);
        this.zRot = tag.getFloat(5);
        this.xScale = tag.getFloat(6);
        this.yScale = tag.getFloat(7);
        this.zScale = tag.getFloat(8);
    }

    public void copyFrom(float[] translation) {
        this.x = translation[0];
        this.y = translation[1];
        this.z = translation[2];
        this.xRot = translation[3];
        this.yRot = translation[4];
        this.zRot = translation[5];
        this.xScale = translation[6];
        this.yScale = translation[7];
        this.zScale = translation[8];
    }

    public void copyFrom(ModelPart modelPart) {
        this.xScale = modelPart.xScale;
        this.yScale = modelPart.yScale;
        this.zScale = modelPart.zScale;
        this.xRot = modelPart.xRot;
        this.yRot = modelPart.yRot;
        this.zRot = modelPart.zRot;
        this.x = modelPart.x;
        this.y = modelPart.y;
        this.z = modelPart.z;
    }

    public boolean hasChild(String childName) {
        return this.children.containsKey(childName);
    }

    public ModelPart getChild(String childName) {
        ModelPart modelpart = this.children.get(childName);
        if (modelpart == null) {
            throw new NoSuchElementException("Can't find bone " + childName);
        } else {
            return modelpart;
        }
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(float rx, float ry, float rz) {
        this.xRot = rx;
        this.yRot = ry;
        this.zRot = rz;
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int pPackedLight, int pPackedOverlay) {
        this.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                pPoseStack.pushPose();
                this.translateAndRotate(pPoseStack);
                if (!this.skipDraw) {
                    this.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
                }
                for(ModelPart modelpart : this.children.values()) {
                    modelpart.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
                }
                pPoseStack.popPose();
            }
        }
    }

    /**
     * Transforms all cubes to a more simple polygon array, which can make render faster.
     *
     * Removes all the cubes from cube list of this model part.
     * The cube list will be empty after this call returns.
     */
    public void meshing() {
        meshed = true;
        List<Polygon> allPolygons = new ArrayList<>();
        for (Cube cube : cubes) {
            cube.polygons(allPolygons);
        }
        polygons = allPolygons.toArray(new Polygon[0]);
        this.cubes.clear();
    }


    public boolean isMeshed() {
        return meshed;
    }

    public void visit(PoseStack pPoseStack, Visitor pVisitor) {
        this.visit(pPoseStack, pVisitor, "");
    }

    private void visit(PoseStack pPoseStack, Visitor pVisitor, String pPath) {
        if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
            pPoseStack.pushPose();
            this.translateAndRotate(pPoseStack);
            PoseStack.Pose pose = pPoseStack.last();

            for(int i = 0; i < this.cubes.size(); ++i) {
                pVisitor.visit(pose, pPath, i, this.cubes.get(i));
            }
            String s = pPath + "/";
            this.children.forEach((p_171320_, p_171321_) -> {
                p_171321_.visit(pPoseStack, pVisitor, s + p_171320_);
            });
            pPoseStack.popPose();
        }
    }


    public void translateAndRotate(PoseStack pPoseStack) {
        pPoseStack.translate(this.x * 0.0625F, this.y * 0.0625F, this.z * 0.0625F);
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
            pPoseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
        }
        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            pPoseStack.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    public void applyInitialPose(PoseStack poseStack) {
        poseStack.translate(initialPose.x * 0.0625F, initialPose.y * 0.0625F, initialPose.z * 0.0625F);
        if (initialPose.xRot != 0.0F || initialPose.yRot != 0.0F || initialPose.zRot != 0.0F) {
            poseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
        }
    }

    private void compile(PoseStack.Pose pose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        if (meshed) {
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            for (Polygon polygon : polygons) {
                Vector3f vector3f = matrix3f.transform(new Vector3f(polygon.normal));
                float f = vector3f.x();
                float f1 = vector3f.y();
                float f2 = vector3f.z();
                for(Vertex vertex : polygon.vertices) {
                    float f3 = vertex.pos.x() * 0.0625F;
                    float f4 = vertex.pos.y() * 0.0625F;
                    float f5 = vertex.pos.z() * 0.0625F;
                    Vector4f vector4f = matrix4f.transform(new Vector4f(f3, f4, f5, 1.0F));
                    pVertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), pRed, pGreen, pBlue, pAlpha, vertex.u, vertex.v, pPackedOverlay, pPackedLight, f, f1, f2);
                }
            }
        } else {
            for(Cube cube : this.cubes) {
                cube.compile(pose, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            }
        }

    }

    public Cube getRandomCube(RandomSource randomSource) {
        return this.cubes.get(randomSource.nextInt(this.cubes.size()));
    }

    public boolean isEmpty() {
        return this.cubes.isEmpty();
    }

    public void offsetPos(Vector3f pos) {
        this.x += pos.x();
        this.y += pos.y();
        this.z += pos.z();
    }

    public void offsetRotation(Vector3f rot) {
        this.xRot += rot.x();
        this.yRot += rot.y();
        this.zRot += rot.z();
    }

    public void offsetScale(Vector3f scale) {
        this.xScale += scale.x();
        this.yScale += scale.y();
        this.zScale += scale.z();
    }

    public Stream<ModelPart> getAllParts() {
        return Stream.concat(Stream.of(this), this.children.values().stream().flatMap(ModelPart::getAllParts));
    }

    public static class UvPolygon {
        public Direction direction;
        public float u1;
        public float v1;
        public float u2;
        public float v2;

        public UvPolygon(Direction direction, float u1, float v1, float u2, float v2) {
            this.direction = direction;
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Cube {
        private final Polygon[] polygons;
        public float minX;
        public float minY;
        public float minZ;
        public float maxX;
        public float maxY;
        public float maxZ;

        public Cube(float originX, float originY, float originZ,
                    float xLen, float yLen, float zLen,
                    float xGrow, float yGrow, float zGrow,
                    float textureWidth, float textureHeight,boolean mirror,
                    Set<UvPolygon> uvPolygons){
            this.minX = originX;
            this.minY = originY;
            this.minZ = originZ;
            this.maxX = originX + xLen;
            this.maxY = originY + yLen;
            this.maxZ = originZ + zLen;
            this.polygons = new Polygon[uvPolygons.size()];
            float growMaxX = originX + xLen;
            float growMaxY = originY + yLen;
            float growMaxZ = originZ + zLen;
            originX -= xGrow;
            originY -= yGrow;
            originZ -= zGrow;
            growMaxX += xGrow;
            growMaxY += yGrow;
            growMaxZ += zGrow;
            if (mirror) {
                float f3 = growMaxX;
                growMaxX = originX;
                originX = f3;
            }
            Vertex vertex0 = new Vertex(growMaxX, originY, originZ);
            Vertex vertex1 = new Vertex(growMaxX, growMaxY, originZ);
            Vertex vertex2 = new Vertex(originX, growMaxY, originZ);
            Vertex vertex3 = new Vertex(originX, originY, growMaxZ);
            Vertex vertex4 = new Vertex(growMaxX, originY, growMaxZ);
            Vertex vertex5 = new Vertex(growMaxX, growMaxY, growMaxZ);
            Vertex vertex6 = new Vertex(originX, growMaxY, growMaxZ);
            Vertex vertex7 = new Vertex(originX, originY, originZ);
            int index = 0;
            for (UvPolygon polygon : uvPolygons) {
                switch (polygon.direction) {
                    case UP -> this.polygons[index] = new Polygon(new Vertex[]{vertex4, vertex3, vertex7, vertex0}, polygon.u1, polygon.v1, polygon.u2, polygon.v2, textureWidth, textureHeight, false, Direction.DOWN);
                    case DOWN -> this.polygons[index] = new Polygon(new Vertex[]{vertex1, vertex2, vertex6, vertex5}, polygon.u1, polygon.v1, polygon.u2, polygon.v2, textureWidth, textureHeight, false, Direction.UP);
                    case EAST -> this.polygons[index] = new Polygon(new Vertex[]{vertex7, vertex3, vertex6, vertex2}, polygon.u1, polygon.v1, polygon.u2, polygon.v2, textureWidth, textureHeight, false, Direction.WEST);
                    case NORTH -> this.polygons[index] = new Polygon(new Vertex[]{vertex0, vertex7, vertex2, vertex1}, polygon.u1, polygon.v1, polygon.u2, polygon.v2, textureWidth, textureHeight, false, Direction.NORTH);
                    case WEST -> this.polygons[index] = new Polygon(new Vertex[]{vertex4, vertex0, vertex1, vertex5}, polygon.u1, polygon.v1, polygon.u2, polygon.v2, textureWidth, textureHeight, false, Direction.EAST);
                    case SOUTH -> this.polygons[index] = new Polygon(new Vertex[]{vertex3, vertex4, vertex5, vertex6}, polygon.u1, polygon.v1, polygon.u2, polygon.v2, textureWidth, textureHeight, false, Direction.SOUTH);
                }
                index ++;
            }
        }

        public Cube(int u, int v, float originX, float originY, float originZ, float xLen, float yLen, float zLen, float xGrow, float yGrow, float zGrow, boolean mirror, float textureWidth, float textureHeight, Set<Direction> directions) {
            this.minX = originX;
            this.minY = originY;
            this.minZ = originZ;
            this.maxX = originX + xLen;
            this.maxY = originY + yLen;
            this.maxZ = originZ + zLen;
            this.polygons = new Polygon[directions.size()];
            float growMaxX = originX + xLen;
            float growMaxY = originY + yLen;
            float growMaxZ = originZ + zLen;
            originX -= xGrow;
            originY -= yGrow;
            originZ -= zGrow;
            growMaxX += xGrow;
            growMaxY += yGrow;
            growMaxZ += zGrow;
            if (mirror) {
                float f3 = growMaxX;
                growMaxX = originX;
                originX = f3;
            }
            Vertex vertex0 = new Vertex(growMaxX, originY, originZ, 0.0F, 8.0F);
            Vertex vertex1 = new Vertex(growMaxX, growMaxY, originZ, 8.0F, 8.0F);
            Vertex vertex2 = new Vertex(originX, growMaxY, originZ, 8.0F, 0.0F);
            Vertex vertex3 = new Vertex(originX, originY, growMaxZ, 0.0F, 0.0F);
            Vertex vertex4 = new Vertex(growMaxX, originY, growMaxZ, 0.0F, 8.0F);
            Vertex vertex5 = new Vertex(growMaxX, growMaxY, growMaxZ, 8.0F, 8.0F);
            Vertex vertex6 = new Vertex(originX, growMaxY, growMaxZ, 8.0F, 0.0F);
            Vertex vertex7 = new Vertex(originX, originY, originZ, 0.0F, 0.0F);
            float f4 = (float)u;
            float f5 = (float)u + zLen;
            float f6 = (float)u + zLen + xLen;
            float f7 = (float)u + zLen + xLen + xLen;
            float f8 = (float)u + zLen + xLen + zLen;
            float f9 = (float)u + zLen + xLen + zLen + xLen;
            float f10 = (float)v;
            float f11 = (float)v + zLen;
            float f12 = (float)v + zLen + yLen;
            int i = 0;

            if (directions.contains(Direction.DOWN)) {
                this.polygons[i++] = new Polygon(new Vertex[]{vertex4, vertex3, vertex7, vertex0}, f5, f10, f6, f11, textureWidth, textureHeight, mirror, Direction.DOWN);
            }

            if (directions.contains(Direction.UP)) {
                this.polygons[i++] = new Polygon(new Vertex[]{vertex1, vertex2, vertex6, vertex5}, f6, f11, f7, f10, textureWidth, textureHeight, mirror, Direction.UP);
            }

            if (directions.contains(Direction.WEST)) {
                this.polygons[i++] = new Polygon(new Vertex[]{vertex7, vertex3, vertex6, vertex2}, f4, f11, f5, f12, textureWidth, textureHeight, mirror, Direction.WEST);
            }

            if (directions.contains(Direction.NORTH)) {
                this.polygons[i++] = new Polygon(new Vertex[]{vertex0, vertex7, vertex2, vertex1}, f5, f11, f6, f12, textureWidth, textureHeight, mirror, Direction.NORTH);
            }

            if (directions.contains(Direction.EAST)) {
                this.polygons[i++] = new Polygon(new Vertex[]{vertex4, vertex0, vertex1, vertex5}, f6, f11, f8, f12, textureWidth, textureHeight, mirror, Direction.EAST);
            }

            if (directions.contains(Direction.SOUTH)) {
                this.polygons[i] = new Polygon(new Vertex[]{vertex3, vertex4, vertex5, vertex6}, f8, f11, f9, f12, textureWidth, textureHeight, mirror, Direction.SOUTH);
            }

        }

        public void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int light, int overlay, float r, float g, float b, float a) {
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            for(Polygon polygon : this.polygons) {
                Vector3f vector3f = matrix3f.transform(new Vector3f(polygon.normal));
                for(Vertex vertex : polygon.vertices) {
                    float f3 = vertex.pos.x() * 0.0625F;
                    float f4 = vertex.pos.y() * 0.0625F;
                    float f5 = vertex.pos.z() * 0.0625F;
                    Vector4f vector4f = matrix4f.transform(new Vector4f(f3, f4, f5, 1.0F));
                    vertexConsumer.vertex(
                            vector4f.x(), vector4f.y(), vector4f.z(),
                            r, g, b, a,
                            vertex.u, vertex.v,
                            overlay, light,
                            vector3f.x(), vector3f.y(), vector3f.z());
                }
            }

        }

       public void polygons(List<Polygon> polygons) {
            polygons.addAll(List.of(this.polygons));
       }
    }

    @OnlyIn(Dist.CLIENT)
    static class Polygon {
        public final Vertex[] vertices;
        public final Vector3f normal;

        public Polygon(Vertex[] vertices, float u1, float v1, float u2, float v2, float textureWidth, float textureHeight, boolean mirror, Direction direction) {
            this.vertices = vertices;
            vertices[0] = vertices[0].remap(u2 / textureWidth, v1 / textureHeight);
            vertices[1] = vertices[1].remap(u1 / textureWidth, v1 / textureHeight);
            vertices[2] = vertices[2].remap(u1 / textureWidth, v2 / textureHeight);
            vertices[3] = vertices[3].remap(u2 / textureWidth, v2 / textureHeight);
            if (mirror) {
                int i = vertices.length;
                for(int j = 0; j < i / 2; ++j) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }

            this.normal = direction.step();
            if (mirror) {
                this.normal.mul(-1.0F, 1.0F, 1.0F);
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public Vertex(float x, float y, float z) {
            this(new Vector3f(x, y, z));
        }

        public Vertex(Vector3f pos) {
            this.pos = pos;
            u =0;
            v=0;
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface Visitor {
        void visit(PoseStack.Pose pPose, String pPath, int pIndex, Cube pCube);
    }
}