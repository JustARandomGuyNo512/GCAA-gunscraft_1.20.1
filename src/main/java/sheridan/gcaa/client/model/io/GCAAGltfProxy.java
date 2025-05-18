package sheridan.gcaa.client.model.io;

import com.jme3.material.Material;
import com.jme3.scene.plugins.gltf.GltfLoader;

import java.lang.reflect.Field;

public class GCAAGltfProxy extends GltfLoader {

    public GCAAGltfProxy(){
        super();
        try {
            Field field = GltfLoader.class.getDeclaredField("defaultMat");
            field.setAccessible(true);
            field.set(this, new Material());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Material readMaterial(int materialIndex) {

        return new Material();
    }
}

