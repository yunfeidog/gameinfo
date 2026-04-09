package com.cxk.gameinfo.gui;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GameInfoConfigScreen extends Screen {
    private final Screen parent;
    private GameInfoConfig config;
    
    // Tab相关
    private int currentTab = 0;
    private final List<TabButton> tabButtons = new ArrayList<>();
    private final String[] tabNames = {"信息开关", "颜色设置"};
    
    // 信息开关Tab的组件
    private Button fpsButton;
    private Button timeButton;
    private Button coordButton;
    private Button netherButton;
    private Button biomeButton;
    private Button remarkButton;
    private Button showEquipmentButton;
    private Button showFurnaceInfoButton;
    private Button showEntityInfoButton;
    private Button showBlockInfoButton;

    private Button enableButton;
    
    // 颜色设置Tab的组件
    private EditBox xPosField;
    private EditBox yPosField;
    private Button colorButton;

    public GameInfoConfigScreen(Screen parent) {
        super(Component.literal("游戏信息配置"));
        this.parent = parent;
        this.config = GameinfoClient.config;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        
        // 创建Tab按钮
        createTabButtons();
        
        // 根据当前Tab创建内容
        if (currentTab == 0) {
            createInfoToggleTab();
        } else if (currentTab == 1) {
            createColorSettingTab();
        }
        
        // 创建底部按钮
        createBottomButtons();
    }
    
    private void createTabButtons() {
        tabButtons.clear();
        int centerX = this.width / 2;
        int tabWidth = 80;
        int tabHeight = 20;
        int startX = centerX - (tabNames.length * tabWidth) / 2;
        
        for (int i = 0; i < tabNames.length; i++) {
            final int tabIndex = i;
            TabButton tabButton = new TabButton(
                startX + i * tabWidth, 35, 
                tabWidth, tabHeight, 
                tabNames[i], 
                tabIndex == currentTab,
                () -> switchTab(tabIndex)
            );
            tabButtons.add(tabButton);
            this.addRenderableWidget(tabButton);
        }
    }
    
    private void switchTab(int newTab) {
        if (newTab != currentTab) {
            currentTab = newTab;
            this.init();
        }
    }
    
    private void createInfoToggleTab() {
        int centerX = this.width / 2;
        int startY = 80;
        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 25;
        int sideMargin = 40;
        
        // 左列
        int leftX = centerX - buttonWidth - sideMargin;
        this.fpsButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "帧数显示", () -> config.showFPS, value -> config.showFPS = value,
                leftX, startY, buttonWidth, buttonHeight));

        this.timeButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "时间显示", () -> config.showTimeAndDays, value -> config.showTimeAndDays = value,
                leftX, startY + spacing, buttonWidth, buttonHeight));

        this.coordButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "坐标显示", () -> config.showCoordinates, value -> config.showCoordinates = value,
                leftX, startY + spacing * 2, buttonWidth, buttonHeight));

        this.netherButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "下界坐标", () -> config.showNetherCoordinates, value -> config.showNetherCoordinates = value,
                leftX, startY + spacing * 3, buttonWidth, buttonHeight));

//        this.showFurnaceInfoButton = this.addDrawableChild(GuiHelper.createToggleButton(
//                "熔炉信息", () -> config.showFurnaceInfo, value -> config.showFurnaceInfo = value,
//                leftX, startY + spacing * 4, buttonWidth, buttonHeight));
        this.enableButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "启用游戏信息", () -> config.enabled, value -> {
                    config.enabled = value;
                    GameinfoClient.logger("启用游戏信息: " + value);
                },
                leftX, startY + spacing * 4, buttonWidth, buttonHeight));

        // 右列
        int rightX = centerX + sideMargin;
        this.biomeButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "群系显示", () -> config.showBiome, value -> config.showBiome = value,
                rightX, startY, buttonWidth, buttonHeight));

        this.showEquipmentButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "装备显示", () -> config.showEquipment, value -> config.showEquipment = value,
                rightX, startY + spacing, buttonWidth, buttonHeight));

        this.remarkButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "版本显示", () -> config.remark, value -> config.remark = value,
                rightX, startY + spacing * 2, buttonWidth, buttonHeight));

        this.showEntityInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "生物信息", () -> config.showEntityInfo, value -> config.showEntityInfo = value,
                rightX, startY + spacing * 3, buttonWidth, buttonHeight));

        this.showBlockInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "方块信息", () -> config.showBlockInfo, value -> config.showBlockInfo = value,
                rightX, startY + spacing * 4, buttonWidth, buttonHeight));
    }
    
    private void createColorSettingTab() {
        int centerX = this.width / 2;
        int startY = 100;
        int spacing = 30;
        
        // 位置设置
        this.xPosField = this.addRenderableWidget(GuiHelper.createNumberField(
                this.font, "X位置", () -> config.xPos, value -> config.xPos = value,
                centerX - 65, startY, 50, 20));

        this.yPosField = this.addRenderableWidget(GuiHelper.createNumberField(
                this.font, "Y位置", () -> config.yPos, value -> config.yPos = value,
                centerX + 15, startY, 50, 20));

        // 颜色选择按钮
        this.colorButton = this.addRenderableWidget(Button.builder(
                Component.literal("选择颜色"),
                button -> this.minecraft.setScreen(new ColorPickerScreen(this, (color, colorName) -> {
                    config.color = color;
                })))
                .bounds(centerX - 50, startY + spacing, 100, 20)
                .build());
    }
    
    private void createBottomButtons() {
        int centerX = this.width / 2;
        int bottomY = this.height - 40;
        
        this.addRenderableWidget(GuiHelper.createButton("保存", () -> {
            config.saveConfig();
            this.onClose();
        }, centerX - 55, bottomY, 50, 20));

        this.addRenderableWidget(GuiHelper.createButton("取消", this::onClose,
                centerX + 5, bottomY, 50, 20));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // 绘制半透明背景
        context.fill(0, 0, this.width, this.height, 0x50000000);
        
        super.extractRenderState(context, mouseX, mouseY, delta);

        // 绘制标题
        context.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFD700);
        
        // 绘制Tab内容区域背景
        int tabContentY = 60;
        int tabContentHeight = this.height - 110;
        context.fill(20, tabContentY, this.width - 20, tabContentY + tabContentHeight, 0x20000000);
