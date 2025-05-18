package sheridan.gcaa.client.model.io;

import com.jme3.anim.Armature;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.scene.mesh.IndexBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.obj.ObjLoader;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.modelPart.BufferedModelBone;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.RenderTypes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

public class GltfLoader {
    private static AssetManager assetManager;

    private static void init() {
        assetManager = new DesktopAssetManager();
        assetManager.registerLoader(GCAAGltfProxy.class, "gltf", "glb");
        assetManager.registerLocator("", GltfLocator.class);
    }

    public static BufferedModelBone loadModel(ResourceLocation location) {
        if (assetManager == null) {
            init();
        }
        Spatial model = assetManager.loadModel(location.toString());
        SkinningControl control = model.getControl(SkinningControl.class);
        if (control == null) {
            System.out.println("Skinning UnFound, please check your model, I only read gltf model that has skinning control bro! ^.^");
            return null;
        }
        BufferedModelBone root = null;
        Map<Integer, BufferedModelBone> boneMap = new HashMap<>();
        Armature armature = control.getArmature();
        for (Joint joint : armature.getJointList()) {
            BufferedModelBone bone = new BufferedModelBone();
            if ("root".equals(joint.getName())) {
                root = bone;
            }
            boneMap.put(joint.getId(), bone);
            Joint parent = joint.getParent();
            if (parent != null) {
                BufferedModelBone parentPart = boneMap.get(parent.getId());
                if (parentPart == null) {
                    parentPart = new BufferedModelBone();
                    boneMap.put(parent.getId(), parentPart);
                }
                parentPart.addChild(joint.getName(), bone);
            }
            Vector3f localTranslation = joint.getLocalTranslation();
            Quaternion localRotation = joint.getLocalRotation();
            float[] angles = new float[3];
            localRotation.toAngles(angles);
            bone.setInitialPose(PartPose.offsetAndRotation(
                    localTranslation.x, localTranslation.y, localTranslation.z,
                    angles[0],
                    angles[1],
                    angles[2]));
            bone.resetPose();
        }
        if (root == null) {
            System.out.println("Root Bone UnFound, please check your model");
            return null;
        }
        Map<BufferedModelBone, Vector3f> pivots = new HashMap<>();
        PoseStack poseStack = new PoseStack();
        buildPivots(root, pivots, poseStack);
        model.depthFirstTraversal(spatial -> {
            if (spatial instanceof Geometry geometry) {
                Mesh mesh = geometry.getMesh();
                if (mesh != null) {
                    loadMesh(mesh, boneMap, pivots);
                }
            }
        });
        root.print();
        return root;
    }

    protected static void buildPivots(BufferedModelBone bone, Map<BufferedModelBone, Vector3f> pivots, PoseStack poseStack) {
        poseStack.pushPose();
        bone.translateAndRotate(poseStack);
        org.joml.Vector3f translation = poseStack.last().pose().getTranslation(new org.joml.Vector3f());
        pivots.put(bone, new Vector3f(translation.x, translation.y, translation.z));
        for (BufferedModelBone child : bone.getChildren().values()) {
            buildPivots(child, pivots, poseStack);
        }
        poseStack.popPose();
    }

    protected static void loadMesh(Mesh mesh, Map<Integer, BufferedModelBone> boneMap, Map<BufferedModelBone, Vector3f> pivots) {
        FloatBuffer pos = mesh.getFloatBuffer(VertexBuffer.Type.Position);
        FloatBuffer nor = mesh.getFloatBuffer(VertexBuffer.Type.Normal);
        FloatBuffer uv = mesh.getFloatBuffer(VertexBuffer.Type.TexCoord);
        IndexBuffer indexBuffer = mesh.getIndexBuffer();
        ShortBuffer boneIndexBuffer = mesh.getShortBuffer(VertexBuffer.Type.BoneIndex);
        FloatBuffer boneWeights = mesh.getFloatBuffer(VertexBuffer.Type.BoneWeight);
        int componentsPerVertex = mesh.getBuffer(VertexBuffer.Type.BoneIndex).getNumComponents();

        int[] indices = new int[indexBuffer.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = indexBuffer.get(i);
        }
        for (int i = 0; i < indices.length; i++) {
            int vertexIndex = indices[i];

            float x = pos.get(vertexIndex * 3);
            float y = pos.get(vertexIndex * 3 + 1);
            float z = pos.get(vertexIndex * 3 + 2);

            float nx = nor.get(vertexIndex * 3);
            float ny = nor.get(vertexIndex * 3 + 1);
            float nz = nor.get(vertexIndex * 3 + 2);

            float u = uv.get(vertexIndex * 2);
            float v = uv.get(vertexIndex * 2 + 1);

            float maxWeight = Float.MIN_VALUE;
            int fineBoneIndex = 1;
            for (int j = 0; j < componentsPerVertex; j ++) {
                int boneIndex = boneIndexBuffer.get(vertexIndex * componentsPerVertex + j);
                float boneWeight = boneWeights.get(vertexIndex * componentsPerVertex + j);
                if (boneWeight > maxWeight) {
                    fineBoneIndex = boneIndex;
                    maxWeight = boneWeight;
                }
            }

            BufferedModelBone bone = boneMap.get(fineBoneIndex);
            if (bone.meshData == null) {
                bone.meshData = new BufferedModelBone.MeshData();
            }
//            float xDist = 0, yDist = 0, zDist = 0;
//            BufferedModelBone parent = bone;
//            while (parent != null) {
//                xDist += parent.x;
//                yDist += parent.y;
//                zDist += parent.z;
//                parent = parent.getParent();
//            }
            Vector3f vector3f = pivots.get(bone);
            float xDist = 0;
            float yDist = 0;
            float zDist = 0;
            if (vector3f != null) {
                xDist = vector3f.x;
                yDist = vector3f.y;
                zDist = vector3f.z;
            }
            int index = indexBuffer.get(i);
            bone.meshData.pushVertex(x - xDist, y - yDist, z - zDist, u, v, nx, ny, nz, index);
        }
    }

    public static void test() {
        BufferedModelBone.TEST = loadModel(new ResourceLocation(GCAA.MODID, "model_assets/test_gltf/m1a2.gltf"));
        ResourceLocation texture = new ResourceLocation(GCAA.MODID, "model_assets/test_gltf/m1a2.png");
        if (BufferedModelBone.TEST != null) {
            BufferedModelBone.TEST.compile(RenderTypes.getMeshCutOut(texture));
        }
    }

}
