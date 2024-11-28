package sheridan.gcaa.client.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.TransactionTerminalRequestPacket;
import sheridan.gcaa.network.packets.c2s.TransferAccountsPacket;

import java.util.ArrayList;
import java.util.List;
@OnlyIn(Dist.CLIENT)
public class TransactionTerminalScreen extends Screen {
    private List<Player> players;
    private final List<Player> searchPlayers = new ArrayList<>();
    private List<Player> pagePlayers = new ArrayList<>();
    private Player selectedPlayer;
    private int time = 0;
    private EditBox searchBar;
    private EditBox moneyInput;
    private static final int pageSize = 8;
    private int currentPage = 0;
    private long balance;
    private long transferMoney;
    private static final ResourceLocation BACKGROUND = new ResourceLocation(GCAA.MODID, "textures/gui/screen/transaction_terminal.png");

    public TransactionTerminalScreen() {
        super(Component.literal(""));
        this.width = 256;
        this.height = 185;
    }

    /**
     * @description 一坤秒 轮询一次玩家列表
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
        updatePlayers();
       if (checkPlayer()) {
           Player player = this.minecraft.player;
           balance = PlayerStatusProvider.getStatus(player).getBalance();
       }
       if (checkSelectPlayer()) {
           moneyInput.setEditable(true);
           String value = moneyInput.getValue();
           if (value.matches("^\\d+.*") && !value.contains(".")) {
               value = value.replaceFirst("^(\\d+).*", "$1");
               // 不超过16位
               if (value.length() > 16) {
                   value = value.substring(0, 16);
               }
           } else {
               value = "";
           }
           moneyInput.setValue(value);
           if (value.isEmpty()) {
               transferMoney = 0;
           } else {
               transferMoney = Long.parseLong(value);
           }
       } else {
           moneyInput.setEditable(false);
           moneyInput.setValue("");
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
        }
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        gridlayout.defaultCellSetting().padding(4, 4, 4, 4);
        int leftPos = (this.width - 256) / 2;
        int topPos = (this.height - 185) / 2;
        searchBar = new EditBox(this.font, leftPos + 9, topPos + 5, 107, 12, Component.literal(""));
        searchBar.setBordered(true);
        searchBar.setFGColor(0x000000);
        rowHelper.addChild(searchBar);
        for (int i = 0; i < pageSize; i++) {
            rowHelper.addChild(new PlayerButton(leftPos + 15 , topPos + 26 + i * 17, (button) -> ((PlayerButton) button).onClick(), i));
        }
        // 上一页按钮
        Button lastPage = Button.builder(Component.literal("<"), (b) -> pageTurning(false)).size(14, 14).pos(leftPos + 9, topPos + 167).build();
        rowHelper.addChild(lastPage);
        // 下一页按钮
        Button nextPage = Button.builder(Component.literal(">"), (b) -> pageTurning(true)).size(14, 14).pos(leftPos + 102, topPos + 167).build();
        rowHelper.addChild(nextPage);
        // 金额输入框
        moneyInput = new EditBox(this.font, leftPos + 131, topPos + 75, 110, 12, Component.literal(""));
        moneyInput.setBordered(true);
        moneyInput.setFGColor(0x000000);
        rowHelper.addChild(moneyInput);
        // 转账按钮
        Button transferButton = Button.builder(Component.translatable("tooltip.screen_info.transfer_accounts"), (b) -> {
            transfer();
        }).size(110, 20).pos(leftPos + 131, topPos + 100).build();
        rowHelper.addChild(transferButton);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }
    /**
     * @description 渲染界面UI
     */
    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        int startX = (this.width - 256) / 2;
        int startY = (this.height - 185) / 2;
        int pageNum = (int) Math.ceil((double) searchPlayers.size() / pageSize);
        // 界面UI
        pGuiGraphics.blit(BACKGROUND, startX, startY,  0,0, 256, 185, 256, 185);
        // 页数
        pGuiGraphics.drawCenteredString(font, Component.literal((pageNum == 0 ? 0 : currentPage + 1) + "/" + (pageNum)), startX + 60, startY + 170, 0xffffff);
        // 余额
        String str = Component.translatable("tooltip.screen_info.balance").getString() + balance;
        pGuiGraphics.drawString(font, str, startX + 252 - font.width(str), startY + 5, 0x00ff00);
        // 转账文字
        String transferText = Component.translatable("tooltip.screen_info.transfer_accounts_to").getString();
        if (checkSelectPlayer()) {
            transferText += " " + selectedPlayer.getDisplayName().getString();
        }
        pGuiGraphics.drawString(font, transferText, startX + 131, startY + 45, 0xffffff);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    /**
     * @description 转账
     */
    private void transfer() {
        if (checkSelectPlayer() && transferMoney != 0 && balance >= transferMoney) {
            PacketHandler.simpleChannel.sendToServer(new TransferAccountsPacket(selectedPlayer.getUUID(), transferMoney));
            moneyInput.setValue("");
            transferMoney = 0;
        }
    }
    public void updateBalance(long balance) {
        this.balance = balance;
        if (checkPlayer()) {
            PlayerStatusProvider.getStatus(this.minecraft.player).setBalance(balance);
        }
    }
    /**
     * @description 玩家按钮
     */
    private class PlayerButton extends ImageButton {
        public int index;
        public PlayerButton(int pX, int pY, OnPress pOnPress, int index) {
            super(pX, pY, 100, 15, 0, 0, BACKGROUND, pOnPress);
            this.index = index;
        }
        // 渲染头像和名字
        @Override
        public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            AbstractClientPlayer player = getPlayer();
            if (player == null) return;
            // 头像
            ResourceLocation skinTextureLocation = player.getSkinTextureLocation();
            PlayerFaceRenderer.draw(pGuiGraphics, skinTextureLocation,  getX(), getY(), 14, true, false);
            // 名字
            pGuiGraphics.drawString(font, player.getDisplayName().getString(), getX() + 20, getY() + 4, 0xffffff);
        }
        public void onClick() {
            AbstractClientPlayer player = getPlayer();
            if (player == null) return;
            selectedPlayer = player;
        }
        public AbstractClientPlayer getPlayer() {
            if (index < pagePlayers.size()) {
                Player player = pagePlayers.get(index);
                return (AbstractClientPlayer) player;
            }
            return null;
        }
    }
    private void updatePlayers() {
        String value = searchBar.getValue();
        if (players == null || players.isEmpty()) return;
        searchPlayers.clear();
        for (Player player : players) {
            if (value.isEmpty() || player.getDisplayName().getString().contains(value)) {
                // 模糊搜索
                searchPlayers.add(player);
            }
        }
        pagePlayers = searchPlayers.subList(
                currentPage * pageSize,
                Math.min((currentPage + 1) * pageSize, searchPlayers.size())
        );
    }

    private void pageTurning(boolean isNextPage) {
        currentPage = isNextPage ? currentPage + 1 : currentPage - 1;
        currentPage = Mth.clamp(currentPage, 0, searchPlayers.size() / pageSize);

    }
    private boolean checkPlayer() {
        return this.minecraft != null && this.minecraft.player != null;
    }
    private boolean checkSelectPlayer() {
        return this.selectedPlayer != null;
    }
}
