package sheridan.gcaa.client.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfig {
    private final static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue renderAttachmentsInGuiView;
    public static ForgeConfigSpec.BooleanValue renderAttachmentsInGroundView;

    static {
        BUILDER.comment("在渲染GUI中的枪械模型时是否要渲染配件，此项设置为false可降低性能消耗\nWhether to renderByModelSlotName attachments when rendering a gun model in the GUI. Setting this to false reduces performance costs");
        renderAttachmentsInGuiView = BUILDER.define("render_attachments_in_gui_view", true);
        BUILDER.comment("\n");
        BUILDER.comment("在渲染地面掉落物枪械模型时是否要渲染配件，此项设置为false可降低性能消耗\nWhether to renderByModelSlotName accessories when rendering ground drop gun models. Set this to false to reduce performance costs");
        renderAttachmentsInGroundView = BUILDER.define("render_attachments_in_ground_view", true);

        SPEC = BUILDER.build();
    }

}
