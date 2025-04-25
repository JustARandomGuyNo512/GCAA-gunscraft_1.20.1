package sheridan.gcaa.items.gun.guns;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.client.IReloadTask;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.SksReloadTask;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.GunProperties;
import sheridan.gcaa.items.gun.IGunFireMode;
import sheridan.gcaa.items.gun.calibers.Caliber;
import sheridan.gcaa.items.gun.fireModes.Semi;
import sheridan.gcaa.items.gun.propertyExtensions.SksReloadExtension;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.ClearGunAmmoPacket;
import sheridan.gcaa.sounds.ModSounds;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.util.List;

public class Sks extends Gun {
    protected SksReloadExtension extension = new SksReloadExtension(
            RenderAndMathUtils.secondsToTicks(1f),
            RenderAndMathUtils.secondsToTicks(0.35f),
            RenderAndMathUtils.secondsToTicks(0.9f),
            RenderAndMathUtils.secondsToTicks(1f),
            1,
            RenderAndMathUtils.secondsToTicks(0.4f)
    );
    private static final Caliber caliber =
            new Caliber(Caliber.CALIBER_762X39MM,10f, 6f, 7f, 11f, 0.85f)
                    .setAmmunition(ModItems.AMMO_7_62X39MM.get());

    public Sks() {
        super(new GunProperties(3.9f, 0.9f, 2.6f, 1.2f, 0.17f,
                3.2f, GunProperties.toRPM(500), 0, 0, 10,
                3.3f, 1f, 0.12f, 0.11f, 16f, List.of(Semi.SEMI),
                ModSounds.SKS_FIRE, ModSounds.SKS_FIRE_SUPPRESSED, caliber));
        this.getGunProperties().addExtension(extension);
    }

    @Override
    public void reload(ItemStack stack, Player player) {
        int num = extension.singleReloadNum;
        if (num > 0) {
            AmmunitionHandler.reloadFor(player, stack, this, num);
        }
    }

    @Override
    public boolean clientReload(ItemStack stack, Player player) {
        if (isNotUsingSelectedAmmo(stack)) {
            PacketHandler.simpleChannel.sendToServer(new ClearGunAmmoPacket());
            clearAmmo(stack, player);
        }
        return super.clientReload(stack, player);
    }

    @Override
    public IReloadTask getReloadingTask(ItemStack stack, Player player) {
        boolean empty = getAmmoLeft(stack) == 0;
        return new SksReloadTask(stack, this,
                empty ? extension.enterDelayEmpty : extension.enterDelay,
                extension.singleReloadLength,
                extension.exitDelay,
                Math.min((getMagSize(stack) - getAmmoLeft(stack)), AmmunitionHandler.getAmmunitionCount(stack, this, player)),
                extension.triggerReloadDelay);
    }

}