//        context.drawBorder(20, tabContentY, this.width - 40, tabContentHeight, 0xFF666666);

        // 根据当前Tab绘制特定内容
        if (currentTab == 0) {
            renderInfoToggleTab(context);
        } else if (currentTab == 1) {
            renderColorSettingTab(context);
        }
    }
    
    private void renderInfoToggleTab(GuiGraphicsExtractor context) {
        int centerX = this.width / 2;
        context.centeredText(this.font, "选择要显示的信息", centerX, 70, 0xFFAAFFAA);
    }
    
    private void renderColorSettingTab(GuiGraphicsExtractor context) {
        int centerX = this.width / 2;
        int startY = 100;
        int spacing = 30;
        
        // 绘制标题
        context.centeredText(this.font, "自定义显示位置和颜色", centerX, 70, 0xFFAAFFAA);
        
        // 绘制位置设置区域背景
        int bgX = centerX - 150;
        int bgY = startY - 10;
        int bgWidth = 300;
        int bgHeight = 100;
        context.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0x30333333);
//        context.drawBorder(bgX, bgY, bgWidth, bgHeight, 0xFF444444);
        
        // 绘制X和Y标签
        context.text(this.font, "X:", centerX - 85, startY + 4, 0xFFFFFFFF);
        context.text(this.font, "Y:", centerX - 5, startY + 4, 0xFFFFFFFF);
        
        // 绘制颜色预览
        int colorSize = 24;
        int colorX = centerX + 65;
        int colorY = startY + spacing - 2;
        
        context.fill(colorX - 1, colorY - 1, colorX + colorSize + 1, colorY + colorSize + 1, 0xFF000000);
        context.fill(colorX, colorY, colorX + colorSize, colorY + colorSize, config.color);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    // Tab按钮类
    private static class TabButton extends Button {
        private final boolean isActive;
        
        public TabButton(int x, int y, int width, int height, String text, boolean isActive, Runnable onPress) {
            super(x, y, width, height, net.minecraft.network.chat.Component.literal(text), button -> onPress.run(), DEFAULT_NARRATION);
            this.isActive = isActive;
        }
        

        @Override
        protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
            // 根据状态选择颜色
            int bgColor = isActive ? 0xFF4A4A4A : 0xFF2A2A2A;
            int borderColor = isActive ? 0xFFFFFFFF : 0xFF666666;
            int textColor = isActive ? 0xFFFFD700 : 0xFFCCCCCC;

            if (this.isHovered() && !isActive) {
                bgColor = 0xFF3A3A3A;
            }

            // 绘制Tab背景
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);

            // 绘制边框（活跃Tab不绘制底边）
//            context.drawBorder(this.getX(), this.getY(), this.width, this.height, borderColor);
            if (isActive) {
                context.fill(this.getX() + 1, this.getY() + this.height - 1, this.getX() + this.width - 1, this.getY() + this.height, bgColor);
            }

            // 绘制文本
            int textX = this.getX() + this.width / 2;
            int textY = this.getY() + (this.height - 8) / 2;
            context.centeredText(Minecraft.getInstance().font, this.getMessage(), textX, textY, textColor);
        }
    }
}
