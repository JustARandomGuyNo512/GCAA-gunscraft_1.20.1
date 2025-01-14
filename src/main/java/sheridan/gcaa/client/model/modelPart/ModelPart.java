package sheridan.gcaa.client.model.modelPart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.lang.Math;
import java.util.*;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public final class ModelPart {
    public static final ModelPart EMPTY = new ModelPart(List.of(), Map.of());
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
    public boolean skipDraw;
    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;
    private PartPose initialPose = PartPose.ZERO;
    private Polygon[] polygons;
    private boolean meshed = false;

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

    /**
     * Load initial pose
     * */
    public void resetPose() {
        this.loadPose(this.initialPose);
    }

    /**
     * Load initial pose for children and self
     * */
    public void resetPoseAll() {
        resetPose();
        for (ModelPart modelPart : this.children.values()) {
            modelPart.resetPoseAll();
        }
    }

    /**
     * Set the model part's pose to the given pose(scale is not included).
     * */
    public void loadPose(PartPose partPose) {
        x = partPose.x;
        y = partPose.y;
        z = partPose.z;
        xRot = partPose.xRot;
        yRot = partPose.yRot;
        zRot = partPose.zRot;
        xScale = 1.0F;
        yScale = 1.0F;
        zScale = 1.0F;
    }

    /**
     * Set the model part's pose to the given listTag, which is a list of 9 floats(x, y, z, rotX, rotY, rotZ).
     * */
    public void copyFrom(ListTag tag) {
        x = tag.getFloat(0);
        y = tag.getFloat(1);
        z = tag.getFloat(2);
        xRot = tag.getFloat(3);
        yRot = tag.getFloat(4);
        zRot = tag.getFloat(5);
        xScale = tag.getFloat(6);
        yScale = tag.getFloat(7);
        zScale = tag.getFloat(8);
    }

    /**
     * Set the model part's pose to the given array, which is an array of 9 floats(x, y, z, rotX, rotY, rotZ).
     * */
    public void copyFrom(float[] translation) {
        x = translation[0];
        y = translation[1];
        z = translation[2];
        xRot = translation[3];
        yRot = translation[4];
        zRot = translation[5];
        xScale = translation[6];
        yScale = translation[7];
        zScale = translation[8];
    }

    /**
     * Copy the model part's pose from the given model part.
     * */
    public void copyFrom(ModelPart modelPart) {
        xScale = modelPart.xScale;
        yScale = modelPart.yScale;
        zScale = modelPart.zScale;
        xRot = modelPart.xRot;
        yRot = modelPart.yRot;
        zRot = modelPart.zRot;
        x = modelPart.x;
        y = modelPart.y;
        z = modelPart.z;
    }

    /**
     * @param progress a float between 0 and 1, where 0 is the current pose and 1 is the target pose.
     * @param to the target pose.
     * <p>
     *           do a linear pose lerp between the current pose and the target pose.
     * </p>
     * */
    public void lerpTo(ModelPart to, float progress) {
        xScale += (to.xScale - xScale) * progress;
        yScale += (to.yScale - yScale) * progress;
        zScale += (to.zScale - zScale) * progress;
        xRot += (to.xRot - xRot) * progress;
        yRot += (to.yRot - yRot) * progress;
        zRot += (to.zRot - zRot) * progress;
        x += (to.x - x) * progress;
        y += (to.y - y) * progress;
        z += (to.z - z) * progress;
    }

    public void resetChildLayerName(String name, String newName) {
        if (children.containsKey(name)) {
            ModelPart part = children.get(name);
            children.remove(name);
            children.put(newName, part);
        }
    }

    public void addChild(String name, ModelPart child) {
        children.put(name, child);
    }

    public boolean hasChild(String childName)  {
        return children.containsKey(childName);
    }

    public boolean hasChildRecursive(String childName)  {
        if (children.containsKey(childName)) {
            return true;
        } else {
            if (!children.isEmpty()) {
                for (ModelPart modelpart : this.children.values()) {
                    return modelpart.hasChild(childName);
                }
            }
            return false;
        }
    }

    public List<Cube> getCubes() {
        return this.cubes;
    }

    public Map<String, ModelPart> getChildren() {
        return this.children;
    }

    public ModelPart getChild(String childName) {
        ModelPart modelpart = this.children.get(childName);
        if (modelpart == null) {
            throw new NoSuchElementException("Can't find bone " + childName);
        } else {
            return modelpart;
        }
    }

    public boolean collisionNoRot(ModelPart other) {
        if (this == other) {
            return false;
        }
        List<Cube> cubes = other.getCubes();
        if (!cubes.isEmpty()) {
            for (Cube cube : this.cubes) {
                for (Cube cube2 : cubes) {
                    if (cube.collisionNoRot(cube2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ModelPart getChildNoThrow(String childName) {
        return this.children.get(childName);
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
        this.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F, true);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha, true);
    }

    private final Vector3f normal = new Vector3f();
    private final Vector4f vec = new Vector4f();
    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha, boolean usePose) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty() || this.polygons != null) {
                pPoseStack.pushPose();
                if (usePose) {
                    this.translateAndRotate(pPoseStack);
                }
                if (!this.skipDraw) {
                    if (meshed) {
                        Matrix4f matrix4f = pPoseStack.last().pose();
                        Matrix3f matrix3f = pPoseStack.last().normal();
                        for(Polygon polygon : this.polygons) {
                            Vector3f vector3f = matrix3f.transform(normal.set(polygon.normal.x, polygon.normal.y, polygon.normal.z));
                            for(Vertex vertex : polygon.vertices) {
                                float f3 = vertex.pos.x() * 0.0625F;
                                float f4 = vertex.pos.y() * 0.0625F;
                                float f5 = vertex.pos.z() * 0.0625F;
                                Vector4f vector4f = matrix4f.transform(vec.set(f3, f4, f5, 1.0F));
                                pVertexConsumer.vertex(
                                        vector4f.x, vector4f.y, vector4f.z,
                                        pRed, pGreen, pBlue, pAlpha,
                                        vertex.u, vertex.v,
                                        pPackedOverlay, pPackedLight,
                                        vector3f.x, vector3f.y, vector3f.z);
                            }
                        }
                    } else {
                        this.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
                    }
                }
                for(ModelPart modelpart : this.children.values()) {
                    modelpart.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha, true);
                }
                pPoseStack.popPose();
            }
        }
    }


    /**
     * This method consolidates the child parts of the model into a single mesh.
     * It processes child parts with names starting with "_SUB_R_", applying
     * their respective rotations and translations, and combines their polygons
     * into a unified structure for optimized rendering.
     * <p>
     * This method is crucial for preparing the model for efficient rendering,
     * ensuring that all necessary transformations are applied and reducing the
     * complexity of the rendering process.
     * </p>
     *
     * Note: This method should be called only once. Subsequent calls will have
     * no effect if the mesh has already been processed.
     */
    public ModelPart meshing() {
        if (meshed) {
            return this;
        }
        Set<String> removeParts = new HashSet<>();
        for (String key : this.children.keySet()) {
            if (key.startsWith("_SUB_R_")) {
                ModelPart part = this.children.get(key);
                Cube cube = part.getCube(0);
                for (Polygon polygon : cube.polygons) {
                    polygon.applyRotation(part.xRot, part.yRot, part.zRot);
                    polygon.applyMove(part.x, part.y, part.z);
                }
                this.cubes.add(cube);
                removeParts.add(key);
            }
        }
        for (String key : removeParts) {
            this.children.remove(key);
        }
        List<Polygon> allPolygon = new ArrayList<>();
        for (Cube cube : cubes) {
            cube.polygons(allPolygon);
        }
        polygons = allPolygon.toArray(new Polygon[0]);
        meshed = true;
        return this;
    }

    public Cube getCube(int index) {
        return cubes.get(index);
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
        for(Cube cube : this.cubes) {
            cube.compile(pose, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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

    public void print() {
        System.out.println(this.toString());
        for (Cube cube : cubes) {
            System.out.println(cube);
        }
        for (Map.Entry<String, ModelPart> partEntry : children.entrySet()) {
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

        /**
         * For minecraft hardcode java edition entity model...
         * */
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

        public boolean collisionNoRot(Cube other)  {
            return this.maxX >= other.minX && this.minX <= other.maxX &&
                    this.maxY >= other.minY && this.minY <= other.maxY &&
                    this.maxZ >= other.minZ && this.minZ <= other.maxZ;
        }

       public void polygons(List<Polygon> polygons) {
            polygons.addAll(Arrays.asList(this.polygons));
       }

        @Override
        public String toString() {
            return "Cube{" +
                    "minX=" + minX +
                    ", minY=" + minY +
                    ", minZ=" + minZ +
                    ", maxX=" + maxX +
                    ", maxY=" + maxY +
                    ", maxZ=" + maxZ +
                    '}';
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

        public void applyRotation(float angleX, float angleY, float angleZ) {
            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = vertices[i].rotate(angleX, angleY, angleZ);
            }
            rotateNormal(this.normal, angleX, angleY, angleZ);
        }

        public void applyMove(float x, float y, float z) {
            for (Vertex vertex : vertices) {
                vertex.move(x, y, z);
            }
        }

        private void rotateNormal(Vector3f normal, float angleX, float angleY, float angleZ) {
            float cosX = (float) Math.cos(angleX);
            float sinX = (float) Math.sin(angleX);
            float y = normal.y * cosX - normal.z * sinX;
            float z = normal.y * sinX + normal.z * cosX;
            normal.y = y;
            normal.z = z;

            float cosY = (float) Math.cos(angleY);
            float sinY = (float) Math.sin(angleY);
            float x = normal.x * cosY + normal.z * sinY;
            z = -normal.x * sinY + normal.z * cosY;
            normal.x = x;
            normal.z = z;

            float cosZ = (float) Math.cos(angleZ);
            float sinZ = (float) Math.sin(angleZ);
            x = normal.x * cosZ - normal.y * sinZ;
            y = normal.x * sinZ + normal.y * cosZ;
            normal.x = x;
            normal.y = y;
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

        public void move(float x, float y, float z) {
            this.pos.add(x, y, z);
        }

        public Vertex rotate(float angleX, float angleY, float angleZ) {
            Vector3f newPos = new Vector3f(pos);
            float cosX = (float) Math.cos(angleX);
            float sinX = (float) Math.sin(angleX);

            newPos.set(pos.x, pos.y * cosX - pos.z * sinX, pos.y * sinX + pos.z * cosX);
            float cosY = (float) Math.cos(angleY);
            float sinY = (float) Math.sin(angleY);
            newPos.set(newPos.x * cosY + newPos.z * sinY, newPos.y, -newPos.x * sinY + newPos.z * cosY);

            float cosZ = (float) Math.cos(angleZ);
            float sinZ = (float) Math.sin(angleZ);
            newPos.set(newPos.x * cosZ - newPos.y * sinZ, newPos.x * sinZ + newPos.y * cosZ, newPos.z);

            return new Vertex(newPos, u, v);
        }
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface Visitor {
        void visit(PoseStack.Pose pPose, String pPath, int pIndex, Cube pCube);
    }
}