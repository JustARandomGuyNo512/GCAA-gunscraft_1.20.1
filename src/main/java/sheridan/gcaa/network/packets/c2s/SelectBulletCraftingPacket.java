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

public class SelectBulletCraftingPacket implements IPacket<SelectBulletCraftingPacket> {
    private String id;
    private int x;
    private int y;
    private int z;

    public SelectBulletCraftingPacket( String id, int x, int y, int z ) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SelectBulletCraftingPacket() {
    }

    @Override
    public void encode(SelectBulletCraftingPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.id);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    @Override
    public SelectBulletCraftingPacket decode(FriendlyByteBuf buffer) {
        return new SelectBulletCraftingPacket(buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void handle(SelectBulletCraftingPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                BlockPos blockPos = new BlockPos(message.x, message.y, message.z);
                BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
                Item value = ForgeRegistries.ITEMS.getValue(new ResourceLocation(message.id));
                if (value instanceof Ammunition ammunition && blockEntity instanceof BulletCraftingBlockEntity block) {
                    block.setCraftingBullet(ammunition);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
