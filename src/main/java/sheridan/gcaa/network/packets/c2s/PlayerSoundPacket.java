package sheridan.gcaa.network.packets.c2s;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.IPacket;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.s2c.UpdateAttachmentScreenGuiContextPacket;
import sheridan.gcaa.sounds.ModSounds;

import java.util.function.Supplier;

public class PlayerSoundPacket implements IPacket<PlayerSoundPacket> {
    public String soundName;

    public PlayerSoundPacket() {

    }

    public PlayerSoundPacket(String soundName) {
        this.soundName = soundName;
    }

    @Override
    public void encode(PlayerSoundPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.soundName);
    }

    @Override
    public PlayerSoundPacket decode(FriendlyByteBuf buffer) {
        return new PlayerSoundPacket(buffer.readUtf());
    }

    @Override
    public void handle(PlayerSoundPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer player = supplier.get().getSender();
            if (player != null) {
                ModSounds.sound(1, 1, player, new ResourceLocation(message.soundName));
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
