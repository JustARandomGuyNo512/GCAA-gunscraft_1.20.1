package sheridan.gcaa.client.model.gun.namingScript.scripts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.gun.namingScript.IScript;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.items.attachments.Attachment;

@OnlyIn(Dist.CLIENT)
public record SlotBoundVisible(String slot) implements IScript<SlotBoundVisible> {

    @Override
    public boolean value(GunRenderContext context) {
        return context.has(slot);
    }

    @Override
    public boolean valueLowQuality(GunRenderContext context) {
        return context.has(slot);
    }

    @Override
    public SlotBoundVisible parse(String script, GunModel gunModel) {
        if (script.startsWith(".") || script.startsWith("s_")) {
            script = script.startsWith(".") ? script.substring(1) : script.substring(2);
            return new SlotBoundVisible(Attachment.getConstantNameField(script));
        }
        return null;
    }

}
