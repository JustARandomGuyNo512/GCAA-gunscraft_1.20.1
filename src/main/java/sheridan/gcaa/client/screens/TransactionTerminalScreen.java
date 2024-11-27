package sheridan.gcaa.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.TransactionTerminalRequestPacket;

import java.util.ArrayList;
import java.util.List;
@OnlyIn(Dist.CLIENT)
public class TransactionTerminalScreen extends Screen {
    private List<Player> players;
    private int time = 0;
    private static final ResourceLocation BACKGROUND = new ResourceLocation(GCAA.MODID, "textures/gui/screen/transaction_terminal.png");

    public TransactionTerminalScreen() {
        super(Component.literal(""));
        this.width = 256;
        this.height = 185;
    }

    /**
     * @desciption 一坤秒 轮询一次玩家列表
     */
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
    protected void init() {
        super.init();
    }

    /**
     * @description 渲染界面UI
     * @param pGuiGraphics
     * @param pMouseX
     * @param pMouseY
     * @param pPartialTick
     */
    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderBackground(pGuiGraphics);
        int startX = (this.width - 256) / 2;
        int startY = (this.height - 185) / 2;
        pGuiGraphics.blit(BACKGROUND, startX, startY,  0,0, 256, 185, 256, 185);

    }
    private boolean checkPlayer() {
        return this.minecraft != null && this.minecraft.player != null;
    }
}
