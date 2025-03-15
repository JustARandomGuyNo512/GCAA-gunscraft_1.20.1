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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.animation.recoilAnimation.*;
import sheridan.gcaa.items.gun.IGun;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RecoilModifyScreen extends Screen {
    public NewRecoilData newRecoilData;
    public IGun gun;
    public Map<String, List<TrackBox>> trackMap = new HashMap<>();
    public Map<String, NewRecoilData.Track> trackPool = new HashMap<>();
    public List<AbstractWidget> springEditions = new ArrayList<>();
    public List<AbstractWidget> trackEditions = new ArrayList<>();
    public int mx, my;
    public EditBox impulseScript;
    public Button selectedSpringBtn;
    public String selectedSpringBtnName;
    public Button variables;
    public Button clampedSpringBtn;
    public Button massDampingSpring;
    public Button steadyStateSpring;
    public boolean hide = false;

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderBackground(pGuiGraphics);
        Font font = Minecraft.getInstance().font;
        pGuiGraphics.drawString(font, "X trans",8, 30, 0xff0000);
        pGuiGraphics.drawString(font, "Y trans",8, 45, 0x00ff00);
        pGuiGraphics.drawString(font, "Z trans",8, 60, 0x0000ff);
        pGuiGraphics.drawString(font, "X rot",8, 75, 0xff0000);
        pGuiGraphics.drawString(font, "Y rot",8, 90, 0x00ff00);
        pGuiGraphics.drawString(font, "Z rot",8, 105, 0x0000ff);

        pGuiGraphics.drawString(font, mx + " " + my, mx + font.width("."), my - font.lineHeight, 0xffffff);
        pGuiGraphics.hLine(0, this.width, my, FastColor.ABGR32.color(255,0,255,0));
        pGuiGraphics.vLine(mx, 0, this.height, FastColor.ABGR32.color(255,0,0,255));
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
        }).size(20, 16).pos(65, 8).build());
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    public RecoilModifyScreen() {
        super(Component.literal("model recoil effect modify"));
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics) {

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
            rowHelper.addChild(Button.builder(Component.literal("A" + (i + 1))
                    .withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB())), (b) -> {
                onSpringBtnClick("A" + (finalI + 1), b);
            }).size(20, 12).pos(240 + i * 25, 30).build());
        }
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            rowHelper.addChild(Button.builder(Component.literal("B" + (i + 1))
                    .withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB())), (b) -> {
                onSpringBtnClick("B" + (finalI + 1), b);
            }).size(20, 12).pos(240 + i * 25, 45).build());
        }
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            rowHelper.addChild(Button.builder(Component.literal("C" + (i + 1))
                    .withStyle(Style.EMPTY.withColor(Color.GRAY.getRGB())), (b) -> {
                onSpringBtnClick("C" + (finalI + 1), b);
            }).size(20, 12).pos(240 + i * 25, 60).build());
        }
    }

    private void onSpringBtnClick(String name, Button btn) {
        selectedSpringBtn = btn;
        selectedSpringBtnName = name;
        NewRecoilData.Track track = trackPool.get(name);
        if (track == null) {
            // create new track
        } else {
            //modify track
        }
    }

    private void onTrackBoxClick(EditBox box) {

    }

    private void initRecoilData(GridLayout.RowHelper rowHelper) {
        EditBox back = new EditBox(Minecraft.getInstance().font, 40, 200, 30, 12, Component.literal(""));
        back.setTooltip(Tooltip.create(Component.literal("Back")));
        EditBox upRot = new EditBox(Minecraft.getInstance().font, 75, 200, 30, 12, Component.literal(""));
        upRot.setTooltip(Tooltip.create(Component.literal("Up Rot")));
        EditBox randomX = new EditBox(Minecraft.getInstance().font, 110, 200, 30, 12, Component.literal(""));
        randomX.setTooltip(Tooltip.create(Component.literal("Random X")));
        EditBox randomY = new EditBox(Minecraft.getInstance().font, 145, 200, 30, 12, Component.literal(""));
        randomY.setTooltip(Tooltip.create(Component.literal("Random Y")));
        EditBox shake = new EditBox(Minecraft.getInstance().font, 180, 200, 30, 12, Component.literal(""));
        shake.setTooltip(Tooltip.create(Component.literal("Shake")));
        rowHelper.addChild(back);
        rowHelper.addChild(upRot);
        rowHelper.addChild(randomX);
        rowHelper.addChild(randomY);
        rowHelper.addChild(shake);
        rowHelper.addChild(Button.builder(Component.literal("Read"), (b) -> {

        }).size(25, 12).pos(40, 180).tooltip(Tooltip.create(Component.literal("Read from memory"))).build());
        rowHelper.addChild(Button.builder(Component.literal("Apply"), (b) -> {

        }).size(25, 12).pos(80, 180).tooltip(Tooltip.create(Component.literal("Apply to recoil data in memory"))).build());
    }

    private void initSpringEditions(GridLayout.RowHelper rowHelper) {
        Button apply = Button.builder(Component.literal("OK"), (b) -> {

        }).size(20, 12).pos(240, 85).tooltip(Tooltip.create(Component.literal("Apply to spring data in memory"))).build();
        clampedSpringBtn = Button.builder(Component.literal("ClampedSpring"), (b) -> {

        }).size(70, 12).pos(240, 100).tooltip(Tooltip.create(Component.literal("ClampedSpring"))).build();
        massDampingSpring = Button.builder(Component.literal("MassDampingSpring"), (b) -> {
        }).size(70, 12).pos(315, 100).tooltip(Tooltip.create(Component.literal("MassDampingSpring"))).build();
        steadyStateSpring = Button.builder(Component.literal("SteadyStateSpring"), (b) -> {

        }).size(70, 12).pos(240, 115).tooltip(Tooltip.create(Component.literal("SteadyStateSpring"))).build();

        rowHelper.addChild(apply);
        rowHelper.addChild(clampedSpringBtn);
        rowHelper.addChild(massDampingSpring);
        rowHelper.addChild(steadyStateSpring);
        springEditions.add(apply);
        springEditions.add(clampedSpringBtn);
        springEditions.add(massDampingSpring);
        springEditions.add(steadyStateSpring);
        springEditionsVisible(false);
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
        impulseScript.setTooltip(Tooltip.create(Component.literal("Input impulse script here, example: 'Back*(A2.Steady*0.5+0.5)+UpRot'")));

        rowHelper.addChild(apply);
        rowHelper.addChild(flag);
        rowHelper.addChild(impulseScript);
        trackEditions.add(flag);
        trackEditions.add(apply);
        trackEditions.add(impulseScript);
    }

    private void springEditionsVisible(boolean visible) {
        for (AbstractWidget widget : springEditions) {
            widget.visible = visible;
        }
    }

    private void trackEditionsVisible(boolean visible) {
        for (AbstractWidget widget : trackEditions) {
            widget.visible = visible;
        }
    }

    private class TrackBox extends EditBox {

        public TrackBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
            super(pFont, pX, pY, pWidth, pHeight, pMessage);
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            super.onClick(pMouseX, pMouseY);
            onTrackBoxClick(this);
        }
    }
}
