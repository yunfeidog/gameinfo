package com.cxk.gameinfo.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class ColorPickerScreen extends Screen {
    private final Screen parent;
    private final ColorSelectedCallback callback;

    // 颜色网格 4x8 = 32种颜色，更多选择
    private final int[] colorPalette = {
            // 第一行：灰度色
            0xFFFFFFFF, 0xFFE0E0E0, 0xFFC0C0C0, 0xFFA0A0A0, 0xFF808080, 0xFF606060, 0xFF404040, 0xFF000000,
            // 第二行：红色系
            0xFFFF0000, 0xFFFF4040, 0xFFFF8080, 0xFFFF4000, 0xFFCC0000, 0xFF800000, 0xFF400000, 0xFF800040,
            // 第三行：绿色系
            0xFF00FF00, 0xFF40FF40, 0xFF80FF80, 0xFF00FF40, 0xFF00CC00, 0xFF008000, 0xFF004000, 0xFF408000,
            // 第四行：蓝色系
            0xFF0000FF, 0xFF4040FF, 0xFF8080FF, 0xFF0040FF, 0xFF0000CC, 0xFF000080, 0xFF000040, 0xFF004080
    };

    // 自定义颜色输入框
    private TextFieldWidget colorInputField;
    private ButtonWidget confirmCustomButton;
    private int previewColor = 0xFFFFFFFF;

    public interface ColorSelectedCallback {
        void onColorSelected(int color, String colorName);
    }

    public ColorPickerScreen(Screen parent, ColorSelectedCallback callback) {
        super(Text.literal("选择颜色"));
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    protected void init() {
        // 计算布局参数
        int colorSize = 32;
        int spacing = 8;
        int cols = 8;
        int rows = 4;

        // 计算网格总尺寸
        int gridWidth = cols * colorSize + (cols - 1) * spacing;
        int gridHeight = rows * colorSize + (rows - 1) * spacing;

        // 居中放置网格
        int startX = (this.width - gridWidth) / 2;
        int startY = 50;

        // 创建颜色按钮网格
        for (int i = 0; i < colorPalette.length; i++) {
            int row = i / cols;
            int col = i % cols;
            int x = startX + col * (colorSize + spacing);
            int y = startY + row * (colorSize + spacing);

            final int colorIndex = i;
            this.addDrawableChild(new ColorButton(x, y, colorSize, colorSize, colorPalette[i],
                    button -> {
                        callback.onColorSelected(colorPalette[colorIndex], "");
                        this.close();
                    }));
        }

        // 自定义颜色输入区域
        int customY = startY + gridHeight + 25;
        int inputWidth = 100;
        int inputHeight = 20;

        // 计算水平布局
        int totalElementWidth = inputWidth + 25 + 20 + 10 + 50; // 输入框 + 间距 + 预览框 + 间距 + 按钮
        int elementStartX = (this.width - totalElementWidth) / 2;

        // ARGB输入框
        this.colorInputField = new TextFieldWidget(this.textRenderer,
                elementStartX, customY + 20, inputWidth, inputHeight,
                Text.literal("ARGB颜色"));
        this.colorInputField.setPlaceholder(Text.literal("FFFFFFFF"));
        this.colorInputField.setText("FFFFFFFF");
        this.colorInputField.setChangedListener(this::onColorInputChanged);
        this.addDrawableChild(this.colorInputField);

        // 确认自定义颜色按钮
        this.confirmCustomButton = this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("使用"),
                        button -> {
                            try {
                                String input = this.colorInputField.getText().trim();
                                if (input.length() == 8) {
                                    int color = (int) Long.parseLong(input, 16);
                                    callback.onColorSelected(color, "");
                                    this.close();
                                }
                            } catch (NumberFormatException e) {
                                // 输入格式错误，不做任何操作
                            }
                        })
                .dimensions(elementStartX + inputWidth + 25 + 20 + 10, customY + 20, 50, 20)
                .build());

        // 底部按钮
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("取消"),
                        button -> this.close())
                .dimensions(elementStartX + inputWidth + 25 + 20 + 10, customY + 50, 50, 20)
                .build());
    }

    private void onColorInputChanged(String input) {
        try {
            if (input.length() == 8) {
                this.previewColor = (int) Long.parseLong(input, 16);
            }
        } catch (NumberFormatException e) {
            // 输入格式错误时保持之前的预览颜色
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 绘制半透明背景
        context.fill(0, 0, this.width, this.height, 0x80000000);

        super.render(context, mouseX, mouseY, delta);

        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFD700);

        // 绘制预设颜色区域标题
        context.drawCenteredTextWithShadow(this.textRenderer, "预设颜色", this.width / 2, 35, Colors.WHITE);

        // 绘制自定义颜色区域
        renderCustomColorArea(context);
    }

    private void renderCustomColorArea(DrawContext context) {
        int customY = 50 + 4 * (32 + 8) - 8 + 25; // 与init中的customY保持一致

        // 绘制自定义颜色区域背景
        int bgX = this.width / 2 - 130;
        int bgWidth = 260;
        int bgHeight = 80;
        context.fill(bgX, customY - 5, bgX + bgWidth, customY + bgHeight, 0x30333333);
        context.drawBorder(bgX, customY - 5, bgWidth, bgHeight, 0xFF666666);

        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, "自定义颜色 (ARGB格式)", this.width / 2, customY + 8, 0xFFAAFFAA);

        // 计算预览框位置（与init中的布局保持一致）
        int inputWidth = 100;
        int totalElementWidth = inputWidth + 25 + 20 + 10 + 50;
        int elementStartX = (this.width - totalElementWidth) / 2;

        // 绘制颜色预览
        int previewSize = 20;
        int previewX = elementStartX + inputWidth + 25;
        int previewY = customY + 20;

        // 预览框边框
        context.fill(previewX - 1, previewY - 1, previewX + previewSize + 1, previewY + previewSize + 1, 0xFF000000);
        // 预览颜色
        context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, this.previewColor);

        // 绘制说明文本
        context.drawTextWithShadow(this.textRenderer, "格式: AARRGGBB 例如: FFFF0000", this.width / 2 - 120, customY + 50, 0xFF888888);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 自定义颜色按钮类
    private static class ColorButton extends ButtonWidget {
        private final int color;

        public ColorButton(int x, int y, int width, int height, int color, PressAction onPress) {
            super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
            this.color = color;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            // 绘制颜色方块
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, this.color);

            // 绘制边框
            int borderColor = this.isHovered() ? 0xFFFFFFFF : 0xFF333333;
            context.drawBorder(this.getX(), this.getY(), this.width, this.height, borderColor);

            // 如果悬停，绘制高亮边框
            if (this.isHovered()) {
                context.drawBorder(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, 0xFFFFD700);
            }
        }
    }
} 