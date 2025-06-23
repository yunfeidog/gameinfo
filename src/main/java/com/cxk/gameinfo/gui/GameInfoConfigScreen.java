package com.cxk.gameinfo.gui;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class GameInfoConfigScreen extends Screen {
    private final Screen parent;
    private GameInfoConfig config;

    private ButtonWidget fpsButton;
    private ButtonWidget timeButton;
    private ButtonWidget coordButton;
    private ButtonWidget netherButton;
    private ButtonWidget biomeButton;
    private ButtonWidget remarkButton;
    private ButtonWidget showEquipmentButton;

    private TextFieldWidget xPosField;
    private TextFieldWidget yPosField;
    private ButtonWidget colorButton;

    private String currentColorName = "黄色"; // 默认颜色名

    public GameInfoConfigScreen(Screen parent) {
        super(Text.literal("游戏信息配置"));
        this.parent = parent;
        this.config = GameinfoClient.config;
        updateColorName();
    }

    // 创建颜色选择按钮
    private ButtonWidget createColorButton(String labelPrefix, int x, int y, int width, int height) {
        return ButtonWidget.builder(
                        Text.literal(labelPrefix + ": " + currentColorName),
                        button -> this.client.setScreen(new ColorPickerScreen(this, (color, colorName) -> {
                            config.color = color;
                            currentColorName = colorName;
                            button.setMessage(Text.literal(labelPrefix + ": " + colorName));
                        })))
                .dimensions(x, y, width, height)
                .build();
    }

    private void updateColorName() {
        switch (config.color) {
            case 0xFFFFFFFF -> currentColorName = "白色";
            case 0xFFFFFF00 -> currentColorName = "黄色";
            case 0xFF00FF00 -> currentColorName = "绿色";
            case 0xFF00FFFF -> currentColorName = "青色";
            case 0xFFFF0000 -> currentColorName = "红色";
            case 0xFFFF00FF -> currentColorName = "紫色";
            case 0xFFFFA500 -> currentColorName = "橙色";
            case 0xFF808080 -> currentColorName = "灰色";
            default -> currentColorName = "自定义";
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 40;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 25;

        // 使用GuiHelper创建开关按钮
        this.fpsButton = this.addDrawableChild(GuiHelper.createToggleButton(
                "帧数显示", () -> config.showFPS, value -> config.showFPS = value,
                centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight));

        this.timeButton = this.addDrawableChild(GuiHelper.createToggleButton(
                "时间显示", () -> config.showTimeAndDays, value -> config.showTimeAndDays = value,
                centerX - buttonWidth / 2, startY + spacing, buttonWidth, buttonHeight));

        this.coordButton = this.addDrawableChild(GuiHelper.createToggleButton(
                "坐标显示", () -> config.showCoordinates, value -> config.showCoordinates = value,
                centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight));

        this.netherButton = this.addDrawableChild(GuiHelper.createToggleButton(
                "下界坐标", () -> config.showNetherCoordinates, value -> config.showNetherCoordinates = value,
                centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, buttonHeight));

        this.biomeButton = this.addDrawableChild(GuiHelper.createToggleButton(
                "群系显示", () -> config.showBiome, value -> config.showBiome = value,
                centerX - buttonWidth / 2, startY + spacing * 4, buttonWidth, buttonHeight));

        this.showEquipmentButton = this.addDrawableChild(GuiHelper.createToggleButton(
                "装备显示", () -> config.showEquipment, value -> config.showEquipment = value,
                centerX - buttonWidth / 2, startY + spacing * 5, buttonWidth, buttonHeight));

        this.remarkButton = this.addDrawableChild(GuiHelper.createToggleButton(
                "版本显示", () -> config.remark, value -> config.remark = value,
                centerX - buttonWidth / 2, startY + spacing * 6, buttonWidth, buttonHeight));

        // 使用GuiHelper创建数字输入框
        this.xPosField = this.addDrawableChild(GuiHelper.createNumberField(
                this.textRenderer, "X位置", () -> config.xPos, value -> config.xPos = value,
                centerX - 80, startY + spacing * 7, 60, 20));

        this.yPosField = this.addDrawableChild(GuiHelper.createNumberField(
                this.textRenderer, "Y位置", () -> config.yPos, value -> config.yPos = value,
                centerX + 20, startY + spacing * 8, 60, 20));

        // 使用自定义方法创建颜色选择按钮
        this.colorButton = this.addDrawableChild(createColorButton(
                "选择颜色", centerX - buttonWidth / 2, startY + spacing * 7, buttonWidth, buttonHeight));

        // 使用GuiHelper创建保存和取消按钮
        this.addDrawableChild(GuiHelper.createButton("保存设置", () -> {
            config.saveConfig();
            this.close();
        }, centerX - 105, startY + spacing * 8 + 10, 100, 20));

        this.addDrawableChild(GuiHelper.createButton("取消", this::close,
                centerX + 5, startY + spacing * 8 + 10, 100, 20));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFFFF);

        int centerX = this.width / 2;
        int startY = 40;
        int spacing = 25;
        int buttonWidth = 200;

        // 绘制X和Y输入框的标签
        context.drawTextWithShadow(this.textRenderer, "X坐标:", centerX - 115, startY + spacing * 6 + 5, 0xFFFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "Y坐标:", centerX - 15, startY + spacing * 6 + 5, 0xFFFFFFFF);

        // 绘制颜色预览方块
        int colorPreviewX = centerX + buttonWidth / 2 - 25;
        int colorPreviewY = startY + spacing * 7 + 2;
        context.fill(colorPreviewX, colorPreviewY, colorPreviewX + 16, colorPreviewY + 16, config.color);
        context.drawBorder(colorPreviewX, colorPreviewY, 16, 16, 0xFF000000);

        // 绘制提示文本
        context.drawCenteredTextWithShadow(this.textRenderer, "点击按钮进行设置，修改后记得保存", this.width / 2, this.height - 30, 0xFFAAAAAA);
    }
}