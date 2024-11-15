package sheridan.gcaa.network.packets.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;
import sheridan.gcaa.Clients;
import sheridan.gcaa.network.IPacket;

import java.util.function.Supplier;

public class ClientHitBlockPacket implements IPacket<ClientHitBlockPacket> {
    public BlockPos blockPos;
    public Vector3f pos;
    public int directionIndex = -1;
    public int[] modsIndexList;

    public ClientHitBlockPacket() {}

    public ClientHitBlockPacket(BlockPos blockPos, Vec3 pos, Direction direction, int[] modsIndexList) {
        this.blockPos = blockPos;
        this.pos = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
        this.directionIndex = getIndex(direction);
        this.modsIndexList = modsIndexList;
    }

    @Override
    public void encode(ClientHitBlockPacket message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.blockPos);
        buffer.writeVector3f(message.pos);
        buffer.writeInt(message.directionIndex);
        buffer.writeVarIntArray(message.modsIndexList);
    }

    @Override
    public ClientHitBlockPacket decode(FriendlyByteBuf buffer) {
        ClientHitBlockPacket packet = new ClientHitBlockPacket();
        packet.blockPos = buffer.readBlockPos();
        packet.pos = buffer.readVector3f();
        packet.directionIndex = buffer.readInt();
        packet.modsIndexList = buffer.readVarIntArray();
        return packet;
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
    public void handle(ClientHitBlockPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    Clients.onProjectileHitBlock(message.blockPos, message.pos, message.directionIndex, message.modsIndexList));
                    //Clients.testPlayParticle(message.blockPos, message.pos, message.directionIndex, message.force));
        });
        supplier.get().setPacketHandled(true);
    }
}
