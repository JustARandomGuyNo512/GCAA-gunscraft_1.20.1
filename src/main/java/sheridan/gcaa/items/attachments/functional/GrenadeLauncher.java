package sheridan.gcaa.items.attachments.functional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IArmReplace;
import sheridan.gcaa.items.attachments.IInteractive;
import sheridan.gcaa.items.gun.IGun;

public class GrenadeLauncher extends Attachment implements IArmReplace, IInteractive {
    public static final String KEY_AMMO = "grenade_ammo";

    public static boolean hasGrenade(ItemStack stack, IGun gun) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        return tag.contains(KEY_AMMO) && tag.getBoolean(KEY_AMMO);
    }

    public static void reload(ItemStack stack, IGun gun) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        tag.putBoolean(KEY_AMMO, true);
    }

    @Override
    public boolean replaceArmRender(boolean mainHand) {
        return !mainHand;
    }

    @Override
    public int orderForArmRender(boolean mainHand) {
        return mainHand ? 0 : 2;
    }

    @Override
    public void onAttach(ItemStack stack, IGun gun, CompoundTag data) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        if (!tag.contains(KEY_AMMO)) {
            tag.putBoolean(KEY_AMMO, false);
        }
    }

    @Override
    public void onDetach(ItemStack stack, IGun gun, CompoundTag data) {
        CompoundTag tag = gun.getGun().checkAndGet(stack);
        tag.remove(KEY_AMMO);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void onMouseButton(int btn, int action, ItemStack stack, IGun gun, Player player) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onKeyPress(int key, int action, ItemStack stack, IGun gun, Player player) {

    }
}
