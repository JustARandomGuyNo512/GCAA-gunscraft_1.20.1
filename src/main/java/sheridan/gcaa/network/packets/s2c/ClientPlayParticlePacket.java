package sheridan.gcaa.network.packets.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class ClientPlayParticlePacket implements IPacket<ClientPlayParticlePacket> {
    public BlockPos blockPos;
    public Vec3 pos;
    public int directionIndex = -1;
    public int force;

    public ClientPlayParticlePacket() {
    }


    public ClientPlayParticlePacket(BlockPos blockPos, Vec3 pos, Direction direction, int force) {
        this.blockPos = blockPos;
        this.pos = pos;
        this.directionIndex = getIndex(direction);
        this.force = force;
    }

    private int getIndex(Direction direction) {
        return switch (direction) {
            case UP -> 0;
            case DOWN -> 1;
            case SOUTH -> 2;
            case NORTH -> 3;
            case WEST -> 4;
            case EAST -> 5;
        };
    }

    @Override
    public void encode(ClientPlayParticlePacket message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.blockPos);
        buffer.writeDouble(message.pos.x);
        buffer.writeDouble(message.pos.y);
        buffer.writeDouble(message.pos.z);
        buffer.writeInt(message.directionIndex);
        buffer.writeInt(message.force);
    }

    @Override
    public ClientPlayParticlePacket decode(FriendlyByteBuf buffer) {
        ClientPlayParticlePacket particlePacket = new ClientPlayParticlePacket();
        particlePacket.blockPos = buffer.readBlockPos();
        particlePacket.pos = new Vec3(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
        particlePacket.directionIndex = buffer.readInt();
        particlePacket.force = buffer.readInt();
        return particlePacket;
    }

    @Override
    public void handle(ClientPlayParticlePacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    Clients.testPlayParticle(message.blockPos, message.pos, message.directionIndex, message.force));
        });
        supplier.get().setPacketHandled(true);
    }
}
