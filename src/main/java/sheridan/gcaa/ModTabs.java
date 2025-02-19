package sheridan.gcaa;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import sheridan.gcaa.items.ModItems;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> MOD_TABS;
    public static final RegistryObject<CreativeModeTab> GUNS_TAB;
    public static final RegistryObject<CreativeModeTab> ATTACHMENTS_TAB;
    public static final RegistryObject<CreativeModeTab> AMMUNITION_TAB;
    /** 杂项物品注册 */
    public static final RegistryObject<CreativeModeTab> OTHER_TAB;
    static {
        MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GCAA.MODID);

        ATTACHMENTS_TAB = MOD_TABS.register("attachments", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.attachments"))
                .icon(() ->new ItemStack(ModItems.AK_SUPPRESSOR.get()))
                .displayItems((parameters, tab) -> {
                    tab.accept(ModItems.PISTOL_SUPPRESSOR.get());
                    tab.accept(ModItems.SMG_SUPPRESSOR.get());
                    tab.accept(ModItems.AK_SUPPRESSOR.get());
                    tab.accept(ModItems.AR_SUPPRESSOR.get());
                    tab.accept(ModItems.AK12_SUPPRESSOR.get());
                    tab.accept(ModItems.SHOTGUN_SUPPRESSOR.get());
                    tab.accept(ModItems.SNIPER_SUPPRESSOR.get());
                    tab.accept(ModItems.OSPREY_SUPPRESSOR.get());
                    tab.accept(ModItems.OSPREY_SMG_SUPPRESSOR.get());
                    tab.accept(ModItems.AK_COMPENSATOR.get());
                    tab.accept(ModItems.AR_COMPENSATOR.get());
                    tab.accept(ModItems.SMG_COMPENSATOR.get());
                    tab.accept(ModItems.DMR_COMPENSATOR.get());
                    tab.accept(ModItems.AK_IMPROVED_HANDGUARD.get());
                    tab.accept(ModItems.AK_RAIL_BRACKET.get());
                    tab.accept(ModItems.RAIL_CLAMP.get());
                    tab.accept(ModItems.AK_IMPROVED_DUST_COVER.get());
                    tab.accept(ModItems.MICRO_RED_DOT.get());
                    tab.accept(ModItems.RED_DOT.get());
                    tab.accept(ModItems.HOLOGRAPHIC.get());
                    tab.accept(ModItems.OKP_7_A.get());
                    tab.accept(ModItems.OKP_7_B.get());
                    tab.accept(ModItems.ACOG.get());
                    tab.accept(ModItems.ELCAN.get());
                    tab.accept(ModItems.SCOPE_X10.get());
                    tab.accept(ModItems.VERTICAL_GRIP.get());
                    tab.accept(ModItems.SLANT_GRIP.get());
                    tab.accept(ModItems.GP_25.get());
                    tab.accept(ModItems.M203.get());
                    tab.accept(ModItems.AR_GAS_BLOCK.get());
                    tab.accept(ModItems.AR_STOCK_TUBE.get());
                    tab.accept(ModItems.AR_RAILED_HANDGUARD.get());
                    tab.accept(ModItems.AR_LIGHT_HANDGUARD_SHORT.get());
                    tab.accept(ModItems.AR_LIGHT_HANDGUARD.get());
                    tab.accept(ModItems.MP5_RAIL_HANDGUARD.get());
                    tab.accept(ModItems.M249_RAILED_HANDGUARD.get());
                    tab.accept(ModItems.AR_EXTEND_MAG.get());
                    tab.accept(ModItems.MAG_SURE_FIRE_60.get());
                    tab.accept(ModItems.AK_EXTEND_MAG.get());
                    tab.accept(ModItems.EXP_MAG5_45X39.get());
                    tab.accept(ModItems.EXP_MAG7_62X51.get());
                    tab.accept(ModItems.GLOCK_EXTEND_MAG.get());
                    tab.accept(ModItems.VECTOR_45_EXTEND_MAG.get());
                    tab.accept(ModItems.EXP_MAG_45_STRAIGHT.get());
                    tab.accept(ModItems.EXP_MAG9X19.get());
                    tab.accept(ModItems.SNIPER_EXTEND_MAG.get());
                    tab.accept(ModItems.SHOTGUN_EXTEND_BAY.get());
                    tab.accept(ModItems.DRUM_9X19_ARC.get());
                    tab.accept(ModItems.DRUM_45_STRAIGHT.get());
                    tab.accept(ModItems.DRUM_5_45X39.get());
                    tab.accept(ModItems.DRUM_AK.get());
                    tab.accept(ModItems.DRUM_7_62X51.get());
                    tab.accept(ModItems.CTR_STOCK.get());
                    tab.accept(ModItems.UBR_STOCK.get());
                    tab.accept(ModItems.AK_TACTICAL_STOCK.get());
                    tab.accept(ModItems.MICRO_LASER_SIGHT.get());
                    tab.accept(ModItems.LASER_SIGHT.get());
                    tab.accept(ModItems.MICRO_FLASHLIGHT.get());
                    tab.accept(ModItems.FLASHLIGHT.get());
                    tab.accept(ModItems.HORIZONTAL_LASER_SIGHT.get());
                    tab.accept(ModItems.RAIL_PANEL.get());
                    tab.accept(ModItems.RAL_PANEL_SHORT.get());
                }).build());

        AMMUNITION_TAB = MOD_TABS.register("ammunition", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.ammunition"))
                .icon(() ->new ItemStack(ModItems.AMMO_9X19MM.get()))
                .displayItems((parameters, tab) -> {
                    tab.accept(ModItems.AMMO_9X19MM.get());
                    tab.accept(ModItems.AMMO_5_7X28MM.get());
                    tab.accept(ModItems.AMMO_45ACP.get());
                    tab.accept(ModItems.AMMO_5_56X45MM.get());
                    tab.accept(ModItems.AMMO_7_62X39MM.get());
                    tab.accept(ModItems.AMMO_5_45X39MM.get());
                    tab.accept(ModItems.AMMO_7_62X51MM.get());
                    tab.accept(ModItems.AMMO_338_LAPUA_MAGNUM.get());
                    tab.accept(ModItems.AMMO_12GAUGE.get());
                    tab.accept(ModItems.AMMO_357MAGNUM.get());
                    tab.accept(ModItems.AMMO_VOG_25.get());
                    tab.accept(ModItems.AMMO_M433.get());
                }).build());

        GUNS_TAB = MOD_TABS.register("guns", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.guns"))
                .icon(() ->new ItemStack(ModItems.G19.get()))
                .displayItems((parameters, tab) -> {
                    tab.accept(ModItems.G19.get());
                    tab.accept(ModItems.FN57.get());
                    tab.accept(ModItems.PYTHON_357.get());
                    tab.accept(ModItems.VECTOR_45.get());
                    tab.accept(ModItems.MP5.get());
                    tab.accept(ModItems.AKM.get());
                    tab.accept(ModItems.AK12.get());
                    tab.accept(ModItems.M4A1.get());
                    tab.accept(ModItems.MK47.get());
                    tab.accept(ModItems.AWP.get());
                    tab.accept(ModItems.FN_BALLISTA.get());
                    tab.accept(ModItems.HK_G28.get());
                    tab.accept(ModItems.M870.get());
                    tab.accept(ModItems.BERETTA_686.get());
                    tab.accept(ModItems.XM1014.get());
                    tab.accept(ModItems.M249.get());
                    tab.accept(ModItems.M60E4.get());
                }).build());

        OTHER_TAB = MOD_TABS.register("other", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.other"))
                .icon(() ->new ItemStack(ModItems.AMMUNITION_PROCESSOR.get()))
                .displayItems((parameters, tab) -> {
                    tab.accept(ModItems.AMMUNITION_PROCESSOR.get());
                    tab.accept(ModItems.VENDING_MACHINE.get());
                    tab.accept(ModItems.TRANSACTION_TERMINAL.get());
                    tab.accept(ModItems.BULLET_CRAFTING.get());
                    tab.accept(ModItems.THIN_COPPER_PLATE.get());
                    tab.accept(ModItems.ORE_LEAD.get());
                    tab.accept(ModItems.LEAD_INGOT.get());
                    tab.accept(ModItems.LEAD_BLOCK.get());
                    tab.accept(ModItems.LEAD_NUGGET.get());
                    tab.accept(ModItems.ORE_ASPHALT.get());
                    tab.accept(ModItems.PLASTIC.get());
                }).build());
    }
}
