package sheridan.gcaa.service.product;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public class GunProduct extends CommonProduct implements IRecycleProduct{
    public GunProduct(Gun gun, int price) {
        super(gun, price);
    }

    @Override
    public int getMaxBuyCount() {
        return 1;
    }

    public IGun getGun() {
        return ((Gun)getItem());
    }

    @Override
    public long getRecyclePrice(ItemStack gunStack, List<Component> tooltip) {
        long price = getPrice(gunStack);
        IGun gun = getGun();
        List<IAttachment> attachments = AttachmentsHandler.INSTANCE.getAttachments(gunStack, gun);
        for (IAttachment attachment : attachments) {
            AttachmentProduct attachmentProduct = AttachmentProduct.get(attachment.get());
            if (attachmentProduct != null) {
                price += attachmentProduct.getRecyclePrice(gunStack, tooltip);
            }
        }
        int ammoLeft = gun.getAmmoLeft(gunStack);
        if (ammoLeft > 0) {
            IAmmunition ammunition = gun.getGunProperties().caliber.ammunition;
            AmmunitionProduct ammunitionProduct = AmmunitionProduct.get(ammunition.get());
            if (ammunitionProduct != null) {

            }
            //CompoundTag ammunitionData = gun.getUsingAmmunitionData(gunStack);
        }
        return price;
    }
}
