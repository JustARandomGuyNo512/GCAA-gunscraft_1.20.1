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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ModelLoader {
    private static final Gson GSON_INSTANCE = new Gson();

    /**
     * This method is not thread safe.
     *
     * The model should only contain and must contain one group named 'root' in the top layer and all others part should be children of the root part.
     * In simple words, the model structure should be a tree. Root is the root of the tree. and all others part are branches of the tree.
     *
     * Reads bedrock 1.12.2 format json model file, returns the layer definition of the model.
     * Call layer.get().bakeRoot().getChild("root") to get the root part of the model.
     */
    public static LayerDefinition loadModelAsset(ResourceLocation location) {
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
        boolean findRoot = false;
        Map<String, PartDefinition> bonesMap = null;
        Map<PartDefinition, JsonObject> jsonObjectMap = null;
        for (JsonElement element : bones) {
            JsonObject bone = element.getAsJsonObject();
            if ("root".equals(bone.get("name").getAsString())) {
                bonesMap = new HashMap<>();
                jsonObjectMap = new HashMap<>();
                Vector3f pivot = getPivot(bone);
                PartDefinition root;
                if (bone.has("rotation")) {
                    Vector3f rotation = getRotation(bone);
                    root = model.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offsetAndRotation(
                            -pivot.x, -pivot.y + 24.0F,
                            pivot.z, rotation.x, rotation.y, rotation.z));
                } else {
                    root = model.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(-pivot.x, -pivot.y + 24.0F, pivot.z));
                }
                bonesMap.put("root", root);
                jsonObjectMap.put(root, bone);
                findRoot = true;
                break;
            }
        }
        if (!findRoot) {
            throw new RuntimeException("Can't find necessary bone named 'root' in the top layer.");
        }
        Integer rIndex = 0;
        for (JsonElement element : bones) {
            JsonObject bone = element.getAsJsonObject();
            String name = bone.get("name").getAsString();
            if ("root".equals(name)) {
                continue;
            }
            String parent = getParentName(bone);
            if (parent == null) {
                throw new RuntimeException("The bone named '" + name + "' has no parent.");
            } else {
                PartDefinition parentBone = bonesMap.get(parent);
                if (parentBone == null) {
                    throw new RuntimeException("Can't find parent of the bone named " + name);
                } else {
                    JsonObject parentJson = jsonObjectMap.get(parentBone);
                    if (parentJson == null) {
                        throw new RuntimeException("Can't find parent json of the bone named " + name);
                    }
                    Vector3f parentPivot = getPivot(parentJson);
                    Vector3f pivot = getPivot(bone);
                    PartDefinition boneDefinition;
                    CubeListBuilder cubeListBuilder = CubeListBuilder.create();
                    boolean hasCubes = bone.has("cubes");
                    JsonArray cubes = hasCubes ? bone.get("cubes").getAsJsonArray() : null;
                    hasCubes = hasCubes && !cubes.isEmpty();
                    if (hasCubes) {
                        handleCubesNonRotate(cubeListBuilder, cubes, parentPivot);
                    }
                    if (bone.has("rotation")) {
                        Vector3f rotation = getRotation(bone);
                        boneDefinition = parentBone.addOrReplaceChild(name, cubeListBuilder, PartPose.offsetAndRotation(
                                -pivot.x, -pivot.y, pivot.z,
                                rotation.x, rotation.y, rotation.z));
                    } else {
                        boneDefinition = parentBone.addOrReplaceChild(name, cubeListBuilder, PartPose.offset(-pivot.x, -pivot.y, pivot.z));
                    }
                    if (hasCubes && !cubes.isEmpty()) {
                        handleRotateSubCubes(boneDefinition, cubes, parentPivot, rIndex);
                    }
                    bonesMap.put(name, boneDefinition);
                    jsonObjectMap.put(boneDefinition, bone);
                }
            }
        }
    }

    private static void handleRotateSubCubes(PartDefinition mainBone, JsonArray cubes, Vector3f parentPivot, Integer rIndex) {
        for (JsonElement element : cubes) {
            JsonObject cube = element.getAsJsonObject();
            if (hasRotation(cube)) {
                Set<ModelPart.UvPolygon> faces = getFaces(cube.getAsJsonObject("uv"));
                if (faces.isEmpty()) {
                    continue;
                }
                Vector3f origin = getAsVec3(cube, "origin");
                Vector3f size = getAsVec3(cube, "size");
                Vector3f pivot = getAsVec3(cube, "pivot");
                float originX = origin.x - pivot.x;
                float originY = pivot.y - (size.y + origin.y);
                float originZ = pivot.z - origin.z;
                float offsetX = pivot.x + parentPivot.x;
                float offsetY = -pivot.y + parentPivot.y;
                float offsetZ = pivot.z - parentPivot.z;
                Vector3f offsetRotation = getRotation(cube);
                mainBone.addOrReplaceChild("r" + rIndex, CubeListBuilder.create().addBox(faces, originX, originY, originZ, size.x, size.y, size.z),
                        PartPose.offsetAndRotation(offsetX, offsetY, offsetZ, offsetRotation.x, offsetRotation.y, offsetRotation.z));
            }
        }
    }

    private static void handleCubesNonRotate(CubeListBuilder cubeListBuilder, JsonArray cubes, Vector3f parentPivot) {
        for (JsonElement element : cubes) {
            JsonObject cube = element.getAsJsonObject();
            if (!hasRotation(cube)) {
                Set<ModelPart.UvPolygon> faces = getFaces(cube.getAsJsonObject("uv"));
                if (!faces.isEmpty()) {
                    Vector3f origin = getAsVec3(cube, "origin");
                    Vector3f size = getAsVec3(cube, "size");
                    float originX = origin.x;
                    float originY = -origin.y - size.y + parentPivot.y;
                    float originZ = origin.z - parentPivot.z;
                    cubeListBuilder.addBox(faces, originX, originY, originZ, size.x, size.y, size.z);
                }
            }
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
            return vector3f.x == 0.0F && vector3f.y == 0.0F && vector3f.z == 0.0F;
        }
        return false;
    }

    private static Vector3f getRotation(JsonObject bone) {
        return new Vector3f(
                (float) Math.toRadians(bone.get("rotation").getAsJsonArray().get(0).getAsFloat()),
                (float) Math.toRadians(bone.get("rotation").getAsJsonArray().get(1).getAsFloat()),
                (float) Math.toRadians(bone.get("rotation").getAsJsonArray().get(2).getAsFloat())
        );
    }

    private static String getParentName(JsonObject bone) {
        return bone.has("parent") ? bone.get("parent").getAsString() : null;
    }

}
