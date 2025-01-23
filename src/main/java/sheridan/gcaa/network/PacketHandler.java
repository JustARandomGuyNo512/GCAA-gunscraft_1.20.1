package sheridan.gcaa.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.packets.c2s.*;
import sheridan.gcaa.network.packets.s2c.*;

public class PacketHandler
{
    public static final String PROTOCOL_VERSION = GCAA.MODID + "1.0";
    public static SimpleChannel simpleChannel;
    private static int tempId;

    public static void register()
    {
        simpleChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(GCAA.MODID, "common"), ()-> PROTOCOL_VERSION,
                (s) ->true, (s) ->true);

        registerPacket(SyncPlayerStatusPacket.class, new SyncPlayerStatusPacket());
        registerPacket(BroadcastPlayerStatusPacket.class, new BroadcastPlayerStatusPacket());
        registerPacket(GunFirePacket.class, new GunFirePacket());
        registerPacket(SwitchFireModePacket.class, new SwitchFireModePacket());
        registerPacket(GunReloadPacket.class, new GunReloadPacket());
        registerPacket(OpenGunModifyScreenPacket.class, new OpenGunModifyScreenPacket());
        registerPacket(InstallAttachmentsPacket.class, new InstallAttachmentsPacket());
        registerPacket(UpdateGunModifyScreenGuiContextPacket.class, new UpdateGunModifyScreenGuiContextPacket());
        registerPacket(UninstallAttachmentPacket.class, new UninstallAttachmentPacket());
        registerPacket(SetEffectiveSightPacket.class, new SetEffectiveSightPacket());
        registerPacket(ClientSoundPacket.class, new ClientSoundPacket());
        registerPacket(PlayerSoundPacket.class, new PlayerSoundPacket());
        registerPacket(DoneHandActionPacket.class, new DoneHandActionPacket());
        registerPacket(SetScopeMagnificationPacket.class, new SetScopeMagnificationPacket());
        registerPacket(FireGrenadeLauncherPacket.class, new FireGrenadeLauncherPacket());
        registerPacket(GrenadeLauncherReloadPacket.class, new GrenadeLauncherReloadPacket());
        registerPacket(HeadShotFeedBackPacket.class, new HeadShotFeedBackPacket());
        registerPacket(TurnFlashlightPacket.class, new TurnFlashlightPacket());
        registerPacket(AmmunitionManagePacket.class, new AmmunitionManagePacket());
        registerPacket(ApplyAmmunitionModifyPacket.class, new ApplyAmmunitionModifyPacket());
        registerPacket(UpdateAmmunitionModifyScreenPacket.class, new UpdateAmmunitionModifyScreenPacket());
        registerPacket(ScreenBindAmmunitionPacket.class, new ScreenBindAmmunitionPacket());
        registerPacket(ClearGunAmmoPacket.class, new ClearGunAmmoPacket());
        registerPacket(ClientHitBlockPacket.class, new ClientHitBlockPacket());
        registerPacket(ExchangePacket.class, new ExchangePacket());
        registerPacket(UpdateVendingMachineScreenPacket.class, new UpdateVendingMachineScreenPacket());
        registerPacket(RequestDataSyncPacket.class, new RequestDataSyncPacket());
        registerPacket(BuyProductPacket.class, new BuyProductPacket());
        registerPacket(RecycleItemPacket.class, new RecycleItemPacket());
        registerPacket(TransactionTerminalRequestPacket.class, new TransactionTerminalRequestPacket());
        registerPacket(UpdateTransactionDataPacket.class, new UpdateTransactionDataPacket());
        registerPacket(TransferAccountsPacket.class, new TransferAccountsPacket());
        registerPacket(UpdateTransferBalancePacket.class, new UpdateTransferBalancePacket());
        registerPacket(SelectBulletCraftingPacket.class, new SelectBulletCraftingPacket());
        registerPacket(StopBulletCraftingPacket.class, new StopBulletCraftingPacket());
        registerPacket(UpdateGunPropertiesPacket.class, new UpdateGunPropertiesPacket());
        registerPacket(UpdateVendingMachineProductsPacket.class, new UpdateVendingMachineProductsPacket());
        registerPacket(UpdateBulletCraftingRecipePacket.class, new UpdateBulletCraftingRecipePacket());
    }

    private static <T> void registerPacket(Class<T> clazz, IPacket<T> message) {
        simpleChannel.registerMessage(tempId++, clazz, message::encode, message::decode, message::handle);
    }
}