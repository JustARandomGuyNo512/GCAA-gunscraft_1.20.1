package sheridan.gcaa.client.model.gun.namingScript;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.gun.namingScript.scripts.AttachmentBoundVisible;
import sheridan.gcaa.client.model.gun.namingScript.scripts.ContainScopeVisible;
import sheridan.gcaa.client.model.gun.namingScript.scripts.PartBoundVisible;
import sheridan.gcaa.client.model.gun.namingScript.scripts.SlotBoundVisible;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScriptUnit {
    private static final List<IScript<?>> REGISTER = new ArrayList<>();
    private final List<Pair> scriptList;

    static {
        REGISTER.add(new SlotBoundVisible(""));
        REGISTER.add(new ContainScopeVisible());
        REGISTER.add(new AttachmentBoundVisible("", null));
        REGISTER.add(new PartBoundVisible(ModelPart.EMPTY, ModelPart.EMPTY));
    }

    public static void register(IScript<?> instanceForParsingScript) {
        REGISTER.add(instanceForParsingScript);
    }

    public ScriptUnit(String scripts, GunModel gunModel) {
        scriptList = new ArrayList<>();
        String[] split = scripts.split(",");
        for (String s : split) {
            boolean flag = s.startsWith("^");
            s = flag ? s.substring(1) : s;
            for (IScript<?> script : REGISTER) {
                Object parse = script.parse(s, gunModel);
                if (parse != null) {
                    scriptList.add(new Pair(!flag, (IScript<?>) parse));
                    break;
                }
            }
        }
    }

    public boolean isEmpty() {
        return scriptList.isEmpty();
    }

    public boolean value(GunRenderContext context, boolean lowQuality) {
        for (Pair pair : scriptList) {
            boolean value = lowQuality ? pair.script.valueLowQuality(context) :
                    pair.script.value(context);
            if (pair.flag) {
                value = !value;
            }
            if (value) {
                return true;
            }
        }
        return false;
    }

    private static class Pair {
        public Pair(boolean flag, IScript<?> script) {
            this.flag = flag;
            this.script = script;
        }

        public boolean flag;
        public IScript<?> script;
    }
}
