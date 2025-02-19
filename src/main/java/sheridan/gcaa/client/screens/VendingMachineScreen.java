package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.registry.GunModelRegister;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.client.render.GunRenderContext;
import sheridan.gcaa.client.screens.components.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.VendingMachineMenu;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.BuyProductPacket;
import sheridan.gcaa.network.packets.c2s.ExchangePacket;
import sheridan.gcaa.network.packets.c2s.RecycleItemPacket;
import sheridan.gcaa.service.ProductsRegister;
import sheridan.gcaa.service.product.GunProduct;
import sheridan.gcaa.service.product.IProduct;
import sheridan.gcaa.service.product.IRecycleProduct;
import sheridan.gcaa.sounds.ModSounds;

import java.awt.*;
import java.util.*;
import java.util.List;

public class VendingMachineScreen extends AbstractContainerScreen<VendingMachineMenu> {
    private static final ResourceLocation EXCHANGE_PNG = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/exchange.png");
    private static final ResourceLocation GUNS = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/guns.png");
    private static final ResourceLocation COMMON = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/common.png");
    private static final ResourceLocation EXCHANGE_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/component/exchange.png");
    private static final ResourceLocation NEXT_PAGE = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/next_page.png");
    private static final ResourceLocation PREV_PAGE = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/prev_page.png");
    private static final ResourceLocation SCROLL_BTN = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/scroll_btn.png");
    private static final ResourceLocation SELECTED_PRODUCT = new ResourceLocation(GCAA.MODID, "textures/gui/component/selected_slot.png");
    private static final ResourceLocation SUITABLE_SLOT_MARK = new ResourceLocation(GCAA.MODID, "textures/gui/component/suitable_slot_mark.png");
    private static final ResourceLocation BUY = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/buy.png");
    private static final ResourceLocation RECYCLE = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/recycle_screen.png");
    private static final ResourceLocation RECYCLE_TAB = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/recycle.png");
    private static final ResourceLocation RECYCLE_TAB_SELECTED = new ResourceLocation(GCAA.MODID, "textures/gui/screen/vending_machine/recycle_selected.png");
    private String currentCategory = ProductsRegister.EXCHANGE;
    private long balance = 0;
    public SimpleContainer exchange;
    private List<IProduct> currentProducts = new ArrayList<>(ProductsRegister.getProducts(currentCategory));
    private final Map<Item, IProduct> exchangeProductsMap = new HashMap<>();
    private final Map<Item, IRecycleProduct> recycleProductsMap = new HashMap<>();
    private OptionalImageButton exchangeBtn;
    private boolean needUpdate = false;
    private Category selectedCategory;
    private OptionalImageButton nextPageBtn;
    private OptionalImageButton prevPageBtn;
    private OptionalImageButton buyBtn;
    private ScrollBtn scrollBtn;
    private int pageIndex = 0;
    private VendingMachineMenu.ProductSlot selectedProduct;
    private int maxBuyCount = 1;
    private int minBuyCount = maxBuyCount;
    private Category recycle;
    private OptionalImageButton recycleBtn;
    private Button detailBtn;
    private int recycleTick = 0;
    private long recyclePrice = 0;

