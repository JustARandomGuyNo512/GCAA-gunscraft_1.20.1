package sheridan.gcaa.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.model.registry.GunModelRegistry;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.guns.IGun;


public class GunDebugAdjustScreen extends Screen {
    private boolean originalInit = false;
    private float[][] originalData;
    private DisplayData displayData;
    private int operationIndex = 0;
    private int viewIndex = 0;
    private EditBox editBox;
    private float p;
    private static String[] viewModeNames = {"FirstPersonMain", "ThirdPersonRight", "Ground","Frame", "GUI"};
    public GunDebugAdjustScreen() {
        super(Component.literal("Gun Debug Adjust Screen"));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        Font font = Minecraft.getInstance().font;
        pGuiGraphics.drawString(font, "Gun Debug Adjust Screen", (this.width - font.width("Gun Debug Adjust Screen")) / 2, 6, 0xFFFFFF);
        String operationStr = "position";
        if (operationIndex == 1) {operationStr = "rotation";}
        if (operationIndex == 2) {operationStr = "scale";}
        pGuiGraphics.drawString(font, operationStr, (this.width - font.width(operationStr)) / 2, 26, 0xFFFFFF);
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        rowHelper.addChild(Button.builder(Component.literal("close"), (p_280814_) -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }).width(50).build(), 2, gridlayout.newCellSettings().paddingTop(50));
        editBox = new EditBox(Minecraft.getInstance().font, 50, 150, 100, 20, Component.literal("p"));
        editBox.setValue("0.1");
        rowHelper.addChild(editBox);
        initBtn(rowHelper);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    private void initBtn(GridLayout.RowHelper rowHelper) {
        rowHelper.addChild(Button.builder(Component.literal("pos"), (p_280814_) -> {
            operationIndex = 0;
        }).width(40).pos(50, 20).build());
        rowHelper.addChild(Button.builder(Component.literal("rot"), (p_280814_) -> {
            operationIndex = 1;
        }).width(40).pos(90, 20).build());
        rowHelper.addChild(Button.builder(Component.literal("scale"), (p_280814_) -> {
            operationIndex = 2;
        }).width(40).pos(130, 20).build());
        rowHelper.addChild(Button.builder(Component.literal("FirstPersonMain"), (p_280814_) -> {
            viewIndex ++;
            viewIndex %= 5;
            p_280814_.setMessage(Component.literal(viewModeNames[viewIndex]));
        }).width(100).pos(300, 20).build());
        rowHelper.addChild(Button.builder(Component.literal("Bobbing"), (p_280814_) -> {
            Clients.handleWeaponBobbing = !Clients.handleWeaponBobbing;
        }).width(50).pos(300, 50).build());
        rowHelper.addChild(Button.builder(Component.literal("print"), (p_280814_) -> {
            printToConsole();
        }).width(100).pos(50, 180).build());
        for (int i = 0; i < 3; i ++) {
            int finalI = i;
            rowHelper.addChild(Button.builder(Component.literal("p" + i + "+"), (p_280814_) -> {
                add(finalI);
            }).width(30).pos(50, 45 + 20 * i).build());
            rowHelper.addChild(Button.builder(Component.literal("p" + i + "-"), (p_280814_) -> {
                dec(finalI);
            }).width(30).pos(90, 45 + 20 * i).build());
            rowHelper.addChild(Button.builder(Component.literal("reset"), (p_280814_) -> {
                resetData(finalI);
            }).width(35).pos(130, 45 + 20 * i).build());
        }
    }

    private void printToConsole() {
        float[] params = displayData.get(viewIndex);
        String paramsStr = params[0] * 16 + "," + params[1] * 16 + "," + params[2] * 16 + "   " +
                Math.toDegrees(params[3]) + "," + Math.toDegrees(params[4]) + "," + Math.toDegrees(params[5]) + "   " +
                params[6] + "," + params[7] + "," + params[8];
        System.out.println(paramsStr);
    }

    private void add(int argIndex) {
        displayData.set(viewIndex, operationIndex * 3 + argIndex, displayData.get(viewIndex, operationIndex * 3 + argIndex) + get());
    }

    private void dec(int argIndex) {
        displayData.set(viewIndex, operationIndex * 3 + argIndex, displayData.get(viewIndex, operationIndex * 3 + argIndex) - get());
    }

    private float get() {
        switch (operationIndex) {
            case 0 -> {return p / 16;}
            case 1 -> {return (float) Math.toRadians(p);}
            case 2 -> {return p;}
        }
        return 0;
    }

    private void resetData(int argIndex) {
        if (originalInit && displayData != null) {
            displayData.set(viewIndex, operationIndex * 3 + argIndex, originalData[viewIndex][operationIndex * 3 + argIndex]);
        }
    }

    @Override
    public void tick() {
        super.tick();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            ItemStack stackOff = player.getOffhandItem();
            if (stack.getItem() instanceof IGun || stackOff.getItem() instanceof IGun) {
                IGun gun = (IGun) (stack.getItem() instanceof IGun ? stack.getItem() : stackOff.getItem());
                displayData = GunModelRegistry.getDisplayData(gun);
                if (displayData == null) {
                    Minecraft.getInstance().setScreen(null);
                } else {
                    if (!originalInit) {
                        originalData = displayData.copy();
                        originalInit = true;
                    }
                    if (!editBox.isFocused()) {
                        try {
                            p = Float.parseFloat(editBox.getValue());
                        } catch (Exception e) {
                            editBox.setValue(p + "");
                        }
                    }
                }
            } else {
                Minecraft.getInstance().setScreen(null);
            }
        }
    }


}
