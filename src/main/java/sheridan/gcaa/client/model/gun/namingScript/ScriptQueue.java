package sheridan.gcaa.client.model.gun.namingScript;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScriptQueue {
    private final List<ScriptUnit> scriptUnits;

    public ScriptQueue(String[] rawScripts, GunModel gunModel) {
        scriptUnits = new ArrayList<>();
        for (String s : rawScripts) {
            ScriptUnit scriptUnit = new ScriptUnit(s, gunModel);
            if (!scriptUnit.isEmpty()) {
                scriptUnits.add(scriptUnit);
            }
        }
    }

    public boolean isEmpty() {
        return scriptUnits.isEmpty();
    }

    public void process(ModelPart target, GunRenderContext context, boolean lowQuality) {
        for (ScriptUnit scriptUnit : scriptUnits) {
            if (!scriptUnit.value(context, lowQuality)) {
                target.visible = false;
                return;
            }
        }
        target.visible = true;
    }
}
