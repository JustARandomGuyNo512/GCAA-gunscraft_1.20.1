package sheridan.gcaa.client.model.assets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import sheridan.gcaa.client.model.modelPart.LayerDefinition;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public class ModelLoader {
    private static final Gson GSON_INSTANCE = new Gson();

    public static LayerDefinition loadModelAsset(ResourceLocation location) {
        AtomicReference<LayerDefinition> layer = new AtomicReference<>();
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
                        layer.set(readJsonStr(json));
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
        return layer.get();
    }

    public static LayerDefinition readJsonStr(String json) {
        JsonObject jsonObject = GSON_INSTANCE.fromJson(json, JsonObject.class);
        handleDescription(jsonObject.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonObject("description"));
        return null;
    }

    private static void handleDescription(JsonObject jsonObject) {
        System.out.println("desc: " + jsonObject + "\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

}
