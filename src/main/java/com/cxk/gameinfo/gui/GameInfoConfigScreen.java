package com.cxk.gameinfo.gui;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class GameInfoConfigScreen extends Screen {
    private final Screen parent;
    private GameInfoConfig config;
    
    private ButtonWidget fpsButton;
    private ButtonWidget timeButton;
    private ButtonWidget coordButton;
    private ButtonWidget netherButton;
    private ButtonWidget biomeButton;
    private ButtonWidget remarkButton;
    private TextFieldWidget xPosField;
    private TextFieldWidget yPosField;
    private TextFieldWidget colorField;
    private SliderWidget scaleSlider;

    public GameInfoConfigScreen(Screen parent) {
        super(Text.literal("游戏信息配置"));
        this.parent = parent;
        this.config = GameinfoClient.config;
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
            .dimensions(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight)
            .build());

        this.timeButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("时间显示: " + (config.showTimeAndDays ? "开启" : "关闭")),
            button -> {
                config.showTimeAndDays = !config.showTimeAndDays;
                button.setMessage(Text.literal("时间显示: " + (config.showTimeAndDays ? "开启" : "关闭")));
            })
            .dimensions(centerX - buttonWidth/2, startY + spacing, buttonWidth, buttonHeight)
            .build());

        this.coordButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("坐标显示: " + (config.showCoordinates ? "开启" : "关闭")),
            button -> {
                config.showCoordinates = !config.showCoordinates;
                button.setMessage(Text.literal("坐标显示: " + (config.showCoordinates ? "开启" : "关闭")));
            })
            .dimensions(centerX - buttonWidth/2, startY + spacing * 2, buttonWidth, buttonHeight)
            .build());

        this.netherButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("下界坐标: " + (config.showNetherCoordinates ? "开启" : "关闭")),
            button -> {
                config.showNetherCoordinates = !config.showNetherCoordinates;
                button.setMessage(Text.literal("下界坐标: " + (config.showNetherCoordinates ? "开启" : "关闭")));
            })
            .dimensions(centerX - buttonWidth/2, startY + spacing * 3, buttonWidth, buttonHeight)
            .build());

        this.biomeButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("群系显示: " + (config.showBiome ? "开启" : "关闭")),
            button -> {
                config.showBiome = !config.showBiome;
                button.setMessage(Text.literal("群系显示: " + (config.showBiome ? "开启" : "关闭")));
            })
            .dimensions(centerX - buttonWidth/2, startY + spacing * 4, buttonWidth, buttonHeight)
            .build());

        this.remarkButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("标注显示: " + (config.remark ? "开启" : "关闭")),
            button -> {
                config.remark = !config.remark;
                button.setMessage(Text.literal("标注显示: " + (config.remark ? "开启" : "关闭")));
            })
            .dimensions(centerX - buttonWidth/2, startY + spacing * 5, buttonWidth, buttonHeight)
            .build());

        // 位置设置
        this.xPosField = new TextFieldWidget(this.textRenderer, centerX - 80, startY + spacing * 6, 60, 20, Text.literal("X位置"));
        this.xPosField.setText(String.valueOf(config.xPos));
        this.xPosField.setChangedListener(text -> {
            try {
                config.xPos = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(this.xPosField);

        this.yPosField = new TextFieldWidget(this.textRenderer, centerX + 20, startY + spacing * 6, 60, 20, Text.literal("Y位置"));
        this.yPosField.setText(String.valueOf(config.yPos));
        this.yPosField.setChangedListener(text -> {
            try {
                config.yPos = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(this.yPosField);

        // 颜色设置
        this.colorField = new TextFieldWidget(this.textRenderer, centerX - buttonWidth/2, startY + spacing * 7, buttonWidth, 20, Text.literal("颜色"));
        this.colorField.setText(String.format("%06X", config.color & 0xFFFFFF));
        this.colorField.setChangedListener(text -> {
            try {
                if (text.length() == 6) {
                    config.color = (int) Long.parseLong("FF" + text, 16);
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(this.colorField);

        // 缩放滑块
        this.scaleSlider = this.addDrawableChild(new SliderWidget(centerX - buttonWidth/2, startY + spacing * 8, buttonWidth, 20, Text.literal("大小: " + String.format("%.1f", config.scale)), config.scale) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("大小: " + String.format("%.1f", this.value)));
            }

            @Override
            protected void applyValue() {
                config.scale = this.value;
            }
        });

        // 保存和取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("保存"),
            button -> {
                config.saveConfig();
                this.close();
            })
            .dimensions(centerX - 105, startY + spacing * 10, 100, 20)
            .build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("取消"),
            button -> this.close())
            .dimensions(centerX + 5, startY + spacing * 10, 100, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // 绘制位置标签
        context.drawTextWithShadow(this.textRenderer, "X位置:", this.width / 2 - 120, 40 + 25 * 6 + 5, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "Y位置:", this.width / 2 - 20, 40 + 25 * 6 + 5, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "颜色(十六进制):", this.width / 2 - 100, 40 + 25 * 7 - 10, 0xFFFFFF);
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