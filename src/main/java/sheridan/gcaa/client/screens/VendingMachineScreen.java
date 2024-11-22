package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.gun.LodGunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.render.GunRenderer;
import sheridan.gcaa.client.screens.components.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.VendingMachineMenu;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.ExchangePacket;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.IProduct;

import java.util.*;

public class VendingMachineScreen extends AbstractContainerScreen<VendingMachineMenu> {
    private static final ResourceLocation EXCHANGE_PNG = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/exchange.png");
    private static final ResourceLocation GUNS = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/guns.png");
    private static final ResourceLocation COMMON = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/common.png");
    private static final ResourceLocation EXCHANGE_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/exchange.png");
    private static final ResourceLocation NEXT_PAGE = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/next_page.png");
    private static final ResourceLocation PREV_PAGE = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/prev_page.png");
    private static final ResourceLocation SCROLL_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/scroll_btn.png");
    private static final ResourceLocation SELECTED_PRODUCT = new ResourceLocation(GCAA.MODID, "textures/gui/component/selected_slot.png");
    private String currentCategory = ProductsRegister.EXCHANGE;
    private long balance = 0;
    public SimpleContainer exchange;
    private List<IProduct> currentProducts = new ArrayList<>(ProductsRegister.getProducts(currentCategory));
    private final Map<Item, IProduct> currentProductsMap = new HashMap<>();
    private OptionalImageButton exchangeBtn;
    private boolean needUpdate = false;
    private Category selectedCategory;
    private OptionalImageButton nextPageBtn;
    private OptionalImageButton prevPageBtn;
    private int pageIndex = 0;
    private VendingMachineMenu.ProductSlot selectedProduct;

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
        prevPageBtn = new OptionalImageButton(this.leftPos + 45, this.topPos + 10, 16, 16, 0, 0, 0, NEXT_PAGE, 16, 16,  (btn) -> switchPage(-1));
        rowHelper.addChild(prevPageBtn);
        nextPageBtn = new OptionalImageButton(this.leftPos + 116, this.topPos + 10, 16, 16, 0, 0, 0, PREV_PAGE, 16, 16,  (btn) -> switchPage(1));
        rowHelper.addChild(nextPageBtn);

        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 4, ProductsRegister.EXCHANGE));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 35, ProductsRegister.GUN));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 67, ProductsRegister.AMMUNITION));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 99, ProductsRegister.ATTACHMENT));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 131, ProductsRegister.OTHER));

        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    private void switchPage(int dist) {
        int pageCount = getPageCount();
        pageIndex = Mth.clamp(pageIndex + dist, 0, pageCount - 1);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        int startX = (this.width - this.getXSize()) / 2;
        int startY = (this.height - this.getYSize()) / 2;
        if (this.minecraft != null) {
            Font font = this.minecraft.font;
            if (ProductsRegister.EXCHANGE.equals(currentCategory)) {
                renderBalance(pGuiGraphics, font, startX, startY);
            } else {
                renderPageSign(pGuiGraphics, font);
            }
            if (selectedProduct != null) {
                pGuiGraphics.blit(SELECTED_PRODUCT, this.leftPos + selectedProduct.x - 3, this.topPos + selectedProduct.y - 3,
                        0,0, 22,22, 22, 22);
            }
        }
    }

    private void renderGun(GuiGraphics graphics, ItemStack itemStack) {
        if (itemStack.getItem() instanceof IGun gun) {
            IGunModel gunModel = GunModelRegister.getModel(gun);
            if (gunModel == null) {
                return;
            }
            PoseStack poseStackView = graphics.pose();
            poseStackView.pushPose();
            float seed = (System.currentTimeMillis() % 10000) / 10000f;
            poseStackView.translate(this.leftPos + 192, this.topPos + 58, 300);
            poseStackView.mulPose(new Quaternionf().rotateY((float) Math.toRadians(360 * seed)));
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            setupScissor(this.leftPos + 138, this.topPos + 32, 110, 52);
            Lighting.setupForEntityInInventory();
            RenderSystem.setShaderColor(1,1,1,1);
            GunRenderContext gunRenderContext =
                    new GunRenderContext(graphics.bufferSource(), poseStackView, itemStack, gun, ItemDisplayContext.GROUND, 15728880, 655360, false);
            LodGunModel.disableLowQuality(gunRenderContext);
            DisplayData displayData = GunModelRegister.getDisplayData(gun);
            if (displayData != null) {
                float[] floats = displayData.get(DisplayData.GROUND);
                poseStackView.scale(floats[6], floats[7], floats[8]);
                float scale = gun.isPistol() ? 100 : 44f;
                poseStackView.scale(-scale,scale,scale);
            }
            gunModel.render(gunRenderContext);
            graphics.bufferSource().endBatch();
            Lighting.setupFor3DItems();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            poseStackView.popPose();
        }
    }

    public static void setupScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        int scale = (int)mc.getWindow().getGuiScale();
        GL11.glScissor(
                x * scale,
                mc.getWindow().getHeight() - y * scale - height * scale,
                Math.max(0, width * scale),
                Math.max(0, height * scale
                ));
    }

    private void renderPageSign(GuiGraphics pGuiGraphics, Font font) {
        int pageNum = getPageCount();
        String str = (pageIndex + 1) + " / " + pageNum;
        pGuiGraphics.drawString(font, Component.literal(str), this.leftPos + 89 - font.width(str) / 2, this.topPos + 13, 0xffffff);
    }

    private int getPageCount() {
        return currentProducts.size() / 35 + (currentProducts.size() % 35 == 0 ? 0 : 1);
    }

    private void refineProductsMap() {
        currentProductsMap.clear();
        for (IProduct product : currentProducts) {
            currentProductsMap.put(product.getItem(), product);
        }
    }

    private List<IProduct> getPageProducts() {
        return currentProducts.subList(pageIndex * 35, Math.min((pageIndex + 1) * 35, currentProducts.size()));
    }

    private void exchange() {
        long val = getExchangeVal();
        if (val != 0) {
            PacketHandler.simpleChannel.sendToServer(new ExchangePacket(val));
            needUpdate = true;
            exchange.setItem(0, ItemStack.EMPTY);
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
            boolean isInExchange = ProductsRegister.EXCHANGE.equals(currentCategory);
            exchangeBtn.visible = isInExchange;
            exchangeBtn.setPrevented(getExchangeVal() == 0);
            for (VendingMachineMenu.HindSlot slot : menu.playerInventorySlots) {
                slot.active = isInExchange;
            }
            menu.exchangeSlot.active = isInExchange;

            for (VendingMachineMenu.ProductSlot slot : menu.productSlots) {
                slot.active = !isInExchange;
            }

            if (!isInExchange) {
                List<IProduct> products = getPageProducts();
                for (int i = 0; i < 35; i++) {
                    if (i < products.size()) {
                        menu.productSlots.get(i).set(products.get(i).getDisplayItem());
                    } else {
                        menu.productSlots.get(i).set(ItemStack.EMPTY);
                    }
                }
            }

            nextPageBtn.visible = !isInExchange;
            prevPageBtn.visible = !isInExchange;

        }
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if (pSlot instanceof VendingMachineMenu.ProductSlot productSlot){
            if (productSlot.active && productSlot.getItem() != ItemStack.EMPTY)  {
                selectedProduct = selectedProduct == productSlot ? null : productSlot;
                return;
            }
        }
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
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

    private ResourceLocation chooseBackground() {
        switch (currentCategory) {
            case ProductsRegister.EXCHANGE -> {return EXCHANGE_PNG;}
            case ProductsRegister.GUN -> {return GUNS;}
            default -> {return COMMON;}
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        this.renderBackground(pGuiGraphics);
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            pGuiGraphics.blit(chooseBackground(), startX, startY,  0,0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());
            if (ProductsRegister.GUN.equals(currentCategory) && selectedProduct != null) {
                renderGun(pGuiGraphics, selectedProduct.getItem());
            }
        }
        if (needUpdate) {
            this.renderBackground(pGuiGraphics);
            RenderSystem.enableDepthTest();
            String text = Component.translatable("label.attachments_screen.wait_response").getString();
            Font font = Minecraft.getInstance().font;
            pGuiGraphics.drawString(font, text, (width - font.width(text)) / 2, height / 2, -1);
        }
    }

    private class Category extends ImageButton {
        public static final ResourceLocation TAB = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/tab.png");
        public static final ResourceLocation TAB_SELECTED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/tab_selected.png");
        public boolean isSelected;
        public String name;

        public Category(int pX, int pY, String name) {
            super(pX, pY, 32, 28, 0, 0, 0, TAB_SELECTED, 32, 28, (button) -> {
                ((Category) button).click();
            });
            this.name = name;
            setTooltip(Tooltip.create(Component.translatable("tooltip.category." + name)));
            if (Objects.equals(currentCategory, this.name)) {
                this.click();
            }
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.renderFakeItem(new ItemStack(ProductsRegister.getIcon(name)),
                    this.getX() + getWidth() / 2 - 6,
                    this.getY() + getHeight() / 2 - 8
            );
        }

        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (isSelected) {
                super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            } else {
                if (isHovered) {
                    pGuiGraphics.setColor(27 / 255f, 161 / 255f, 226 / 255f, 0.6f);
                } else {
                    pGuiGraphics.setColor(0.5f, 0.5f, 0.5f, 1f);
                }
                this.renderTexture(pGuiGraphics, TAB, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
            }
            pGuiGraphics.setColor(1, 1, 1, 1);
        }

        public void click() {
            currentCategory = name;
            if (selectedCategory != null) {
                selectedCategory.isSelected = false;
                if (!Objects.equals(selectedCategory.name, this.name)) {
                    selectedProduct = null;
                }
            }
            isSelected = true;
            selectedCategory = this;
            currentProducts = new ArrayList<>(ProductsRegister.getProducts(name));
        }
    }
}
