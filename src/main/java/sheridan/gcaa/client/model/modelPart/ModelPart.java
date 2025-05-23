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
public final class ModelPart implements IAnimatedModelPart {
    public static final ModelPart EMPTY = new ModelPart(List.of(), Map.of());
    public ModelPart parent;
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
    private boolean meshed = false;
    private boolean touched = false;
    public MergedModelPartData meshedModelBone;
    public String debug_name = "";
    private static final Cube ZERO = new Cube(0,0,0,0,0,0,0,0,0,0,0,false,Set.of());

    public ModelPart(List<Cube> cubes, Map<String, ModelPart> children) {
        this.cubes = cubes;
        this.children = children;
        for (ModelPart modelPart : children.values()) {
            modelPart.parent = this;
        }
    }

    public ModelPart() {
        cubes = new ArrayList<>();
        children = new HashMap<>();
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
        if (touched) {
            this.loadPose(this.initialPose);
            touched = false;
        }
    }

    public void forceResetPose() {
        this.loadPose(this.initialPose);
        touched = false;
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

    public void forceResetPoseAll() {
        forceResetPose();
        for (ModelPart modelPart : this.children.values()) {
            modelPart.forceResetPoseAll();
        }
    }

    public void setTouched() {
        this.touched = true;
    }

    public ModelPart findChildByPath(String path) {
        String[] paths = path.split("/");
        ModelPart target = this;
        for (String s : paths) {
            if (target.hasChild(s)) {
                target = target.getChild(s);
            } else {
                return null;
            }
        }
        return target;
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
        touched = true;
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
        touched = true;
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
        touched = true;
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
        touched = true;
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
        touched = true;
    }

    public void resetChildLayerName(String name, String newName) {
        if (children.containsKey(name)) {
            ModelPart part = children.get(name);
            children.remove(name);
            children.put(newName, part);
            part.debug_name = newName;
        }
    }

    public void addChild(String name, ModelPart child) {
        children.put(name, child);
        child.parent = this;
        child.debug_name = name;
    }

    public List<Cube> getCubes() {
        return cubes;
    }

    public void setX(float x) {
        this.x = x;
        touched = true;
    }

    public void setY(float y) {
        this.y = y;
        touched = true;
    }

    public void setZ(float z) {
        this.z = z;
        touched = true;
    }

    public void setxRot(float xRot) {
        this.xRot = xRot;
        touched = true;
    }

    public void setyRot(float yRot) {
        this.yRot = yRot;
        touched = true;
    }

    public void setzRot(float zRot) {
        this.zRot = zRot;
        touched = true;
    }

    public void setxScale(float xScale) {
        this.xScale = xScale;
        touched = true;
    }

    public void setyScale(float yScale) {
        this.yScale = yScale;
        touched = true;
    }

    public void setzScale(float zScale) {
        this.zScale = zScale;
        touched = true;
    }

    public void addX(float x) {
        this.x += x;
        touched = true;
    }

    public void addY(float y) {
        this.y += y;
        touched = true;
    }

    public void addZ(float z) {
        this.z += z;
        touched = true;
    }

    public void addxRot(float xRot) {
        this.xRot += xRot;
        touched = true;
    }

    public void addyRot(float yRot) {
        this.yRot += yRot;
        touched = true;
    }

    public void addzRot(float zRot) {
        this.zRot += zRot;
        touched = true;
    }

    public void addxScale(float xScale) {
        this.xScale += xScale;
        touched = true;
    }

    public void addyScale(float yScale) {
        this.yScale += yScale;
        touched = true;
    }

    public void addzScale(float zScale) {
        this.zScale += zScale;
        touched = true;
    }

    public boolean hasChild(String childName)  {
        return children.containsKey(childName);
    }

    public boolean containsChild(String childName)  {
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

    public Map<String, ModelPart> getChildren() {
        return this.children;
    }

    public boolean hasChild() {
        return children != null && !children.isEmpty();
    }

    public ModelPart getChild(String childName) {
        ModelPart modelpart = this.children.get(childName);
        if (modelpart == null) {
            throw new NoSuchElementException("Can't find bone " + childName);
        } else {
            return modelpart;
        }
    }

    public ModelPart getUniqueChild() {
        return children.values().iterator().next();
    }


    public ModelPart getChildNoThrow(String childName) {
        return this.children.get(childName);
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        touched = true;
    }

    public void setRotation(float rx, float ry, float rz) {
        this.xRot = rx;
        this.yRot = ry;
        this.zRot = rz;
        touched = true;
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int pPackedLight, int pPackedOverlay) {
        this.render(poseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F, true);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha, true);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha, boolean usePose) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                pPoseStack.pushPose();
                if (usePose) {
                    this.translateAndRotate(pPoseStack);
                }
                if (!this.skipDraw) {
                    if (this.meshedModelBone != null) {
                        this.meshedModelBone.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
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
        this.meshedModelBone = MergedModelPartData.convert(this);
        if (this.meshedModelBone != null) {
            cubes.clear();
            cubes.add(ZERO);
        }
        meshed = true;
        return this;
    }

    public void meshingAll() {
        meshing();
        for (ModelPart modelPart : children.values()) {
            modelPart.meshingAll();
        }
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

    private void compile(PoseStack.Pose pose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        for(Cube cube : this.cubes) {
            cube.compileNew(pose, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }

    public Cube getRandomCube(RandomSource randomSource) {
        return this.cubes.get(randomSource.nextInt(this.cubes.size()));
    }

    public boolean isEmpty() {
        return this.cubes.isEmpty();
    }

    @Override
    public void offsetPos(Vector3f pos) {
        this.x += pos.x();
        this.y += pos.y();
        this.z += pos.z();
        touched = true;
    }

    @Override
    public void offsetRotation(Vector3f rot) {
        this.xRot += rot.x();
        this.yRot += rot.y();
        this.zRot += rot.z();
        touched = true;
    }

    @Override
    public void offsetScale(Vector3f scale) {
        this.xScale += scale.x();
        this.yScale += scale.y();
        this.zScale += scale.z();
        touched = true;
    }

    @Override
    public Stream<IAnimatedModelPart> getAllParts() {
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


        Vector3f destVec3 = new Vector3f();
        public void compileNew(PoseStack.Pose pose, VertexConsumer vertexConsumer, int light, int overlay, float r, float g, float b, float a) {
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            for(Polygon polygon : this.polygons) {
                matrix3f.transform(polygon.normal.x, polygon.normal.y, polygon.normal.z, destVec3);
                for(Vertex vertex : polygon.vertices) {
                    float x = vertex.pos.x();
                    float y = vertex.pos.y();
                    float z = vertex.pos.z();
                    vertexConsumer.vertex(
                            matrix4f.m00() * x + matrix4f.m10() * y + matrix4f.m20() * z + matrix4f.m30(),
                            matrix4f.m01() * x + matrix4f.m11() * y + matrix4f.m21() * z + matrix4f.m31(),
                            matrix4f.m02() * x + matrix4f.m12() * y + matrix4f.m22() * z + matrix4f.m32(),
                            r, g, b, a,
                            vertex.u, vertex.v, overlay, light,
                            destVec3.x, destVec3.y, destVec3.z);
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

       public Polygon[] getPolygons() {
            return polygons;
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
            this(new Vector3f(x / 16, y / 16, z / 16), u, v);
        }

        public Vertex(float x, float y, float z) {
            this(new Vector3f(x / 16, y / 16, z / 16));
        }

        public Vertex(Vector3f pos) {
            this.pos = pos;
            u = 0;
            v = 0;
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
            this.pos.add(x / 16, y / 16, z / 16);
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