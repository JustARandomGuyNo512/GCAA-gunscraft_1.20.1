package sheridan.gcaa.client.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private final static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue renderAttachmentsInGuiView;
    public static ForgeConfigSpec.BooleanValue renderAttachmentsInGroundView;
    public static ForgeConfigSpec.BooleanValue renderVanillaModelInGuiView;
    public static ForgeConfigSpec.BooleanValue renderVanillaModelInGroundView;
    public static ForgeConfigSpec.BooleanValue useDynamicWeaponLighting;
    public static ForgeConfigSpec.BooleanValue alwaysSlimArm;
    public static ForgeConfigSpec.BooleanValue enableMuzzleFlashScaleModifyOnUsingScope;

    static {
        BUILDER.comment("在渲染GUI中的枪械模型时是否要渲染配件，此项设置为false可降低性能消耗\nWhether to renderByModelSlotName attachments when rendering a gun model in the GUI. Setting this to false reduces performance costs");
        renderAttachmentsInGuiView = BUILDER.define("render_attachments_in_gui_view", true);
        BUILDER.comment("\n");
        BUILDER.comment("在渲染地面掉落物枪械模型时是否要渲染配件，此项设置为false可降低性能消耗\nWhether to renderByModelSlotName accessories when rendering ground drop gun models. Set this to false to reduce performance costs");
        renderAttachmentsInGroundView = BUILDER.define("render_attachments_in_ground_view", true);
        BUILDER.comment("\n");
        BUILDER.comment("在物品栏中渲染原版枪械模型，设置为true可能大幅提升性能\nRender the vanilla gun model in the inventory, setting it to true can greatly improve performance");
        renderVanillaModelInGuiView = BUILDER.define("render_vanilla_model_in_gui_view", false);
        BUILDER.comment("\n");
        BUILDER.comment("在渲染枪械掉落物时原版枪械模型，设置为true可能大幅提升性能\nWhen rendering gun item drops on ground use the vanilla gun model, setting to true may significantly improve performance");
        renderVanillaModelInGroundView = BUILDER.define("render_vanilla_model_in_ground_view", false);
        BUILDER.comment("\n");
        BUILDER.comment("渲染枪械模型时使用随枪口火焰变化地动态亮度\nUse dynamic brightness that varies with the muzzle flame when rendering a gun model");
        useDynamicWeaponLighting = BUILDER.define("use_dynamic_weapon_lighting", true);
        BUILDER.comment("\n");
        BUILDER.comment("渲染手臂时使用女性玩家模型的手臂尺寸，这是GCAA在开发时的配置，更改为true可获得更好视觉体验\nThe arm is rendered using the arm size of the female player model, which was configured by GCAA at the time of development. Change to true for a better visual experience");
        alwaysSlimArm = BUILDER.define("always_slim_arm", false);
        BUILDER.comment("\n");
        BUILDER.comment("渲染第一人称开镜视野时缩放枪口火焰尺寸以免遮挡视野\nZoom the muzzle flame size when rendering the first-person view to avoid blocking the view");
        enableMuzzleFlashScaleModifyOnUsingScope = BUILDER.define("enable_muzzle_flash_scale_modify_on_using_scope", true);
        SPEC = BUILDER.build();
    }

}
