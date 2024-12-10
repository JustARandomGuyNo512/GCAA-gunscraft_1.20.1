package sheridan.gcaa.network.packets.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class StopBulletCraftingPacket implements IPacket<StopBulletCraftingPacket> {
    private int x;
    private int y;
    private int z;

    public StopBulletCraftingPacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public StopBulletCraftingPacket() {
    }

    @Override
    public void encode(StopBulletCraftingPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    @Override
    public StopBulletCraftingPacket decode(FriendlyByteBuf buffer) {
        return new StopBulletCraftingPacket(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(StopBulletCraftingPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                BlockPos blockPos = new BlockPos(message.x, message.y, message.z);
                BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
                if (blockEntity instanceof BulletCraftingBlockEntity block) {
                    block.stopBulletCrafting();
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
