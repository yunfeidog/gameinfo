package com.cxk.gameinfo.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiHelper {

    /**
     * 创建开关按钮
     * @param labelPrefix 按钮标签前缀
     * @param getter 获取当前值的函数
     * @param setter 设置新值的函数
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @return 创建的按钮组件
     */
    public static ButtonWidget createToggleButton(String labelPrefix, Supplier<Boolean> getter, Consumer<Boolean> setter, int x, int y, int width, int height) {
        return ButtonWidget.builder(
                Text.literal(labelPrefix + ": " + (getter.get() ? "开启" : "关闭")),
                button -> {
                    boolean newValue = !getter.get();
                    setter.accept(newValue);
                    button.setMessage(Text.literal(labelPrefix + ": " + (newValue ? "开启" : "关闭")));
                })
                .dimensions(x, y, width, height)
                .build();
    }

    /**
     * 创建数字输入框
     * @param textRenderer 文字渲染器
     * @param placeholder 占位符文本
     * @param getter 获取当前值的函数
     * @param setter 设置新值的函数
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @return 创建的输入框组件
     */
    public static TextFieldWidget createNumberField(TextRenderer textRenderer, String placeholder, Supplier<Integer> getter, Consumer<Integer> setter, int x, int y, int width, int height) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, width, height, Text.literal(placeholder));
        field.setText(String.valueOf(getter.get()));
        field.setChangedListener(text -> {
            try {
                setter.accept(Integer.parseInt(text));
            } catch (NumberFormatException ignored) {
            }
        });
        return field;
    }

    /**
     * 创建文本输入框
     * @param textRenderer 文字渲染器
     * @param placeholder 占位符文本
     * @param getter 获取当前值的函数
     * @param setter 设置新值的函数
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @return 创建的输入框组件
     */
    public static TextFieldWidget createTextField(TextRenderer textRenderer, String placeholder, Supplier<String> getter, Consumer<String> setter, int x, int y, int width, int height) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, width, height, Text.literal(placeholder));
        field.setText(getter.get());
        field.setChangedListener(setter);
        return field;
    }

    /**
     * 创建普通按钮
     * @param text 按钮文本
     * @param action 点击时执行的动作
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @return 创建的按钮组件
     */
    public static ButtonWidget createButton(String text, Runnable action, int x, int y, int width, int height) {
        return ButtonWidget.builder(Text.literal(text), button -> action.run())
                .dimensions(x, y, width, height)
                .build();
    }

    /**
     * 创建选项按钮（可循环选择的按钮）
     * @param labelPrefix 按钮标签前缀
     * @param options 选项数组
     * @param currentIndex 当前选中的索引
     * @param setter 设置新索引的函数
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @return 创建的按钮组件
     */
    public static ButtonWidget createOptionButton(String labelPrefix, String[] options, Supplier<Integer> currentIndex, Consumer<Integer> setter, int x, int y, int width, int height) {
        return ButtonWidget.builder(
                Text.literal(labelPrefix + ": " + options[currentIndex.get()]),
                button -> {
                    int newIndex = (currentIndex.get() + 1) % options.length;
                    setter.accept(newIndex);
                    button.setMessage(Text.literal(labelPrefix + ": " + options[newIndex]));
                })
                .dimensions(x, y, width, height)
                .build();
    }

    /**
     * 创建带验证的数字输入框
     * @param textRenderer 文字渲染器
     * @param placeholder 占位符文本
     * @param getter 获取当前值的函数
     * @param setter 设置新值的函数
     * @param min 最小值
     * @param max 最大值
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @return 创建的输入框组件
     */
    public static TextFieldWidget createValidatedNumberField(TextRenderer textRenderer, String placeholder, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int x, int y, int width, int height) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, width, height, Text.literal(placeholder));
        field.setText(String.valueOf(getter.get()));
        field.setChangedListener(text -> {
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    setter.accept(value);
                }
            } catch (NumberFormatException ignored) {
            }
        });
        return field;
    }

    /**
     * 创建滑块样式的数值调节按钮组
     * @param labelPrefix 标签前缀
     * @param getter 获取当前值的函数
     * @param setter 设置新值的函数
     * @param min 最小值
     * @param max 最大值
     * @param step 步长
     * @param x X坐标
     * @param y Y坐标
     * @param totalWidth 总宽度
     * @param height 高度
     * @return 包含减少、显示、增加三个按钮的数组
     */
    public static ButtonWidget[] createValueAdjustButtons(String labelPrefix, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int step, int x, int y, int totalWidth, int height) {
        int buttonWidth = 30;
        int displayWidth = totalWidth - buttonWidth * 2;
        
        ButtonWidget decreaseButton = ButtonWidget.builder(
                Text.literal("-"),
                button -> {
                    int newValue = Math.max(min, getter.get() - step);
                    setter.accept(newValue);
                })
                .dimensions(x, y, buttonWidth, height)
                .build();

        ButtonWidget displayButton = ButtonWidget.builder(
                Text.literal(labelPrefix + ": " + getter.get()),
                button -> {})
                .dimensions(x + buttonWidth, y, displayWidth, height)
                .build();

        ButtonWidget increaseButton = ButtonWidget.builder(
                Text.literal("+"),
                button -> {
                    int newValue = Math.min(max, getter.get() + step);
                    setter.accept(newValue);
                })
                .dimensions(x + buttonWidth + displayWidth, y, buttonWidth, height)
                .build();

        return new ButtonWidget[]{decreaseButton, displayButton, increaseButton};
    }
} 