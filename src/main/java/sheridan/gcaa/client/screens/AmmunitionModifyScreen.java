package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.screens.components.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.AmmunitionModifyMenu;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.AmmunitionModRegister;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.ApplyAmmunitionModifyPacket;
import sheridan.gcaa.utils.FontUtils;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class AmmunitionModifyScreen extends AbstractContainerScreen<AmmunitionModifyMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(GCAA.MODID, "textures/gui/screen/ammunition_modify.png");
    private static final ResourceLocation AMMO_PROGRESS_EMPTY = new ResourceLocation(GCAA.MODID, "textures/gui/component/ammo_progress_empty.png");
    private static final ResourceLocation AMMO_PROGRESS_FILLED = new ResourceLocation(GCAA.MODID, "textures/gui/component/ammo_progress_filled.png");
    private static final ResourceLocation MODIFY_AMMUNITION = new ResourceLocation(GCAA.MODID, "textures/gui/component/modify_ammunition.png");
    private static final ResourceLocation BTN_BORDER = new ResourceLocation(GCAA.MODID, "textures/gui/component/btn_border.png");

    private static final int PAGE_SIZE = 42;
    private static final int COLUMN_SIZE = 6;
    private OptionalImageButton applyBtn;
    private final SimpleContainer ammo;
    private final ArrayList<ArrayList<AmmunitionModRegister.ModEntry>> suitableAmmunitionMods = new ArrayList<>();
    private int page = 0;
    private IAmmunition currentAmmo = null;
    private List<List<ModIcon>> modIcons = null;
    private final Set<AmmunitionModRegister.ModEntry> selectedMods = new HashSet<>();
    private final Set<IAmmunitionMod> ammoAlreadyHas = new HashSet<>();
    private boolean needUpdate = false;
    private long balance;
    private Set<Integer> tempSelectedMods = new HashSet<>();

    public AmmunitionModifyScreen(AmmunitionModifyMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.width = 276;
        this.height = 166;
        this.imageWidth = 276;
        this.imageHeight = 166;
        ammo = pMenu.ammo;
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(1);
        gridlayout.defaultCellSetting().padding(1, 1, 1, 1);
        applyBtn = new OptionalImageButton(this.leftPos + 140, this.topPos + 62, 16, 16, 0, 0, 0, MODIFY_AMMUNITION, 16, 16,  (btn) -> applyModify());
        modIcons = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE / COLUMN_SIZE; i++) {
            List<ModIcon> column = new ArrayList<>();
            for (int j = 0; j < COLUMN_SIZE; j++) {
                ModIcon icon = new ModIcon(this.leftPos + 5 + j * 16, this.topPos + 18 + i * 16, 16, 16, (btn)->{
                        if (!needUpdate) {
                            ((ModIcon) btn).click();
                        }
                    }, null);
                rowHelper.addChild(icon);
                column.add(icon);
            }
            modIcons.add(column);
        }
        rowHelper.addChild(applyBtn);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.minecraft != null && this.minecraft.player != null) {
            if (!needUpdate) {
                update();
                Player player = this.minecraft.player;
                balance = PlayerStatusProvider.getStatus(player).getBalance();
            }
        } else {
            onClose();
        }
    }

    private void update() {
        boolean ammoPut = false;
        if (ammo.getItem(0) == ItemStack.EMPTY) {
            applyBtn.setPrevented(true);
            applyBtn.setPreventedTooltipStr(Component.translatable("tooltip.btn.need_put_ammo").getString());
            currentAmmo = null;
        } else if (!(ammo.getItem(0).getItem() instanceof IAmmunition)) {
            applyBtn.setPrevented(true);
            applyBtn.setPreventedTooltipStr(Component.translatable("tooltip.btn.not_suitable").getString());
            currentAmmo = null;
        } else {
            currentAmmo = (IAmmunition) ammo.getItem(0).getItem();
            ammoPut = true;
        }
        updateAmmoAlreadyHas();
        updateSuitableMods();
        updateModIcons();
        if (ammoPut) {
            if (selectedMods != null && selectedMods.size() > 0) {
                if (getPrice() <= balance) {
                    applyBtn.setPrevented(false);
                    applyBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.apply")));
                } else {
                    applyBtn.setPrevented(true);
                    applyBtn.setPreventedTooltip("");
                }
            } else {
                applyBtn.setPrevented(true);
                applyBtn.setPreventedTooltipStr(Component.translatable("tooltip.btn.no_modify_selected").getString());
            }
        }
    }

    private void updateAmmoAlreadyHas() {
        if (checkAmmo()) {
            ammoAlreadyHas.clear();
            ammoAlreadyHas.addAll(currentAmmo.getMods(ammo.getItem(0)));
        }
    }

    private void applyModify() {
        if (checkAmmo() && selectedMods != null && selectedMods.size() > 0) {
            int total = getTotal();
            int cap = currentAmmo.getMaxModCapacity();
            if (total > cap) {
                return;
            }
            long price = getPrice();
            if (balance < price) {
                return;
            }
            List<IAmmunitionMod> mods = new ArrayList<>();
            for (AmmunitionModRegister.ModEntry entry : selectedMods) {
                mods.add(entry.mod());
            }
            PacketHandler.simpleChannel.sendToServer(new ApplyAmmunitionModifyPacket((mods)));
            needUpdate = true;
        }
    }

    private long getPrice() {
        if (selectedMods != null && selectedMods.size() > 0) {
            long price = 0;
            for (AmmunitionModRegister.ModEntry entry : selectedMods) {
                price += entry.mod().getPrice();
            }
            if (currentAmmo != null) {
                int ammoLeft = currentAmmo.getAmmoLeft(ammo.getItem(0));
                if (ammoLeft > 0) {
                    price *= (double) ammoLeft / currentAmmo.get().getMaxDamage(ammo.getItem(0));
                } else {
                    return 0;
                }
            }
            return price;
        }
        return 0;
    }

    private boolean checkAmmo() {
        if (ammo.getItem(0).getItem() instanceof IAmmunition stackAmmunition) {
            return currentAmmo == stackAmmunition;
        }
        return false;
    }

    private void updateSuitableMods() {
        if (currentAmmo == null) {
            suitableAmmunitionMods.clear();
            return;
        }
        Set<Integer> idSet = new HashSet<>();
        Set<IAmmunitionMod> suitableMods = currentAmmo.getSuitableMods();
        ArrayList<AmmunitionModRegister.ModEntry> allEntries = new ArrayList<>();
        for (IAmmunitionMod mod : suitableMods) {
            AmmunitionModRegister.ModEntry entry = AmmunitionModRegister.getEntry(mod);
            if (entry != null) {
                allEntries.add(entry);
                idSet.add(entry.index());
            }
        }
        suitableAmmunitionMods.clear();
        for (int i = 0; i < allEntries.size(); i += PAGE_SIZE) {
            suitableAmmunitionMods.add(new ArrayList<>(allEntries.subList(i, Math.min(i + PAGE_SIZE, allEntries.size()))));
        }
        Iterator<Integer> iterator = tempSelectedMods.iterator();
        iterator.forEachRemaining(id -> {
            if (!idSet.contains(id)) {
                iterator.remove();
            }
        });
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        renderDataRate(pGuiGraphics);
        renderBalance(pGuiGraphics);
    }

    private void renderBalance(@NotNull GuiGraphics pGuiGraphics) {
        if (this.minecraft != null) {
            Font font = this.minecraft.font;
            String str = Component.translatable("tooltip.screen_info.balance").getString() + balance;
            pGuiGraphics.drawString(font, str, this.leftPos + 277, this.topPos + 5, 0x00ff00);
            int color = balance >= getPrice() ? 0x00ff00 : 0xff0000;
            String str2 = Component.translatable("tooltip.screen_info.worth").getString() + getPrice();
            pGuiGraphics.drawString(font, str2, this.leftPos + 277, this.topPos + 15, color);
        }
    }

    private void renderDataRate(@NotNull GuiGraphics pGuiGraphics) {
        if (checkAmmo()) {
            CompoundTag prevRate = Ammunition.getWhiteDataTag();
            List<IAmmunitionMod> mods = currentAmmo.getMods(ammo.getItem(0));
            if (selectedMods != null && selectedMods.size() > 0) {
                for (AmmunitionModRegister.ModEntry entry : selectedMods) {
                    mods.add(entry.mod());
                }
            }
            Ammunition.processDataRateByGivenMods(prevRate, mods, currentAmmo);
            float baseDamageRate = prevRate.getFloat(Ammunition.BASE_DAMAGE_RATE);
            MutableComponent baseDamageRateComponent = Component.translatable("gcaa.ammunition_data.base_damage_rate")
                    .append(FontUtils.toPercentageStr(baseDamageRate));
            if (baseDamageRate < Ammunition.MIN_BASE_DAMAGE_RATE) {
                baseDamageRateComponent.append(Component.literal("(" + FontUtils.toPercentageStr(Ammunition.MIN_BASE_DAMAGE_RATE) + ")"));
            }
            float minDamageRate = prevRate.getFloat(Ammunition.MIN_DAMAGE_RATE);
            MutableComponent minDamageRateComponent = Component.translatable("gcaa.ammunition_data.min_damage_rate")
                    .append(FontUtils.toPercentageStr(minDamageRate));
            if (minDamageRate < Ammunition.MIN_MIN_DAMAGE_RATE) {
                minDamageRateComponent.append(Component.literal("(" + FontUtils.toPercentageStr(Ammunition.MIN_MIN_DAMAGE_RATE) + ")"));
            }
            float effectiveRangeRate = prevRate.getFloat(Ammunition.EFFECTIVE_RANGE_RATE);
            MutableComponent effectiveRangeRateComponent = Component.translatable("gcaa.ammunition_data.effective_range_rate")
                    .append(FontUtils.toPercentageStr(effectiveRangeRate));
            if (effectiveRangeRate < Ammunition.MIN_EFFECTIVE_RANGE_RATE) {
                effectiveRangeRateComponent.append(Component.literal("(" + FontUtils.toPercentageStr(Ammunition.MIN_EFFECTIVE_RANGE_RATE) + ")"));
            }
            float speedRate = prevRate.getFloat(Ammunition.SPEED_RATE);
            MutableComponent speedRateComponent = Component.translatable("gcaa.ammunition_data.speed_rate")
                    .append(FontUtils.toPercentageStr(speedRate));
            if (speedRate < Ammunition.MIN_SPEED_RATE) {
                speedRateComponent.append(Component.literal("(" + FontUtils.toPercentageStr(Ammunition.MIN_SPEED_RATE) + ")"));
            }
            float penetrationRate = prevRate.getFloat(Ammunition.PENETRATION_RATE);
            MutableComponent penetrationRateComponent = Component.translatable("gcaa.ammunition_data.penetration_rate")
                    .append(FontUtils.toPercentageStr(penetrationRate));
            if (penetrationRate < Ammunition.MIN_PENETRATION_RATE) {
                penetrationRateComponent.append(Component.literal("(" + FontUtils.toPercentageStr(Ammunition.MIN_PENETRATION_RATE) + ")"));
            }
            int x = this.leftPos + 189;
            int yStart = this.topPos + 10;
            int color = 0x36e1f1;
            pGuiGraphics.drawString(this.font, baseDamageRateComponent, x, yStart, color);
            pGuiGraphics.drawString(this.font, minDamageRateComponent, x, yStart + 12, color);
            pGuiGraphics.drawString(this.font, effectiveRangeRateComponent, x, yStart + 24, color);
            pGuiGraphics.drawString(this.font, speedRateComponent, x, yStart + 36, color);
            pGuiGraphics.drawString(this.font, penetrationRateComponent, x, yStart + 48, color);
        }
    }

    public void updateClient(String modsUUID, int maxModCapability, CompoundTag modsTag, long balance) {
        needUpdate = false;
        tempSelectedMods.clear();
        for (AmmunitionModRegister.ModEntry modEntry : selectedMods) {
            tempSelectedMods.add(modEntry.index());
        }
        selectedMods.clear();
        updateAmmoAlreadyHas();
        for (int i = 0; i < PAGE_SIZE; i++) {
            int j = i / COLUMN_SIZE;
            ModIcon icon = modIcons.get(j).get(i % COLUMN_SIZE);
            icon.clearEntry();
        }
        if (checkAmmo()) {
            ItemStack item = ammo.getItem(0);
            CompoundTag tag = item.getTag();
            if (tag != null) {
                tag.putString("modsUUID", modsUUID);
                tag.putInt("max_mod_capacity", maxModCapability);
                tag.put("mods", modsTag);
            }
        }
        if (this.minecraft != null && this.minecraft.player != null) {
            PlayerStatusProvider.getStatus(this.minecraft.player).setBalance(balance);
        }
    }

    private void updateModIcons() {
        if (suitableAmmunitionMods == null) {
            return;
        }
        if (suitableAmmunitionMods.size() == 0) {
            for (int i = 0; i < PAGE_SIZE; i++) {
                int j = i / COLUMN_SIZE;
                ModIcon icon = modIcons.get(j).get(i % COLUMN_SIZE);
                icon.clearEntry();
            }
            return;
        }
        List<AmmunitionModRegister.ModEntry> mods = suitableAmmunitionMods.get(page);
        for (int i = 0; i < PAGE_SIZE; i++) {
            int j = i / COLUMN_SIZE;
            ModIcon icon = modIcons.get(j).get(i % COLUMN_SIZE);
            if (i < mods.size()) {
                icon.setEntry(mods.get(i));
            } else {
                icon.clearEntry();
            }
        }

    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBackground(pGuiGraphics);
        if (this.minecraft != null) {
            int startX = (this.width - this.getXSize()) / 2;
            int startY = (this.height - this.getYSize()) / 2;
            pGuiGraphics.blit(AMMO_PROGRESS_EMPTY, startX + 104, startY + 39,  0,0, 84, 24, 84, 24);
            if (selectedMods != null && checkAmmo()) {
                float cap = currentAmmo.getMaxModCapacity();
                float total = getTotal();
                float progress = total / cap;
                pGuiGraphics.blit(AMMO_PROGRESS_FILLED, (int) (startX + (104 - 84 * (1 - progress))), startY + 39,  0,0, 84, 24, 84, 24);
            }
            pGuiGraphics.blit(BACKGROUND, startX, startY,  0,0, this.getXSize(), this.getYSize(), this.getXSize(), this.getYSize());
        }
        if (needUpdate) {
            this.renderBackground(pGuiGraphics);
            RenderSystem.enableDepthTest();
            String text = Component.translatable("label.attachments_screen.wait_response").getString();
            Font font = Minecraft.getInstance().font;
            pGuiGraphics.drawString(font, text, (width - font.width(text)) / 2, height / 2, -1);
        }
    }

    private int getTotal() {
        int total = currentAmmo.getModCapacityUsed(ammo.getItem(0));
        for (AmmunitionModRegister.ModEntry entry : selectedMods) {
            total += entry.mod().getCostFor(currentAmmo);
        }
        return total;
    }

    private boolean canAddMod(IAmmunitionMod mod) {
        if (checkAmmo()) {
            return !ammoAlreadyHas.contains(mod) && currentAmmo.getMaxModCapacity() - getTotal() >= mod.getCostFor(currentAmmo)
                    && (balance >= getPrice() + mod.getPrice());
        }
        return false;
    }

    private class ModIcon extends ImageButton {
        private ResourceLocation currentTexture = null;
        private Vector2i offset;
        private int texW;
        private int texH;
        private AmmunitionModRegister.ModEntry entry;
        public boolean selected = false;
        public boolean blocked = false;
        private boolean mouseDown = false;

        public ModIcon(int pX, int pY, int pWidth, int pHeight, OnPress pOnPress, AmmunitionModRegister.ModEntry entry)  {
            super(pX, pY, pWidth, pHeight, 0, 0, new ResourceLocation(""), pOnPress);
            offset = new Vector2i(0, 0);
            this.entry = entry;
            if (this.entry != null) {
                Vector4i uvs = entry.mod().getIconUV();
                setOffset(new Vector2i(uvs.x, uvs.y));
                setCurrentTexture(entry.mod().getIconTexture(), uvs.z, uvs.w);
            }
        }

        public void setCurrentTexture(ResourceLocation currentTexture, int texW, int texH)  {
            this.currentTexture = currentTexture;
            this.texW = texW;
            this.texH = texH;
        }

        public void setOffset(Vector2i offset) {
            this.offset = offset;
        }

        public AmmunitionModRegister.ModEntry getEntry() {
            return entry;
        }

        public void clearEntry() {
            if (entry != null) {
                selectedMods.remove(entry);
            }
            entry = null;
            currentTexture = null;
            selected = false;
            blocked = false;
        }

        public void click() {
            if (entry != null) {
                if (selectedMods.contains(entry)) {
                    selectedMods.remove(entry);
                    selected = false;
                    tempSelectedMods.remove(entry.index());
                } else {
                    if (!blocked && canAddMod(entry.mod())) {
                        selectedMods.add(entry);
                        selected = true;
                    }
                }
            }
        }

        public void select() {
            if (entry != null) {
                selected = true;
                selectedMods.add(entry);
            }
        }

        public void setEntry(AmmunitionModRegister.ModEntry entry) {
            this.entry = entry;
            Vector4i uvs = entry.mod().getIconUV();
            setOffset(new Vector2i(uvs.x, uvs.y));
            setCurrentTexture(entry.mod().getIconTexture(), uvs.z, uvs.w);
            if (canAddMod(entry.mod())) {
                if (selectedMods.contains(entry) || tempSelectedMods.contains(entry.index())) {
                    select();
                }
                blocked = false;
                genTooltip(entry, false);
            } else {
                blocked = true;
                genTooltip(entry, true);
            }
            for (AmmunitionModRegister.ModEntry e : selectedMods) {
                if (e.index() == entry.index()) {
                    selected = true;
                    break;
                }
            }
        }

        private void genTooltip(AmmunitionModRegister.ModEntry entry, boolean blocked) {
            MutableComponent component = Component.translatable(blocked ?
                            (balance < (getPrice() + entry.mod().getPrice()) ?  "tooltip.btn.underfund" : "tooltip.btn.modify_prevented") :
                            "tooltip.btn.select_modify")
                    .append("\n")
                    .append(Component.translatable(entry.mod().getDescriptionId()).withStyle(Style.EMPTY.withColor(entry.mod().getThemeColor())));
            Component specialDescription = entry.mod().getSpecialDescription();
            if (specialDescription != null) {
                component.append("\n");
                component.append(specialDescription).append("\n");
            }
            String str2 = Component.translatable("tooltip.screen_info.worth").getString() + price(entry.mod());
            component.append(Component.literal(str2));
            setTooltip(Tooltip.create(component));
        }

        private long price(IAmmunitionMod mod) {
            long price = mod.getPrice();
            if (currentAmmo != null) {
                int ammoLeft = currentAmmo.getAmmoLeft(ammo.getItem(0));
                if (ammoLeft > 0) {
                    price *= (double) ammoLeft / currentAmmo.get().getMaxDamage(ammo.getItem(0));
                }
            }
            return price;
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            super.onClick(pMouseX, pMouseY);
            mouseDown = true;
        }

        @Override
        public void onRelease(double pMouseX, double pMouseY) {
            super.onRelease(pMouseX, pMouseY);
            mouseDown = false;
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (currentTexture != null) {
                if (selected) {
                    pGuiGraphics.setColor(27 / 255f, 161 / 255f, 226 / 255f, 1);
                } else {
                    if (blocked) {
                        pGuiGraphics.setColor(0.5f, 0.5f, 0.5f, 0.5f);
                    } else {
                        pGuiGraphics.setColor(1, 1, 1, 1);
                    }
                }
                if (mouseDown) {
                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().scale(1.01f, 1.01f, 1f);
                }
                pGuiGraphics.blit(currentTexture, getX(), getY(), offset.x, offset.y, getWidth(), getHeight(), texW, texH);
                if (isHovered) {
                    pGuiGraphics.blit(BTN_BORDER, getX(), getY(), 0, 0, getWidth(), getHeight(), 16, 16);
                }
                pGuiGraphics.setColor(1, 1, 1, 1);
                if (mouseDown) {
                    pGuiGraphics.pose().popPose();
                }
            }
        }
    }

}
