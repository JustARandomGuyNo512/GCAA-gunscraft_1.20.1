package sheridan.gcaa.network.packets.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class UpdateBulletCraftingRecipePacket implements IPacket<UpdateBulletCraftingRecipePacket> {
    public String string;

    public UpdateBulletCraftingRecipePacket(String string) {
        this.string = string;
    }

    public UpdateBulletCraftingRecipePacket() {}

    @Override
    public void encode(UpdateBulletCraftingRecipePacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.string);
    }

    @Override
    public UpdateBulletCraftingRecipePacket decode(FriendlyByteBuf buffer) {
        return new UpdateBulletCraftingRecipePacket(buffer.readUtf());
    }

    @Override
    public void handle(UpdateBulletCraftingRecipePacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                RecipeRegister.syncAmmunitionRecipeFromServer(message.string)
        ));
        supplier.get().setPacketHandled(true);
    }
}
