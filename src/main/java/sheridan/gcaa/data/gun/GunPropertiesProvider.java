package sheridan.gcaa.data.gun;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.data.IDataPacketGen;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GunPropertiesProvider implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;
    public GunPropertiesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "gun_properties");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        for (IGun gun : Gun.getAllInstances()) {
            JsonObject object = new JsonObject();
            gun.getGunProperties().writeData(object);
            String name = gun.getGun().getDescriptionId().split("\\.")[2];
            ResourceLocation key = new ResourceLocation(GCAA.MODID, name);
            Path path = this.pathProvider.json(key);
            list.add(DataProvider.saveStable(pOutput, object, path));
        }
        JsonObject object = new JsonObject();
        genProtocol(object);
        list.add(DataProvider.saveStable(pOutput, object, pathProvider.file(new ResourceLocation(GCAA.MODID, "README"), "txt")));
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    private void genProtocol(JsonObject jsonObject) {
        JsonObject en = new JsonObject();
        jsonObject.addProperty("version", "emmm... I forgot, whatever");
        en.addProperty("#0", "Adding parameters for custom firearms can provide more fun. Reasonable data changes can better adapt mods to the game environment and personal preferences. However, before you start, GCAA needs to declare:");
        en.addProperty("#1", "These data will automatically send synchronization messages to players joining the server, which may incur network and performance costs.");
        en.addProperty("#2", "Data is almost never subject to security validation.");
        en.addProperty("#3", "Due to the lack of restrictions on data values, once you modify the data, GCAA will no longer guarantee the safety of runtime. Players are responsible for any resulting program vulnerabilities, errors, or crashes.");
        jsonObject.add("announcement", en);

        JsonObject zh = new JsonObject();
        jsonObject.addProperty("版本", "我忘了。。。随便吧");
        zh.addProperty("#0", "自定义枪械的一些参数能提供更多趣味，合理的更改数据能够使mod更好的适配游戏环境和个人喜好，但是在您动手之前GCAA需要先向您声明：");
        zh.addProperty("#1", "这些数据会自动向加入服务器的玩家发送同步消息，这可能会带来网络消耗和性能消耗");
        zh.addProperty("#2", "数据几乎不会经过安全验证");
        zh.addProperty("#3", "由于数据取值几乎不做限制，一旦您修改数据，GCAA将不再保证运行时的安全，由此可能产生的程序漏洞，错误或崩溃问题由玩家自行承担后果");
        jsonObject.add("声明", zh);
    }

    @Override
    public @NotNull String getName() {
        return GCAA.MODID + ": gun_properties";
    }
}
