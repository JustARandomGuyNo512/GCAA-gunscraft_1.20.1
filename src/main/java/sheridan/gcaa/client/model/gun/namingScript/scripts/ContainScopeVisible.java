package sheridan.gcaa.client.model.gun.namingScript.scripts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.gun.namingScript.IScript;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public class ContainScopeVisible implements IScript<ContainScopeVisible> {
    @Override
    public boolean value(GunRenderContext context) {
        return !context.notContainsScope();
    }

    @Override
    public boolean valueLowQuality(GunRenderContext context) {
        return !context.notContainsScope();
    }

    @Override
    public ContainScopeVisible parse(String script, GunModel gunModel) {
        return "SCOPES".equals(script) ? this : null;
    }
}