    public VendingMachineScreen(VendingMachineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 256;
        this.height = 166;
        this.imageWidth = 256;
        this.imageHeight = 166;
        exchange = pMenu.exchange;
        refineExchangeProducts();
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
        recycleBtn = new OptionalImageButton(this.leftPos + 120, this.topPos + 62, 16, 16, 0, 0, 0, BUY, 16, 16,  (btn) -> {});
        rowHelper.addChild(recycleBtn);
        recycleBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.long_press_to_accept")));
        recycleBtn.visible = false;
        prevPageBtn = new OptionalImageButton(this.leftPos + 45, this.topPos + 10, 16, 16, 0, 0, 0, NEXT_PAGE, 16, 16,  (btn) -> switchPage(-1));
        rowHelper.addChild(prevPageBtn);
        prevPageBtn.visible = false;
        nextPageBtn = new OptionalImageButton(this.leftPos + 116, this.topPos + 10, 16, 16, 0, 0, 0, PREV_PAGE, 16, 16,  (btn) -> switchPage(1));
        rowHelper.addChild(nextPageBtn);
        nextPageBtn.visible = false;
        scrollBtn = new ScrollBtn(this.leftPos + 140, this.topPos + 106, 15, 12, 90, SCROLL_BTN, 15, 12);
        rowHelper.addChild(scrollBtn);
        scrollBtn.visible = false;
        detailBtn = Button.builder(Component.literal("?"), (b)->{}).size(16, 16).pos(this.leftPos + 140, this.topPos + 62).build();
        rowHelper.addChild(detailBtn);
        detailBtn.visible = false;
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 4, ProductsRegister.EXCHANGE));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 35, ProductsRegister.GUN));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 67, ProductsRegister.AMMUNITION));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 99, ProductsRegister.ATTACHMENT));
        rowHelper.addChild(new Category(this.leftPos + 11, this.topPos + 131, ProductsRegister.OTHER));
        recycle = new Category(this.leftPos + 213, this.topPos + 131, ProductsRegister.RECYCLE)
                .overrideTexture(RECYCLE_TAB, RECYCLE_TAB_SELECTED);
        rowHelper.addChild(recycle);

        buyBtn = new OptionalImageButton(this.leftPos + 185, this.topPos + 140, 16, 16, 0, 0, 0, BUY, 16, 16,  (btn) -> buy());
        rowHelper.addChild(buyBtn);
        buyBtn.visible = false;

        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    private void buy() {
        if (needUpdate) {
            return;
        }
        if (!ProductsRegister.EXCHANGE.equals(currentCategory) && selectedProduct != null) {
            IProduct product = selectedProduct.product;
            if (product == null) {
                return;
            }
            ItemStack stack = menu.productDisplaySlot.getItem();
            if (stack.getItem() == Items.AIR) {
                return;
            }
            int price = product.getPrice(stack);
            if (price > 0 && price <= balance) {
                PacketHandler.simpleChannel.sendToServer(new BuyProductPacket(stack, ProductsRegister.getId(selectedProduct.product)));
                needUpdate = true;
            }
        }
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
            if (ProductsRegister.GUN.equals(currentCategory) && selectedProduct != null) {
                if (selectedProduct.product instanceof GunProduct gunProduct) {
                    Font font = this.minecraft.font;
                    List<FormattedCharSequence> sequences = font.split(Component.literal(gunProduct.getIntroduction()), 112);
                    int lines = Math.min(sequences.size(), 55 / font.lineHeight);
                    for (int i = 0; i < lines; i ++) {
                        pGuiGraphics.drawString(font, sequences.get(i), this.leftPos + 137, this.topPos + 102 + i * font.lineHeight, Color.CYAN.getRGB());
                    }
                }
            }
            if (ProductsRegister.EXCHANGE.equals(currentCategory)) {
                renderBalance(pGuiGraphics, font, startX, startY);
                renderSuitableItemMark(pGuiGraphics);
            } else {
                renderPageSign(pGuiGraphics, font);
                String str = Component.translatable("tooltip.screen_info.balance").getString() + balance;
                pGuiGraphics.drawString(font, str, this.leftPos +
                        (ProductsRegister.RECYCLE.equals(currentCategory) ? 211 : 248)
                        - font.width(str), this.topPos + 4, 0x00ff00);
            }
            if (ProductsRegister.RECYCLE.equals(currentCategory)) {
                renderSuitableItemMark(pGuiGraphics);
                renderRecycleProgress(pGuiGraphics, font);
                String str2 = Component.translatable("tooltip.screen_info.worth").getString() + recyclePrice;
                pGuiGraphics.drawString(font, str2, startX + 145, startY + 38, 0x00ff00);
            }
            if (selectedProduct != null && !ProductsRegister.RECYCLE.equals(currentCategory)) {
                pGuiGraphics.blit(SELECTED_PRODUCT, this.leftPos + selectedProduct.x - 3, this.topPos + selectedProduct.y - 3,
                        0,0, 22,22, 22, 22);
                IProduct product = selectedProduct.product;
                ItemStack stack = menu.productDisplaySlot.getItem();
                if (product != null && !stack.isEmpty() && !ProductsRegister.EXCHANGE.equals(currentCategory)) {
                    int price = product.getPrice(stack);
                    int color = balance >= price ? 0x00ff00 : 0xff0000;
                    String str2 = Component.translatable("tooltip.screen_info.worth").getString() + price;
                    if (ProductsRegister.GUN.equals(currentCategory)) {
                        pGuiGraphics.drawString(font, str2, this.leftPos + 137, this.topPos + 88, color);
                    } else {
                        pGuiGraphics.drawString(font, str2,
                                this.leftPos + 193 - font.width(str2) / 2,
                                this.topPos + 87, color);
                    }
                }
            }
        }

    }

    private void renderRecycleProgress(GuiGraphics graphics, Font font) {
        int w1 = font.width(">");
        int w2 = font.width(".");
        int startX = this.leftPos + 48;
        float progress = recycleTick / 40f;
        int count1 = (int) (progress * 161 / w1);
        int count2 = (int) ((1 - progress) * 161 / w2);

        String builder2 = ".".repeat(Math.max(0, count2));
        graphics.drawString(font, ">".repeat(Math.max(0, count1)), startX, this.topPos + 20, getColor(progress));
        graphics.drawString(font, builder2, startX + 161 - font.width(builder2), this.topPos + 20, getColor(progress));
    }

    private int getColor(float progress) {
        int red = Math.round(progress * 255);
        int green = Math.round((1 - progress) * 255);
        int blue = 0;
        return (255 << 24) | (red << 16) | (green << 8) | blue;
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
            GunModel.disableLowQuality(gunRenderContext);
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
        if (ProductsRegister.RECYCLE.equals(currentCategory)) {
            return;
        }
        int pageNum = getPageCount();
        String str = (pageIndex + 1) + " / " + pageNum;
        pGuiGraphics.drawString(font, Component.literal(str), this.leftPos + 89 - font.width(str) / 2, this.topPos + 13, 0xffffff);
    }

    private int getPageCount() {
        return currentProducts.size() / 35 + (currentProducts.size() % 35 == 0 ? 0 : 1);
    }

    private void refineExchangeProducts() {
        exchangeProductsMap.clear();
        for (IProduct product : currentProducts) {
            exchangeProductsMap.put(product.getItem(), product);
        }
    }

    private void refineRecycleProducts() {
        recycleProductsMap.clear();
        for (IProduct product : currentProducts) {
            if (product instanceof IRecycleProduct recycleProduct) {
                recycleProductsMap.put(product.getItem(), recycleProduct);
            }
        }
    }

    private List<IProduct> getPageProducts() {
        try {
            return currentProducts.subList(pageIndex * 35, Math.min((pageIndex + 1) * 35, currentProducts.size()));
        } catch (Exception ignore) {
            return new ArrayList<>();
        }
    }

    private void exchange() {
        if (needUpdate) {
            return;
        }
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
            ModSounds.sound(1, 1, this.minecraft.player, ModSounds.DEAL.get());
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
            boolean isInRecycle = ProductsRegister.RECYCLE.equals(currentCategory);
            recycleBtn.visible = isInRecycle;
            detailBtn.visible = isInRecycle && menu.recycleSlot.getItem() != ItemStack.EMPTY;
            recycleBtn.setPrevented(menu.recycleSlot.getItem() == ItemStack.EMPTY);
            if (!recycleBtn.isPrevented() && isInRecycle && recycleBtn.isMouseDown()) {
                recycleTick = Math.min(40, recycleTick + 1);
                if (recycleTick == 40) {
                    recycle();
                    recycleBtn.mouseReleased(0, 0, 0);
                }
            } else {
                recycleTick = 0;
            }
            if (detailBtn.visible) {
                IRecycleProduct product = recycleProductsMap.get(menu.recycleSlot.getItem().getItem());
                if (product != null) {
                    List<Component> tooltips = new ArrayList<>();
                    long recyclePrice = product.getRecyclePrice(menu.recycleSlot.getItem(), tooltips);
                    MutableComponent translatable = Component.translatable("tooltip.screen_info.recycle_detail").append("\n");
                    for (Component component: tooltips) {
                        translatable.append(component).append("\n");
                    }
                    long finalPrice = (long) (recyclePrice * IRecycleProduct.RECYCLE_PRICE_RATE);
                    translatable.append(Component.translatable("tooltip.screen_info.final_worth").getString()
                            + " " + recyclePrice + " x " + IRecycleProduct.RECYCLE_PRICE_RATE + " = " + finalPrice);
                    detailBtn.setTooltip(Tooltip.create(translatable));
                    this.recyclePrice = finalPrice;
                }
            } else {
                recyclePrice = 0;
            }
            exchangeBtn.visible = isInExchange;
            exchangeBtn.setPrevented(getExchangeVal() == 0);
            for (VendingMachineMenu.HindSlot slot : menu.playerInventorySlots) {
                slot.active = isInExchange || isInRecycle;
            }
            menu.exchangeSlot.active = isInExchange;
            menu.recycleSlot.active = isInRecycle;

            for (VendingMachineMenu.ProductSlot slot : menu.productSlots) {
                slot.active = !isInExchange && !isInRecycle;
            }

            if (!isInExchange && !isInRecycle) {
                List<IProduct> products = getPageProducts();
                for (int i = 0; i < 35; i++) {
                    if (i < products.size()) {
                        menu.productSlots.get(i).set(products.get(i).getDisplayItem());
                        menu.productSlots.get(i).product = products.get(i);
                    } else {
                        menu.productSlots.get(i).set(ItemStack.EMPTY);
                        menu.productSlots.get(i).product = null;
                    }
                }
                if (ProductsRegister.GUN.equals(currentCategory)) {
                    buyBtn.setPosition(this.leftPos + 233, this.topPos + 85);
                } else {
                    buyBtn.setPosition(this.leftPos + 185, this.topPos + 140);
                }
                recycle.setPosition(this.leftPos + 253, recycle.getY());
            } else {
                recycle.setPosition(this.leftPos + 213, recycle.getY());
            }

            nextPageBtn.visible = !isInExchange && !isInRecycle;
            prevPageBtn.visible = !isInExchange && !isInRecycle;
            buyBtn.visible = !isInExchange && !isInRecycle;

            boolean commonPage = !isInExchange && !ProductsRegister.GUN.equals(currentCategory) && !isInRecycle;
            scrollBtn.visible = commonPage;
            menu.productDisplaySlot.active = commonPage;

            boolean banScroll;
            if (selectedProduct != null) {
                IProduct product = selectedProduct.product;
                if (product != null) {
                    maxBuyCount = product.getMaxBuyCount();
                    minBuyCount = product.getMinBuyCount();
                    int buyCount = calculateBuyCount(scrollBtn.progress);
                    menu.productDisplaySlot.set(product.getItemStack(buyCount));
                    banScroll = maxBuyCount <= 1;
                    ItemStack stack = menu.productDisplaySlot.getItem();
                    if (stack.getItem() != Items.AIR) {
                        int price = product.getPrice(stack);
                        buyBtn.setPrevented(balance < price);
                    }
                } else {
                    banScroll = true;
                    buyBtn.setPrevented(true);
                }
            } else {
                banScroll = true;
                buyBtn.setPrevented(true);
            }
            if (banScroll) {
                scrollBtn.reset();
                scrollBtn.setPrevented(true);
            } else {
                scrollBtn.setPrevented(false);
            }

        }
    }

    private int calculateBuyCount(float progress) {
        int target = Math.round(minBuyCount + (maxBuyCount - minBuyCount) * progress);
        int adjustedTarget = (target / minBuyCount) * minBuyCount;
        return Mth.clamp(adjustedTarget, minBuyCount, maxBuyCount);
    }

    @Override
    protected void slotClicked(@NotNull Slot pSlot, int pSlotId, int pMouseButton, @NotNull ClickType pType) {
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

    private void renderSuitableItemMark(GuiGraphics pGuiGraphics) {
        RenderSystem.enableBlend();
        float alphaTick = (System.currentTimeMillis() % 1000) * 0.001f;
        RenderSystem.setShaderColor(1, 1, 1, 0.25f + alphaTick);
        for (Slot slot : menu.playerInventorySlots) {
            if (ProductsRegister.getProducts(currentCategory).contains(IProduct.of(slot.getItem().getItem()))) {
                pGuiGraphics.blit(SUITABLE_SLOT_MARK, this.leftPos + slot.x, this.topPos + slot.y,
                        0,0, 16,16, 16, 16);
            }
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    private long getExchangeVal() {
        if (!exchange.isEmpty()) {
            ItemStack itemStack = exchange.getItem(0);
            IProduct product = exchangeProductsMap.get(itemStack.getItem());
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
            case ProductsRegister.RECYCLE -> {return RECYCLE;}
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
        private ResourceLocation overrideTexture = null;
        private ResourceLocation overrideTextureSelected = null;

        public Category(int pX, int pY, String name) {
            super(pX, pY, 32, 28, 0, 0, 0, TAB_SELECTED, 32,
                    28, (button) -> ((Category) button).click());
            this.name = name;
            setTooltip(Tooltip.create(Component.translatable("tooltip.category." + name)));
            if (Objects.equals(currentCategory, this.name)) {
                this.click();
            }
        }

        @Override
        public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.renderFakeItem(new ItemStack(ProductsRegister.getIcon(name)),
                    this.getX() + getWidth() / 2 - 6,
                    this.getY() + getHeight() / 2 - 8
            );
        }

        public Category overrideTexture(ResourceLocation texture, ResourceLocation textureSelected) {
            this.overrideTexture = texture;
            this.overrideTextureSelected = textureSelected;
            return this;
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (isSelected) {
                if (overrideTextureSelected != null) {
                    this.renderTexture(pGuiGraphics, overrideTextureSelected, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
                } else {
                    super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                }
            } else {
                if (isHovered) {
                    pGuiGraphics.setColor(27 / 255f, 161 / 255f, 226 / 255f, 0.6f);
                } else {
                    pGuiGraphics.setColor(0.5f, 0.5f, 0.5f, 1f);
                }
                this.renderTexture(pGuiGraphics,
                        overrideTexture == null ? TAB : overrideTexture
                        , this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
            }
            pGuiGraphics.setColor(1, 1, 1, 1);
        }

        public void click() {
            currentCategory = name;
            if (selectedCategory != null) {
                selectedCategory.isSelected = false;
                if (!Objects.equals(selectedCategory.name, this.name)) {
                    selectedProduct = null;
                    menu.productDisplaySlot.set(ItemStack.EMPTY);
                }
            }
            pageIndex = 0;
            isSelected = true;
            selectedCategory = this;
            currentProducts = new ArrayList<>(ProductsRegister.getProducts(name));
            if (ProductsRegister.EXCHANGE.equals(name)) {
                refineExchangeProducts();
            }
            if (ProductsRegister.RECYCLE.equals(name)) {
                refineRecycleProducts();
            }
        }
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);
        if (selectedProduct != null) {
            scrollBtn.mouseMove(pMouseX);
        }
    }

    private void recycle() {
        if (needUpdate) {
            return;
        }
        ItemStack itemStack = menu.recycleSlot.getItem();
        if (recyclePrice == 0 || itemStack == ItemStack.EMPTY) {
            return;
        }
        IRecycleProduct product = recycleProductsMap.get(itemStack.getItem());
        if (product == null) {
            return;
        }
        PacketHandler.simpleChannel.sendToServer(new RecycleItemPacket(ProductsRegister.getId(product.get())));
        recyclePrice = 0;
        menu.recycleSlot.set(ItemStack.EMPTY);
        needUpdate = true;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (needUpdate) {
            return false;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private static class ScrollBtn extends OptionalImageButton {
        private double xOffset;
        private final int len;
        public float progress;
        private final int originalX;

        public ScrollBtn(int pX, int pY, int pWidth, int pHeight, int len, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight) {
            super(pX, pY, pWidth, pHeight, 0, 0, 0, pResourceLocation, pTextureWidth, pTextureHeight, (btn) -> {});
            this.len = len;
            originalX= getX();
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            boolean clicked = super.mouseClicked(pMouseX, pMouseY, pButton);
            if (clicked && mouseDown && !isPrevented()) {
                xOffset = pMouseX;
            }
            return clicked;
        }

        public void mouseMove(double pMouseX) {
            if (mouseDown && !isPrevented())  {
                float dis = (float) (pMouseX - xOffset);
                xOffset = pMouseX;
                progress = Mth.clamp( progress + dis / len, 0, 1.05f);
                this.setX((int) (originalX + len * progress));
            }
        }

        public void reset() {
            progress = 0;
            xOffset = 0;
            setX(originalX);
            super.reset();
        }
    }
}
