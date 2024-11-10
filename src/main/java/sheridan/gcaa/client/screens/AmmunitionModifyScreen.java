package sheridan.gcaa.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector4i;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.screens.componets.OptionalImageButton;
import sheridan.gcaa.client.screens.containers.AmmunitionModifyMenu;
import sheridan.gcaa.items.ammunition.AmmunitionModRegister;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.network.PacketHandler;
import sheridan.gcaa.network.packets.c2s.ApplyAmmunitionModifyPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AmmunitionModifyScreen extends AbstractContainerScreen<AmmunitionModifyMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(GCAA.MODID, "textures/gui/screen/ammunition_modify.png");
    private static final ResourceLocation AMMO_PROGRESS_EMPTY = new ResourceLocation(GCAA.MODID, "textures/gui/component/ammo_progress_empty.png");
    private static final ResourceLocation AMMO_PROGRESS_FILLED = new ResourceLocation(GCAA.MODID, "textures/gui/component/ammo_progress_filled.png");
    private static final ResourceLocation MODIFY_AMMUNITION = new ResourceLocation(GCAA.MODID, "textures/gui/component/modify_ammunition.png");
    private static final int PAGE_SIZE = 42;
    private static final int COLUMN_SIZE = 6;
    private OptionalImageButton applyBtn;
    private final SimpleContainer ammo;
    private final ArrayList<ArrayList<AmmunitionModRegister.ModEntry>> suitableAmmunitionMods = new ArrayList<>();
    private int page = 0;
    private IAmmunition currentAmmo = null;
    private List<List<ModIcon>> modIcons = null;
    private final Set<AmmunitionModRegister.ModEntry> selectedMods = new HashSet<>();
    private Set<IAmmunitionMod> ammoAlreadyHas = new HashSet<>();
    private boolean needUpdate = false;

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
        applyBtn = new OptionalImageButton(this.leftPos + 140, this.topPos + 62, 16, 16, 0, 0, 0, MODIFY_AMMUNITION, 16, 16,  (btn) -> {applyModify();});
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
                applyBtn.setPrevented(false);
                applyBtn.setTooltip(Tooltip.create(Component.translatable("tooltip.btn.apply")));
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
            List<IAmmunitionMod> mods = new ArrayList<>();
            for (AmmunitionModRegister.ModEntry entry : selectedMods) {
                mods.add(entry.mod());
            }
            PacketHandler.simpleChannel.sendToServer(new ApplyAmmunitionModifyPacket((mods)));
            needUpdate = true;
        }
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
        Set<IAmmunitionMod> suitableMods = currentAmmo.getSuitableMods();
        ArrayList<AmmunitionModRegister.ModEntry> allEntries = new ArrayList<>();
        for (IAmmunitionMod mod : suitableMods) {
            AmmunitionModRegister.ModEntry entry = AmmunitionModRegister.getEntry(mod);
            if (entry != null) {
                allEntries.add(entry);
            }
        }
        suitableAmmunitionMods.clear();
        for (int i = 0; i < allEntries.size(); i += PAGE_SIZE) {
            suitableAmmunitionMods.add(new ArrayList<>(allEntries.subList(i, Math.min(i + PAGE_SIZE, allEntries.size()))));
        }
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void updateClient(String modsUUID, int maxModCapability, CompoundTag modsTag) {
        needUpdate = false;
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
            if (selectedMods != null && selectedMods.size() > 0 && checkAmmo()) {
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
            total += entry.mod().cost();
        }
        return total;
    }

    private boolean canAddMod(IAmmunitionMod mod) {
        if (checkAmmo()) {
            return !ammoAlreadyHas.contains(mod) && currentAmmo.getMaxModCapacity() - getTotal() >= mod.cost();
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

        public ResourceLocation getCurrentTexture() {
            return currentTexture;
        }

        public void setCurrentTexture(ResourceLocation currentTexture, int texW, int texH)  {
            this.currentTexture = currentTexture;
            this.texW = texW;
            this.texH = texH;
        }

        public Vector2i getOffset() {
            return offset;
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
                if (selectedMods.contains(entry)) {
                    select();
                }
                blocked = false;
            } else {
                blocked = true;
            }
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (currentTexture != null) {
                if (selected) {
                    pGuiGraphics.setColor(27 / 255f, 161 / 255f, 226 / 255f, 1);
                } else {
                    if (blocked) {
                        pGuiGraphics.setColor(0.5f, 0.5f, 0.5f, 1);
                    } else {
                        pGuiGraphics.setColor(1, 1, 1, 1);
                    }
                }
                pGuiGraphics.blit(currentTexture, getX(), getY(), offset.x, offset.y, getWidth(), getHeight(), texW, texH);
                pGuiGraphics.setColor(1, 1, 1, 1);
            }
        }
    }

}
