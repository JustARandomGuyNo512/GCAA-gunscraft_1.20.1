package sheridan.gcaa.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private final static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue renderAttachmentsInGuiView;
    public static ForgeConfigSpec.BooleanValue renderAttachmentsInGroundView;
    public static ForgeConfigSpec.BooleanValue renderVanillaModelInGroundView;
    public static ForgeConfigSpec.BooleanValue enableMuzzleFlashScaleModifyOnUsingScope;
    public static ForgeConfigSpec.BooleanValue renderLowQualityModelInTPView;
    public static ForgeConfigSpec.BooleanValue renderLowQualityModelInGroundView;
    public static ForgeConfigSpec.BooleanValue renderLowQualityModelInOtherView;
    public static ForgeConfigSpec.BooleanValue enableMuzzleFlashLighting;

    static {
        BUILDER.comment("在渲染GUI中的枪械模型时是否要渲染配件，此项设置为false可降低性能消耗\nWhether to renderByModelSlotName attachments when rendering a gun model in the GUI. Setting this to false reduces performance costs");
        renderAttachmentsInGuiView = BUILDER.define("render_attachments_in_gui_view", true);
        BUILDER.comment("\n");
        BUILDER.comment("在渲染地面掉落物枪械模型时是否要渲染配件，此项设置为false可降低性能消耗\nWhether to renderByModelSlotName accessories when rendering ground drop gun models. Set this to false to reduce performance costs");
        renderAttachmentsInGroundView = BUILDER.define("render_attachments_in_ground_view", true);
        BUILDER.comment("\n");
        BUILDER.comment("在渲染枪械掉落物时原版枪械模型，设置为true可能大幅提升性能\nWhen rendering gun item drops on ground use the vanilla gun model, setting to true may significantly improve performance");
        renderVanillaModelInGroundView = BUILDER.define("render_vanilla_model_in_ground_view", false);
        BUILDER.comment("\n");
        BUILDER.comment("渲染第一人称开镜视野时缩放枪口火焰尺寸以免遮挡视野\nZoom the muzzle flame size when rendering the first-person view to avoid blocking the view");
        enableMuzzleFlashScaleModifyOnUsingScope = BUILDER.define("enable_muzzle_flash_scale_modify_on_using_scope", true);
        BUILDER.comment("\n");
        BUILDER.comment("第三人称渲染时使用低质量模型（如果模型支持），这将极大提升多人情况下的渲染性能\nUsing a low-quality model for third-person rendering (if the model supports it) will greatly improve rendering performance in multiplayer situations");
        renderLowQualityModelInTPView = BUILDER.define("render_low_quality_model_third_person", true);
        BUILDER.comment("\n");
        BUILDER.comment("渲染地面掉落物时使用低质量模型（如果模型支持），这将极大提升大量枪械掉落物堆积情况下的渲染性能\nWhen rendering ground drop items, use a low-quality model (if the model supports it), which greatly improves rendering performance in cases of large accumulation of gun items");
        renderLowQualityModelInGroundView = BUILDER.define("render_low_quality_model_ground", false);
        BUILDER.comment("\n");
        BUILDER.comment("在第一人称渲染已经以上配置之外的情景下渲染低质量模型（如果模型支持），可以提升性能\nRendering low-quality models (if the model supports it) in situations where first-person rendering is already beyond the above configuration can improve performance");
        renderLowQualityModelInOtherView = BUILDER.define("render_low_quality_model_other", false);
        BUILDER.comment("\n");
        BUILDER.comment("在第一人称下开枪对环境的光照效果，实验性选项可能不稳定，可能造成低FPS\nThe lighting effect on the environment when shooting in first person, experimental options may be unstable and may result in low FPS");
        enableMuzzleFlashLighting = BUILDER.define("enable_muzzle_flash_lighting", false);
        SPEC = BUILDER.build();
    }

}
