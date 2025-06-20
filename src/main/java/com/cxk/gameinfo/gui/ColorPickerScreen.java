package com.cxk.gameinfo.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class ColorPickerScreen extends Screen {
    private final Screen parent;
    private final ColorSelectedCallback callback;
    
    // 颜色网格 4x6 = 24种颜色
    private final int[] colorPalette = {
        0xFFFFFFFF, 0xFFC0C0C0, 0xFF808080, 0xFF404040, 0xFF000000, 0xFF800000,
        0xFFFF0000, 0xFFFF8000, 0xFFFFFF00, 0xFF80FF00, 0xFF00FF00, 0xFF00FF80,
        0xFF00FFFF, 0xFF0080FF, 0xFF0000FF, 0xFF8000FF, 0xFFFF00FF, 0xFFFF0080,
        0xFF800040, 0xFF804000, 0xFF808000, 0xFF408000, 0xFF008000, 0xFF004080
    };
    
    private final String[] colorNames = {
        "白色", "浅灰", "灰色", "深灰", "黑色", "深红",
        "红色", "橙色", "黄色", "浅绿", "绿色", "青绿",
        "青色", "浅蓝", "蓝色", "紫色", "粉色", "玫红",
        "棕红", "棕色", "橄榄", "深绿", "墨绿", "深蓝"
    };
    
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
        int startX = this.width / 2 - 150;
        int startY = 60;
        int colorSize = 40;
        int spacing = 10;
        
        // 创建颜色按钮网格 6列4行
        for (int i = 0; i < colorPalette.length; i++) {
            int row = i / 6;
            int col = i % 6;
            int x = startX + col * (colorSize + spacing);
            int y = startY + row * (colorSize + spacing);
            
            final int colorIndex = i;
            this.addDrawableChild(new ColorButton(x, y, colorSize, colorSize, colorPalette[i], 
                button -> {
                    callback.onColorSelected(colorPalette[colorIndex], colorNames[colorIndex]);
                    this.close();
                }));
        }
        
        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("取消"),
            button -> this.close())
            .dimensions(this.width / 2 - 50, startY + 4 * (colorSize + spacing) + 20, 100, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 30, Colors.WHITE);
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
            int borderColor = this.isHovered() ? 0xFFFFFFFF : 0xFF000000;
            context.drawBorder(this.getX(), this.getY(), this.width, this.height, borderColor);
            
            // 如果悬停，绘制高亮边框
            if (this.isHovered()) {
                context.drawBorder(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, 0xFFFFFFFF);
            }
        }
    }
} 