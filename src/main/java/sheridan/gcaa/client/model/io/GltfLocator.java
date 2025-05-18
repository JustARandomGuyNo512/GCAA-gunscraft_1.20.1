package sheridan.gcaa.client.model.io;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;

import java.io.IOException;
import java.io.InputStream;

@OnlyIn(Dist.CLIENT)
public class GltfLocator implements AssetLocator {

    @Override
    public void setRootPath(String rootPath) {}

    @Override
    public AssetInfo locate(AssetManager manager, AssetKey key) {
        ResourceLocation path = new ResourceLocation(key.getName());
        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(path).orElse(null);
            if (resource == null) {
                System.err.println("找不到资源: " + path);
                return null;
            }

            InputStream stream = resource.open();

            return new AssetInfo(manager, key) {
                @Override
                public InputStream openStream() {
                    return stream;
                }
            };

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
