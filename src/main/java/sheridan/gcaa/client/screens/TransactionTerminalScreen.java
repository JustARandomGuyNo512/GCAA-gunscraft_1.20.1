package sheridan.gcaa.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.TransactionTerminalRequestPacket;

import java.util.ArrayList;
import java.util.List;
@OnlyIn(Dist.CLIENT)
public class TransactionTerminalScreen extends Screen {
    private List<Player> players;
    private int time = 0;
    public TransactionTerminalScreen() {
        super(Component.literal(""));
    }

    @Override
    public void tick() {
        super.tick();
        if (players == null) {
            players = new ArrayList<>();
            PacketHandler.simpleChannel.sendToServer(new TransactionTerminalRequestPacket());
            time = 0;
        }
        if (time++ > 50) {
            PacketHandler.simpleChannel.sendToServer(new TransactionTerminalRequestPacket());
            time = 0;
        }
    }

    /**
     * @param playerIds 所有玩家id除去自身
     * @description  用于 从服务端更新客户端数据
     */
    public void updateClientDataFromServer(List<Integer> playerIds) {
        if (checkPlayer()) {
            players.clear();
            for (int id : playerIds) {
                    Entity entity = this.minecraft.player.level().getEntity(id);
                    if (entity instanceof Player player) {
                        players.add(player);
                    }
            }
            System.out.println(players.size());
        }
    }
    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawString(font, "Transaction Terminal", 100, 100, 0xFFFFFF, false);
        this.renderBackground(pGuiGraphics);
    }
    private boolean checkPlayer() {
        return this.minecraft != null && this.minecraft.player != null;
    }
}
