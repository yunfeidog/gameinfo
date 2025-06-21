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

        // 根据当前颜色设置颜色名
        updateColorName();
    }

    private void updateColorName() {
        // 根据颜色值确定颜色名称
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

        // 显示选项按钮
        this.fpsButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("帧数显示: " + (config.showFPS ? "开启" : "关闭")),
                        button -> {
                            config.showFPS = !config.showFPS;
                            button.setMessage(Text.literal("帧数显示: " + (config.showFPS ? "开启" : "关闭")));
                        })
                .dimensions(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight)
                .build());

        this.timeButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("时间显示: " + (config.showTimeAndDays ? "开启" : "关闭")),
                        button -> {
                            config.showTimeAndDays = !config.showTimeAndDays;
                            button.setMessage(Text.literal("时间显示: " + (config.showTimeAndDays ? "开启" : "关闭")));
                        })
                .dimensions(centerX - buttonWidth / 2, startY + spacing, buttonWidth, buttonHeight)
                .build());

        this.coordButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("坐标显示: " + (config.showCoordinates ? "开启" : "关闭")),
                        button -> {
                            config.showCoordinates = !config.showCoordinates;
                            button.setMessage(Text.literal("坐标显示: " + (config.showCoordinates ? "开启" : "关闭")));
                        })
                .dimensions(centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight)
                .build());

        this.netherButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("下界坐标: " + (config.showNetherCoordinates ? "开启" : "关闭")),
                        button -> {
                            config.showNetherCoordinates = !config.showNetherCoordinates;
                            button.setMessage(Text.literal("下界坐标: " + (config.showNetherCoordinates ? "开启" : "关闭")));
                        })
                .dimensions(centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, buttonHeight)
                .build());

        this.biomeButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("群系显示: " + (config.showBiome ? "开启" : "关闭")),
                        button -> {
                            config.showBiome = !config.showBiome;
                            button.setMessage(Text.literal("群系显示: " + (config.showBiome ? "开启" : "关闭")));
                        })
                .dimensions(centerX - buttonWidth / 2, startY + spacing * 4, buttonWidth, buttonHeight)
                .build());

        this.showEquipmentButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("装备显示: " + (config.showEquipment ? "开启" : "关闭")),
                        button -> {
                            config.showEquipment = !config.showEquipment;
                            button.setMessage(Text.literal("装备显示: " + (config.showEquipment ? "开启" : "关闭")));
                        })
                .dimensions(centerX - buttonWidth / 2, startY + spacing * 5, buttonWidth, buttonHeight)
                .build());

        this.remarkButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("版本显示: " + (config.remark ? "开启" : "关闭")),
                        button -> {
                            config.remark = !config.remark;
                            button.setMessage(Text.literal("版本显示: " + (config.remark ? "开启" : "关闭")));
                        })
                .dimensions(centerX - buttonWidth / 2, startY + spacing * 5, buttonWidth, buttonHeight)
                .build());


        // X Y位置设置
        this.xPosField = new TextFieldWidget(this.textRenderer, centerX - 80, startY + spacing * 6, 60, 20, Text.literal("X位置"));
        this.xPosField.setText(String.valueOf(config.xPos));
        this.xPosField.setChangedListener(text -> {
            try {
                config.xPos = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
            }
        });
        this.addDrawableChild(this.xPosField);

        this.yPosField = new TextFieldWidget(this.textRenderer, centerX + 20, startY + spacing * 6, 60, 20, Text.literal("Y位置"));
        this.yPosField.setText(String.valueOf(config.yPos));
        this.yPosField.setChangedListener(text -> {
            try {
                config.yPos = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
            }
        });
        this.addDrawableChild(this.yPosField);

        // 颜色选择按钮 - 打开颜色选择器
        this.colorButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("选择颜色: " + currentColorName),
                        button -> {
                            this.client.setScreen(new ColorPickerScreen(this, (color, colorName) -> {
                                config.color = color;
                                currentColorName = colorName;
                                button.setMessage(Text.literal("选择颜色: " + colorName));
                            }));
                        })
                .dimensions(centerX - buttonWidth / 2, startY + spacing * 7, buttonWidth, buttonHeight)
                .build());

        // 保存和取消按钮
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("保存设置"),
                        button -> {
                            config.saveConfig();
                            this.close();
                        })
                .dimensions(centerX - 105, startY + spacing * 8 + 10, 100, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("取消"),
                        button -> this.close())
                .dimensions(centerX + 5, startY + spacing * 8 + 10, 100, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        int centerX = this.width / 2;
        int startY = 40;
        int spacing = 25;
        int buttonWidth = 200;

        // 绘制X和Y输入框的标签
        context.drawTextWithShadow(this.textRenderer, "X坐标:", centerX - 115, startY + spacing * 6 + 5, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "Y坐标:", centerX - 15, startY + spacing * 6 + 5, 0xFFFFFF);

        // 绘制颜色预览方块
        int colorPreviewX = centerX + buttonWidth / 2 - 25;
        int colorPreviewY = startY + spacing * 7 + 2;
        context.fill(colorPreviewX, colorPreviewY, colorPreviewX + 16, colorPreviewY + 16, config.color);
        context.drawBorder(colorPreviewX, colorPreviewY, 16, 16, 0xFF000000);

        // 绘制提示文本
        context.drawCenteredTextWithShadow(this.textRenderer, "点击按钮进行设置，修改后记得保存", this.width / 2, this.height - 30, 0xAAAAAA);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}