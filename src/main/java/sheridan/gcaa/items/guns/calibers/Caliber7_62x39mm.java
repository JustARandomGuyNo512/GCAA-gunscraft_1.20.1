package sheridan.gcaa.items.guns.calibers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunitions.IAmmunition;
import sheridan.gcaa.items.guns.ICaliber;
import sheridan.gcaa.items.guns.IGun;

public class Caliber7_62x39mm implements ICaliber {
    public static final Caliber7_62x39mm INSTANCE = new Caliber7_62x39mm();

    @Override
    public String getName() {
        return "7.62x39mm";
    }

    @Override
    public void fireBullet(IAmmunition ammunition, ItemStack ammunitionStack, IGun gun, Player player, ItemStack gunStack) {
//        ArrowItem arrowItem = (ArrowItem) Items.ARROW;
//        AbstractArrow arrow = arrowItem.createArrow(player.level(), ItemStack.EMPTY, player);
//        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4.2f, 1F);
//        arrow.setBaseDamage(arrow.getBaseDamage() + 5);
//        arrow.setPos(arrow.position().add(player.getLookAngle()));
//        player.level().addFreshEntity(arrow);
    }

    @Override
    public int getCost() {
        return 7;
    }
}
