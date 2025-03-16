package sheridan.gcaa.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.recoilAnimation.*;
import sheridan.gcaa.items.gun.IGun;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class RecoilModifyScreen extends Screen {
    public NewRecoilData newRecoilData;
    public IGun gun;
    public Map<String, List<TrackBox>> trackMap = new HashMap<>();
    public Map<String, MassDampingSpring> springPool = new HashMap<>();
    public Set<AbstractWidget> springEditions = new HashSet<>();
    public Set<AbstractWidget> trackEditions = new HashSet<>();
    public int mx, my;
    public EditBox impulseScript;
    public Button selectedSpringBtn;
    public String selectedSpringBtnName;
    public Button variables;
    public Button clampedSpringBtn;
    public Button massDampingSpring;
    public Button steadyStateSpring;
    public boolean hide = false;
    public boolean trackEditionVisible = true;
    public boolean springEditionVisible = true;
    public List<Label> labels = new ArrayList<>();
    public MassDampingSpring onModifySpring;
    EditBox back, upRot, randomX, randomY, shake;
    EditBox stiffness, dampingForward, dampingBackward, upperLimit, lowerLimit;
    public static Map<String, Supplier<MassDampingSpring>> springFactory = new HashMap<>();
    public Label springTypeName = new Label("Spring Type: None", 265, 85, 0xffffff);
    public ConcurrentLinkedDeque<Task> affirmTasks = new ConcurrentLinkedDeque<>();
    public List<Button> springPoolBtnList = new ArrayList<>();
    static Map<String, Integer> colorMap = new HashMap<>();

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (!hide) {
            String warn = "Any action that triggers the interface to reload will result in the loss of unsaved data";
            Font font = Minecraft.getInstance().font;
            pGuiGraphics.drawString(font, warn, (int) (this.width / 2 - font.width(warn) / 2.5f), 10, 0xff0000);
        }

        if (!hide) {
            for (Label label : labels) {
                label.render(pGuiGraphics);
            }
            if (springEditionVisible) {
                springTypeName.render(pGuiGraphics);
            }
        }

        if (!hide) {
            pGuiGraphics.drawString(this.font, mx + " " + my, mx + this.font.width("."), my - this.font.lineHeight, 0xffffff);
            pGuiGraphics.hLine(0, this.width, my, FastColor.ABGR32.color(255,0,255,0));
            pGuiGraphics.vLine(mx, 0, this.height, FastColor.ABGR32.color(255,0,0,255));
        }

        if (!affirmTasks.isEmpty()) {
            Task peek = affirmTasks.peek();
            if (peek != null) {
                renderBackground(pGuiGraphics);
                String info = peek.info();
                pGuiGraphics.drawString(font, info, (int) (this.width / 2 - font.width(info) / 2f), this.height / 2, 0xffffff);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);
        initTrackMap(rowHelper);
        initSpringPoolBtn(rowHelper);
        initRecoilData(rowHelper);
        initSpringEditions(rowHelper);
        initTrackEditions(rowHelper);
        initLabels();
        Button fire = Button.builder(Component.literal("Fire"), (b) -> {
            Clients.MAIN_HAND_STATUS.buttonDown.set(true);
            Clients.DO_SEND_FIRE_PACKET = false;
            RecoilCameraHandler.INSTANCE.disable = true;
        }).size(20, 16).pos(40, 8).build();
        rowHelper.addChild(fire);
        rowHelper.addChild(Button.builder(Component.literal("Hide"), (b) -> {
            hide = !hide;
            RecoilModifyScreen.this.renderables.forEach(r -> {
                if (r instanceof AbstractWidget abstractWidget) {
                    abstractWidget.visible = !hide;
                }
            });
            b.visible = true;
            fire.visible = true;
            springEditionsVisible(springEditionVisible);
            trackEditionsVisible(trackEditionVisible);
        }).size(20, 16).pos(65, 8).build());
        gridlayout.visitWidgets(this::addRenderableWidget);
        if (this.minecraft != null && this.minecraft.player != null) {
            ItemStack mainHandItem = this.minecraft.player.getMainHandItem();
            if (mainHandItem.getItem() instanceof IGun gun) {
                this.newRecoilData = NewRecoilData.get(gun);
                syncRecoilData();
            }
        }
    }

    public RecoilModifyScreen() {
        super(Component.literal("model recoil effect modify"));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!affirmTasks.isEmpty()) {
            return false;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (!affirmTasks.isEmpty()) {
            Task peek = affirmTasks.peek();
            if (peek == null) {
                affirmTasks.pop();
                return false;
            }
            if (pKeyCode == 89) {
                // yes
                peek.doYesTask();
                affirmTasks.pop();
            } else if (pKeyCode == 78 || pKeyCode == 27) {
                // no
                peek.doNoTask();
                affirmTasks.pop();
            }
            return false;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);
        mx = (int) pMouseX;
        my = (int) pMouseY;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        Clients.MAIN_HAND_STATUS.buttonDown.set(false);
        Clients.DO_SEND_FIRE_PACKET = true;
        RecoilCameraHandler.INSTANCE.disable = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void tick() {
        super.tick();
        if (newRecoilData == null) {
            this.onClose();
            if (this.minecraft != null) {
                this.minecraft.screen = null;
            }
        }
        for (Button btn : springPoolBtnList) {
            String string = btn.getMessage().getString();
            int color = springPool.get(string) == null ? Color.GRAY.getRGB() : colorMap.getOrDefault(string, 0xffffff);
            btn.setMessage(Component.literal(string).withStyle(Style.EMPTY.withColor(color)));
        }
    }

    private void syncRecoilData() {
        if (this.newRecoilData == null) {
            return;
        }
        back.setValue(newRecoilData.back.strVal());
        upRot.setValue(newRecoilData.upRot.strVal());
        randomX.setValue(newRecoilData.randomX.strVal());
        randomY.setValue(newRecoilData.randomY.strVal());
        shake.setValue(newRecoilData.shake.strVal());
    }

    private void applyRecoilData() {
        if (this.newRecoilData == null) {
            return;
        }
        try {
            newRecoilData.back.setValue(back.getValue().trim());
        } catch (Exception e)  {e.printStackTrace();}
        try {
            newRecoilData.upRot.setValue(upRot.getValue().trim());
        } catch (Exception e)  {e.printStackTrace();}
        try {
            newRecoilData.randomX.setValue(randomX.getValue().trim());
        } catch (Exception e)  {e.printStackTrace();}
        try {
            newRecoilData.randomY.setValue(randomY.getValue().trim());
        } catch (Exception e)  {e.printStackTrace();}
        try {
            newRecoilData.shake.setValue(shake.getValue().trim());
        } catch (Exception e)  {e.printStackTrace();}
        syncRecoilData();
    }

    private void initTrackMap(GridLayout.RowHelper rowHelper) {
        trackMap.put(NewRecoilData.TRANS_X, List.of(
                new TrackBox(Minecraft.getInstance().font, 40, 30, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 62, 30, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 84, 30, 18, 12, Component.literal(""))));
        trackMap.put(NewRecoilData.TRANS_Y, List.of(
                new TrackBox(Minecraft.getInstance().font, 40, 45, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 62, 45, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 84, 45, 18, 12, Component.literal(""))));
        trackMap.put(NewRecoilData.TRANS_Z, List.of(
                new TrackBox(Minecraft.getInstance().font, 40, 60, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 62, 60, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 84, 60, 18, 12, Component.literal(""))));
        trackMap.put(NewRecoilData.ROT_X, List.of(
                new TrackBox(Minecraft.getInstance().font, 40, 75, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 62, 75, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 84, 75, 18, 12, Component.literal(""))));
        trackMap.put(NewRecoilData.ROT_Y, List.of(
                new TrackBox(Minecraft.getInstance().font, 40, 90, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 62, 90, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 84, 90, 18, 12, Component.literal(""))));
        trackMap.put(NewRecoilData.ROT_Z, List.of(
                new TrackBox(Minecraft.getInstance().font, 40, 105, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 62, 105, 18, 12, Component.literal("")),
                new TrackBox(Minecraft.getInstance().font, 84, 105, 18, 12, Component.literal(""))));
        trackMap.values().forEach(list -> list.forEach(rowHelper::addChild));
    }

    private void initSpringPoolBtn(GridLayout.RowHelper rowHelper) {
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            Button btn = Button.builder(Component.literal("A" + (i + 1))
                    .withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB())), (b) ->
                    onSpringBtnClick("A" + (finalI + 1), b)).size(20, 12).pos(240 + i * 25, 30).build();
            rowHelper.addChild(btn);
            springPoolBtnList.add(btn);
        }
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            Button btn = Button.builder(Component.literal("B" + (i + 1))
                    .withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB())), (b) ->
                    onSpringBtnClick("B" + (finalI + 1), b)).size(20, 12).pos(240 + i * 25, 45).build();
            rowHelper.addChild(btn);
            springPoolBtnList.add(btn);
        }
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            Button btn = Button.builder(Component.literal("C" + (i + 1))
                    .withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB())), (b) ->
                    onSpringBtnClick("C" + (finalI + 1), b)).size(20, 12).pos(240 + i * 25, 60).build();
            rowHelper.addChild(btn);
            springPoolBtnList.add(btn);
        }
    }

    private void onSpringBtnClick(String name, Button btn) {
        boolean clickOnSame = name.equals(selectedSpringBtnName);
        selectedSpringBtn = btn;
        selectedSpringBtnName = name;
        MassDampingSpring massDampingSpring = springPool.get(name);
        if (massDampingSpring != null) {
            syncSpringDataToEditions();
            // sync data to Editions components
        }
        onModifySpring = massDampingSpring;
        if (clickOnSame) {
            springEditionsVisible(!springEditionVisible);
        } else {
            springEditionsVisible(true);
        }
    }

    private void syncSpringDataToEditions() {
        if (onModifySpring == null) {
            return;
        }
        stiffness.setValue(onModifySpring.stiffness.strVal());
        dampingForward.setValue(onModifySpring.dampingForward.strVal());
        dampingBackward.setValue(onModifySpring.dampingBackward.strVal());
        if (onModifySpring instanceof ClampedSpring clampedSpring) {
            upperLimit.setValue(clampedSpring.upperLimit.strVal());
            lowerLimit.setValue(clampedSpring.lowerLimit.strVal());
        }
    }

    private void onTrackBoxClick(EditBox box) {

    }

    private void initRecoilData(GridLayout.RowHelper rowHelper) {
        back = new EditBox(Minecraft.getInstance().font, 40, 200, 35, 12, Component.literal(""));
        back.setTooltip(Tooltip.create(Component.literal("Back")));
        upRot = new EditBox(Minecraft.getInstance().font, 80, 200, 35, 12, Component.literal(""));
        upRot.setTooltip(Tooltip.create(Component.literal("Up Rot")));
        randomX = new EditBox(Minecraft.getInstance().font, 120, 200, 35, 12, Component.literal(""));
        randomX.setTooltip(Tooltip.create(Component.literal("Random X")));
        randomY = new EditBox(Minecraft.getInstance().font, 160, 200, 35, 12, Component.literal(""));
        randomY.setTooltip(Tooltip.create(Component.literal("Random Y")));
        shake = new EditBox(Minecraft.getInstance().font, 200, 200, 35, 12, Component.literal(""));
        shake.setTooltip(Tooltip.create(Component.literal("Shake")));
        rowHelper.addChild(back);
        rowHelper.addChild(upRot);
        rowHelper.addChild(randomX);
        rowHelper.addChild(randomY);
        rowHelper.addChild(shake);
        rowHelper.addChild(Button.builder(Component.literal("Read"), (b) -> {
            syncRecoilData();
        }).size(25, 12).pos(40, 180).tooltip(Tooltip.create(Component.literal("Read from memory"))).build());
        rowHelper.addChild(Button.builder(Component.literal("Apply"), (b) -> {
            applyRecoilData();
        }).size(25, 12).pos(75, 180).tooltip(Tooltip.create(Component.literal("Apply to recoil data in memory"))).build());
    }

    private void initSpringEditions(GridLayout.RowHelper rowHelper) {
        Button apply = Button.builder(Component.literal("OK"), (b) -> {
            onModifySpringSave();
        }).size(20, 12).pos(240, 85).tooltip(Tooltip.create(Component.literal("Apply to spring data in memory"))).build();
        clampedSpringBtn = Button.builder(Component.literal("ClampedSpring"),
                (b) -> onSpringTypeClicked("ClampedSpring")).size(70, 12).pos(240, 100).tooltip(Tooltip.create(Component.literal("ClampedSpring"))).build();
        massDampingSpring = Button.builder(Component.literal("MassDampingSpring"),
                (b) -> onSpringTypeClicked("MassDampingSpring")).size(70, 12).pos(315, 100).tooltip(Tooltip.create(Component.literal("MassDampingSpring"))).build();
        steadyStateSpring = Button.builder(Component.literal("SteadyStateSpring"),
                (b) -> onSpringTypeClicked("SteadyStateSpring")).size(70, 12).pos(240, 115).tooltip(Tooltip.create(Component.literal("SteadyStateSpring"))).build();
        stiffness = new EditBox(Minecraft.getInstance().font, 240, 130, 100, 12, Component.literal(""));
        stiffness.setTooltip(Tooltip.create(Component.literal("Stiffness")));
        dampingForward = new EditBox(Minecraft.getInstance().font, 240, 145, 100, 12, Component.literal(""));
        dampingForward.setTooltip(Tooltip.create(Component.literal("Damping Forward")));
        dampingBackward = new EditBox(Minecraft.getInstance().font, 240, 160, 100, 12, Component.literal(""));
        dampingBackward.setTooltip(Tooltip.create(Component.literal("Damping Backward")));
        upperLimit = new EditBox(Minecraft.getInstance().font, 240, 175, 100, 12, Component.literal(""));
        upperLimit.setTooltip(Tooltip.create(Component.literal("Upper Limit")));
        lowerLimit = new EditBox(Minecraft.getInstance().font, 240, 190, 100, 12, Component.literal(""));
        lowerLimit.setTooltip(Tooltip.create(Component.literal("Lower Limit")));

        rowHelper.addChild(apply);
        rowHelper.addChild(clampedSpringBtn);
        rowHelper.addChild(massDampingSpring);
        rowHelper.addChild(steadyStateSpring);
        rowHelper.addChild(stiffness);
        rowHelper.addChild(dampingForward);
        rowHelper.addChild(dampingBackward);
        rowHelper.addChild(upperLimit);
        rowHelper.addChild(lowerLimit);
        springEditions.add(apply);
        springEditions.add(clampedSpringBtn);
        springEditions.add(massDampingSpring);
        springEditions.add(steadyStateSpring);
        springEditions.add(stiffness);
        springEditions.add(dampingForward);
        springEditions.add(dampingBackward);
        springEditions.add(upperLimit);
        springEditions.add(lowerLimit);
        springEditionsVisible(false);
    }

    private void onModifySpringSave() {
        if (onModifySpring == null) {
            return;
        }
        try {
            onModifySpring.stiffness.setValue(stiffness.getValue().trim());
        } catch (Exception e) {e.printStackTrace();}
        try {
            onModifySpring.dampingForward.setValue(dampingForward.getValue().trim());
        } catch (Exception e) {e.printStackTrace();}
        try {
            onModifySpring.dampingBackward.setValue(dampingBackward.getValue().trim());
        } catch (Exception e) {e.printStackTrace();}
        if (onModifySpring instanceof ClampedSpring clampedSpring) {
            try {
                clampedSpring.upperLimit.setValue(upperLimit.getValue().trim());
            } catch (Exception e) {e.printStackTrace();}
            try {
                clampedSpring.lowerLimit.setValue(lowerLimit.getValue().trim());
            } catch (Exception e) {e.printStackTrace();}
        }
        syncSpringDataToEditions();
    }

    private void onSpringTypeClicked(String type) {
        Runnable r = () -> {
            Supplier<MassDampingSpring> massDampingSpringSupplier = springFactory.get(type);
            onModifySpring = massDampingSpringSupplier.get();
            springPool.put(selectedSpringBtnName, onModifySpring);
            syncSpringDataToEditions();
            springEditionBoxesVisible();
            updateSpringTypeName();
        };
        if (onModifySpring == null) {
            r.run();
        } else {
            String simpleName = onModifySpring.getClass().getSimpleName();
            if (!simpleName.equals(type)) {
                affirmTask("Recreate spring object from: '" + simpleName + "' to: '" + type + "' ?", r);
            }
        }
    }

    private interface Task {
        void doYesTask();
        void doNoTask();
        String info();
    }

    private void affirmTask(String screenInfo, Runnable yesFunc) {
        affirmTask(screenInfo, yesFunc, () -> {});
    }

    private void affirmTask(String screenInfo, Runnable yesFunc, Runnable noFunc) {
        Task task = new Task() {
            @Override
            public void doYesTask() {
                yesFunc.run();
            }
            @Override
            public void doNoTask() {
                noFunc.run();
            }
            @Override
            public String info() {
                return screenInfo + " (Y / N or Ecs)";
            }
        };
        affirmTasks.push(task);
    }

    private void initTrackEditions(GridLayout.RowHelper rowHelper) {
        Button flag = Button.builder(Component.literal("+"), (b) -> {
            String flagStr = b.getMessage().getString();
            flagStr = "+".equals(flagStr) ? "-" : "+";
            b.setMessage(Component.literal(flagStr));
            String tooltipStr = "+".equals(flagStr) ? "Direction (positive)" : "Direction (negative)";
            b.setTooltip(Tooltip.create(Component.literal(tooltipStr)));
            //Logic here...
        }).size(20, 12).pos(40, 125).tooltip(Tooltip.create(Component.literal("Direction (positive)"))).build();

        Button apply = Button.builder(Component.literal("Apply"), (b) -> {

        }).size(25, 12).pos(80, 125).tooltip(Tooltip.create(Component.literal("Apply to track data in memory"))).build();

        impulseScript = new EditBox(Minecraft.getInstance().font, 40, 145, 345, 15, Component.literal(""));
        impulseScript.setTooltip(Tooltip.create(Component.literal(
                "Input impulse script here, example:\n 'Back*(A2.Steady*0.5+0.5)+UpRot'\nHover mouse on 'Variables' to see all available variables")));
        impulseScript.setMaxLength(81);
        variables = Button.builder(Component.literal("Variables"), (b) -> {}).size(40, 12).pos(345, 125)
                .tooltip(Tooltip.create(Component.literal("All named Variables:\n"))).build();

        rowHelper.addChild(apply);
        rowHelper.addChild(flag);
        rowHelper.addChild(impulseScript);
        rowHelper.addChild(variables);
        trackEditions.add(flag);
        trackEditions.add(apply);
        trackEditions.add(impulseScript);
        trackEditions.add(variables);
        trackEditionsVisible(false);
    }

    private void springEditionsVisible(boolean visible) {
        for (AbstractWidget widget : springEditions) {
            widget.visible = visible;
        }
        springEditionVisible = visible;
        springEditionBoxesVisible();
        updateSpringTypeName();
    }

    private void updateSpringTypeName() {
        if (onModifySpring != null) {
            String simpleName = onModifySpring.getClass().getSimpleName();
            springTypeName.str = "Spring Type: " + simpleName;
        } else {
            springTypeName.str = "Spring Type: None";
        }
    }

    private void springEditionBoxesVisible() {
        if (onModifySpring != null && springEditionVisible) {
            stiffness.visible = true;
            dampingForward.visible = true;
            dampingBackward.visible = true;
            upperLimit.visible = onModifySpring instanceof ClampedSpring;
            lowerLimit.visible = upperLimit.visible;
        } else {
            stiffness.visible = false;
            dampingForward.visible = false;
            dampingBackward.visible = false;
            upperLimit.visible = false;
            lowerLimit.visible = false;
        }
    }

    private void trackEditionsVisible(boolean visible) {
        for (AbstractWidget widget : trackEditions) {
            widget.visible = visible;
        }
        trackEditionVisible = visible;
    }

    private class TrackBox extends EditBox {

        public TrackBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
            super(pFont, pX, pY, pWidth, pHeight, pMessage);
            this.setMaxLength(2);
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            super.onClick(pMouseX, pMouseY);
            onTrackBoxClick(this);
        }
    }

    private void initLabels() {
        labels.add(new Label("X trans", 8, 30, 0xff0000));
        labels.add(new Label("Y trans", 8, 45, 0x00ff00));
        labels.add(new Label("Z trans", 8, 60, 0x0000ff));
        labels.add(new Label("X rot", 8, 75, 0xff0000));
        labels.add(new Label("Y rot", 8, 90, 0x00ff00));
        labels.add(new Label("Z rot", 8, 105, 0x0000ff));
    }

    private static class Label {
        public String str;
        public int color;
        public int x, y;

        public Label(String str, int x, int y, int color) {
            this.str = str;
            this.color = color;
            this.x = x;
            this.y = y;
        }

        public Label(String str, int x, int y) {
            this(str, x, y,0xffffff);
        }

        public void render(GuiGraphics graphics) {
            graphics.drawString(Minecraft.getInstance().font, str, x, y, color);
        }
    }

    static {
        springFactory.put("MassDampingSpring", () -> new MassDampingSpring("0", "0", "0", "0"));
        springFactory.put("ClampedSpring", () -> new ClampedSpring("0", "0", "0", "0", "0","0"));
        springFactory.put("SteadyStateSpring", () -> new SteadyStateSpring("0", "0", "0", "0"));

        colorMap.put("A1", 0xFF5733); // 鲜艳橙红
        colorMap.put("A2", 0xE74C3C); // 深红色
        colorMap.put("A3", 0xFF8C00); // 橙色
        colorMap.put("A4", 0xFFD700); // 金黄色
        colorMap.put("A5", 0xFFA500); // 标准橙色
        colorMap.put("A6", 0xF4A460); // 沙褐色

        // B 组：绿-青-蓝系
        colorMap.put("B1", 0x2ECC71); // 亮绿色
        colorMap.put("B2", 0x27AE60); // 深绿色
        colorMap.put("B3", 0x1ABC9C); // 亮青色
        colorMap.put("B4", 0x3498DB); // 天蓝色
        colorMap.put("B5", 0x2980B9); // 深蓝色
        colorMap.put("B6", 0x0E4D92); // 皇家蓝

        // C 组：紫-粉-棕-灰系
        colorMap.put("C1", 0x9B59B6); // 紫罗兰
        colorMap.put("C2", 0x8E44AD); // 深紫色
        colorMap.put("C3", 0xFF69B4); // 热粉色
        colorMap.put("C4", 0xD2691E); // 巧克力色
        colorMap.put("C5", 0xA52A2A); // 棕色
        colorMap.put("C6", 0x7F8C8D); // 深灰色
    }
}
