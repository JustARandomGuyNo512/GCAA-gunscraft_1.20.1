package sheridan.gcaa.client.model.assets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;
import org.joml.Vector4f;
import sheridan.gcaa.client.model.modelPart.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ModelLoader {
    private static final Gson GSON_INSTANCE = new Gson();

    /**
     * This method is not thread safe.
     * <p>
     * The model should only contain and must contain one group named 'root' in the top layer and all others part should be children of the root part.
     * In simple words, the model structure should be a tree. Root is the root of the tree. and all others part are branches of the tree.
     * All bone names in model structure can not start with "_SUB_R_", because this is a reserved prefix for sub-parts.
     * <p>
     * Reads bedrock 1.12.2 format json model file, returns the layer definition of the model.
     * Call layer.get().bakeRoot().getChild("root") to get the root part of the model.
     */
    public static LayerDefinition loadModelLayer(ResourceLocation location) {
        AtomicReference<LayerDefinition> resultRef = new AtomicReference<>(null);
        try {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            manager.getResource(location).ifPresent(res -> {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        reader.close();
                        String json = stringBuilder.toString();
                        LayerDefinition result = readJsonStr(json);
                        resultRef.set(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultRef.get();
    }

    public static LayerDefinition readJsonStr(String json) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition model = meshDefinition.getRoot();
        JsonObject jsonObject = GSON_INSTANCE.fromJson(json, JsonObject.class);
        Vec2 texture = readTextureSize(jsonObject.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonObject("description"));
        readBones(jsonObject.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonArray("bones"), model);
        return LayerDefinition.create(meshDefinition, (int) texture.x, (int) texture.y);
    }

    private static Vec2 readTextureSize(JsonObject jsonObject) {
        return new Vec2(jsonObject.get("texture_width").getAsInt(), jsonObject.get("texture_height").getAsInt());
    }

    private static void readBones(JsonArray bones, PartDefinition model) {
        checkValid(bones);
        Map<String, PartDefinition> bonesMap = new HashMap<>();
        Map<PartDefinition, JsonObject> jsonObjectMap = new HashMap<>();
        constructSkeleton(bones, model, bonesMap, jsonObjectMap);
        Integer cubeIndex = 0;
        loadCubesForBones(bones, bonesMap, cubeIndex);
    }

    private static void loadCubesForBones(JsonArray bones, Map<String, PartDefinition> bonesMap, Integer cubeIndex) {
        for (JsonElement element : bones) {
            JsonObject bone = element.getAsJsonObject();
            if (bone.has("cubes")) {
                String name = bone.get("name").getAsString();
                PartDefinition boneDefinition = bonesMap.get(name);
                Vector3f pivot = getPivot(bone);
                CubeListBuilder cubeListBuilder = CubeListBuilder.create();
                for (JsonElement cubeElement : bone.get("cubes").getAsJsonArray()) {
                    JsonObject cube = cubeElement.getAsJsonObject();
                    Set<ModelPart.UvPolygon> faces = getFaces(cube.getAsJsonObject("uv"));
                    if (faces.isEmpty()) {
                        continue;
                    }
                    Vector3f origin = getAsVec3(cube, "origin");
                    if (hasRotation(cube)) {
                        Vector3f cubePivot = getPivot(cube);
                        Vector3f cubeRotation = getRotation(cube);
                        Vector3f size = getAsVec3(cube, "size");
                        Vector3f _origin = new Vector3f(-cubePivot.x, cubePivot.y, cubePivot.z);
                        Vector3f _from = new Vector3f(-size.x - origin.x, origin.y, origin.z);
                        float originX = -_from.x + (_origin.x - size.x);
                        float originY = -_from.y + (_origin.y - size.y);
                        float originZ = _from.z - _origin.z;
                        boneDefinition.addOrReplaceChild("_SUB_R_" + cubeIndex + name,
                                CubeListBuilder.create().addBox(
                                faces,
                                originX, originY, originZ,
                                size.x, size.y, size.z
                        ),
                                PartPose.offsetAndRotation(//1.08579
                                cubePivot.x - pivot.x, -cubePivot.y + pivot.y, cubePivot.z - pivot.z,
                                cubeRotation.x, cubeRotation.y, cubeRotation.z
                                ));
                        cubeIndex ++;
                    } else {
                        Vector3f size = getAsVec3(cube, "size");
                        Vector3f _origin = new Vector3f(-pivot.x, pivot.y, pivot.z);
                        Vector3f _from = new Vector3f(-size.x - origin.x, origin.y, origin.z);
                        Vector3f _to = new Vector3f(-origin.x, origin.y + size.y, size.z + origin.z);
                        float originX = _origin.x - _to.x;
                        float originY = -_from.y - size.y + _origin.y;
                        float originZ = _from.z - _origin.z;
                        cubeListBuilder.addBox(faces, originX, originY, originZ, size.x, size.y, size.z);
                    }
                }
                boneDefinition.putCubes(cubeListBuilder);
            }
        }
    }
    
    private static void constructSkeleton(JsonArray bones, PartDefinition main, Map<String, PartDefinition> bonesMap, Map<PartDefinition, JsonObject> jsonObjectMap) {
        for (JsonElement element : bones) {
            JsonObject bone = element.getAsJsonObject();
            if (bone.has("parent")) {
                String parent = bone.get("parent").getAsString();
                PartDefinition parentBone = bonesMap.get(parent);
                if (parentBone == null) {
                    throw new RuntimeException("Can't find parent of the bone named " + parent);
                }
                String name = bone.get("name").getAsString();
                if (name.startsWith("_SUB_R_")) {
                    throw new RuntimeException("illegal bone name: " + name + " bone name can not start with '_SUB_R_'");
                }
                Vector3f pivot = getPivot(bone);
                Vector3f parentPivot = getPivot(jsonObjectMap.get(parentBone));
                PartDefinition boneDefinition;
                if (bone.has("rotation")) {
                    Vector3f rotation = getRotation(bone);
                    boneDefinition = parentBone.addOrReplaceChild(name, PartPose.offsetAndRotation(
                            pivot.x, -pivot.y - parentPivot.y, pivot.z - parentPivot.z,
                            rotation.x, rotation.y, rotation.z
                    ));
                } else {
                    boneDefinition = parentBone.addOrReplaceChild(name, PartPose.offset(pivot.x, -pivot.y + parentPivot.y, pivot.z - parentPivot.z));
                }
                bonesMap.put(name, boneDefinition);
                jsonObjectMap.put(boneDefinition, bone);
            } else {
                String name = bone.get("name").getAsString();
                if (name.startsWith("_SUB_R_")) {
                    throw new RuntimeException("illegal bone name: " + name + " bone name can not start with '_SUB_R_'");
                }
                Vector3f pivot = getPivot(bone);
                PartDefinition root;
                if (!bone.has("rotation")) {
                    root = main.addOrReplaceChild("root", PartPose.offset(pivot.x, -pivot.y, pivot.z));
                } else {
                    Vector3f rotate = getRotation(bone);
                    root = main.addOrReplaceChild("root", PartPose.offsetAndRotation(pivot.x, -pivot.y, pivot.z, rotate.x, rotate.y, rotate.z));
                }
                bonesMap.put(name, root);
                jsonObjectMap.put(root, bone);
            }
        }
    }
    
    private static void checkValid(JsonArray bones) {
        boolean hasRoot = false;
        for (JsonElement element : bones) {
            JsonObject bone = element.getAsJsonObject();
            String name = bone.get("name").getAsString();
            boolean hasParent = bone.has("parent");
            if (!hasParent) {
                if ("root".equals(name)) {
                    if (!hasRoot) {
                        hasRoot = true;
                    } else {
                        throw new RuntimeException("There are multiple root bones.");
                    }
                } else {
                    throw new RuntimeException("The bone named '" + name + "' has no parent.");
                }
            }
        }
        if (!hasRoot) {
            throw new RuntimeException("There is no root bone.");
        }
    }

    private static Set<ModelPart.UvPolygon> getFaces(JsonObject uv) {
        Set<ModelPart.UvPolygon> faces = new HashSet<>();
        if (uv.has("north")) {
            Vector4f uvParams = getUV(uv.getAsJsonObject("north"));
            faces.add(new ModelPart.UvPolygon(Direction.NORTH, uvParams.x, uvParams.y, uvParams.z, uvParams.w));
        }
        if (uv.has("east")) {
            Vector4f uvParams = getUV(uv.getAsJsonObject("east"));
            faces.add(new ModelPart.UvPolygon(Direction.EAST, uvParams.x, uvParams.y, uvParams.z, uvParams.w));
        }
        if (uv.has("south")) {
            Vector4f uvParams = getUV(uv.getAsJsonObject("south"));
            faces.add(new ModelPart.UvPolygon(Direction.SOUTH, uvParams.x, uvParams.y, uvParams.z, uvParams.w));
        }
        if (uv.has("west")) {
            Vector4f uvParams = getUV(uv.getAsJsonObject("west"));
            faces.add(new ModelPart.UvPolygon(Direction.WEST, uvParams.x, uvParams.y, uvParams.z, uvParams.w));
        }
        if (uv.has("up")) {
            Vector4f uvParams = getUV(uv.getAsJsonObject("up"));
            faces.add(new ModelPart.UvPolygon(Direction.UP, uvParams.x, uvParams.y, uvParams.z, uvParams.w));
        }
        if (uv.has("down")) {
            Vector4f uvParams = getUV(uv.getAsJsonObject("down"));
            faces.add(new ModelPart.UvPolygon(Direction.DOWN, uvParams.x, uvParams.y, uvParams.z, uvParams.w));
        }
        return faces;
    }

    private static Vector4f getUV(JsonObject uv) {
        JsonArray uv0 = uv.getAsJsonArray("uv");
        JsonArray uv1 = uv.getAsJsonArray("uv_size");
        return new Vector4f(
                uv0.get(0).getAsFloat(),
                uv0.get(1).getAsFloat(),
                uv0.get(0).getAsFloat() + uv1.get(0).getAsFloat(),
                uv0.get(1).getAsFloat() + uv1.get(1).getAsFloat()
        );
    }

    private static Vector3f getAsVec3(JsonObject bone, String name) {
        return new Vector3f(
                bone.get(name).getAsJsonArray().get(0).getAsFloat(),
                bone.get(name).getAsJsonArray().get(1).getAsFloat(),
                bone.get(name).getAsJsonArray().get(2).getAsFloat()
        );
    }

    private static Vector3f getPivot(JsonObject bone) {
        return new Vector3f(
                bone.get("pivot").getAsJsonArray().get(0).getAsFloat(),
                bone.get("pivot").getAsJsonArray().get(1).getAsFloat(),
                bone.get("pivot").getAsJsonArray().get(2).getAsFloat()
        );
    }

    private static boolean hasRotation(JsonObject object) {
        if (object.has("rotation")) {
            Vector3f vector3f = getRotation(object);
            return isNotZero(vector3f.x) || isNotZero(vector3f.y) || isNotZero(vector3f.z);
        }
        return false;
    }

    private static boolean isNotZero(float val) {
        return !(val >= 0.0F - 1e-8) || !(val <= 0.0F + 1e-8);
    }

    private static Vector3f getRotation(JsonObject bone) {
        return new Vector3f(
                (float) Math.toRadians(bone.get("rotation").getAsJsonArray().get(0).getAsFloat()),
                (float) Math.toRadians(bone.get("rotation").getAsJsonArray().get(1).getAsFloat()),
                (float) Math.toRadians(bone.get("rotation").getAsJsonArray().get(2).getAsFloat())
        );
    }

}
