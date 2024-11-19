package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.screens.components.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.VendingMachineMenu;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.ExchangePacket;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.IProduct;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VendingMachineScreen extends AbstractContainerScreen<VendingMachineMenu> {
    private static final ResourceLocation EXCHANGE_PNG = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/exchange.png");
    private static final ResourceLocation EXCHANGE_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/exchange.png");
    private String currentCategory = ProductsRegister.EXCHANGE;
    private long balance = 0;
    public SimpleContainer exchange;
    private Set<IProduct> currentProducts = ProductsRegister.getProducts(currentCategory);
    private final Map<Item, IProduct> currentProductsMap = new HashMap<>();
    private OptionalImageButton exchangeBtn;
    private boolean needUpdate = false;

    public VendingMachineScreen(VendingMachineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 256;
        this.height = 166;
        this.imageWidth = 256;
        this.imageHeight = 166;
        exchange = pMenu.exchange;
        refineProductsMap();
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(1);
        gridlayout.defaultCellSetting().padding(1, 1, 1, 1);
        exchangeBtn = new OptionalImageButton(this.leftPos + 120, this.topPos + 62, 16, 16, 0, 0, 0, EXCHANGE_BTN, 16, 16,  (btn) -> exchange());

        rowHelper.addChild(exchangeBtn);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int startX = (this.width - this.getXSize()) / 2;
        int startY = (this.height - this.getYSize()) / 2;
        if (this.minecraft != null) {
            Font font = this.minecraft.font;
            renderBalance(pGuiGraphics, font, startX, startY);
        }
    }

    private void refineProductsMap() {
        currentProductsMap.clear();
        for (IProduct product : currentProducts) {
            currentProductsMap.put(product.getItem(), product);
        }
    }

    private void exchange() {
        long val = getExchangeVal();
        if (val != 0) {
            PacketHandler.simpleChannel.sendToServer(new ExchangePacket(val));
            needUpdate = true;
        }
    }

    public void handleUpdate(long balance) {
        needUpdate = false;
        this.balance = balance;
        if (checkPlayer()) {
            PlayerStatusProvider.getStatus(this.minecraft.player).setBalance(balance);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (needUpdate) {
            return;
        }
        if (checkPlayer()) {
            Player player = this.minecraft.player;
            balance = PlayerStatusProvider.getStatus(player).getBalance();
            exchangeBtn.setPrevented(getExchangeVal() == 0);
        }
    }

    private void renderBalance(GuiGraphics pGuiGraphics, Font font, int sx, int sy) {
        String str = Component.translatable("tooltip.screen_info.balance").getString() + balance;
        pGuiGraphics.drawString(font, str, sx + 211 - font.width(str), sy + 5, 0x00ff00);
        String str2 = Component.translatable("tooltip.screen_info.worth").getString() + getExchangeVal();
        pGuiGraphics.drawString(font, str2, sx + 145, sy + 38, 0x00ff00);
    }

    private long getExchangeVal() {
        if (!exchange.isEmpty()) {
            ItemStack itemStack = exchange.getItem(0);
            IProduct product = currentProductsMap.get(itemStack.getItem());
            return product.getPrice(itemStack);
        }
        return 0;
    }

    private boolean checkPlayer() {
        return this.minecraft != null && this.minecraft.player != null;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {

    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        this.renderBackground(pGuiGraphics);
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            pGuiGraphics.blit(EXCHANGE_PNG, startX, startY,  0,0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());

        }
        if (needUpdate) {
            this.renderBackground(pGuiGraphics);
            RenderSystem.enableDepthTest();
            String text = Component.translatable("label.attachments_screen.wait_response").getString();
            Font font = Minecraft.getInstance().font;
            pGuiGraphics.drawString(font, text, (width - font.width(text)) / 2, height / 2, -1);
        }
    }


}
