package com.cxk.gameinfo.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

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
    public static Button createToggleButton(String labelPrefix, Supplier<Boolean> getter, Consumer<Boolean> setter, int x, int y, int width, int height) {
        return Button.builder(
                Component.literal(labelPrefix + " " + (getter.get() ? "✓" : "✗")),
                button -> {
                    boolean newValue = !getter.get();
                    setter.accept(newValue);
                    button.setMessage(Component.literal(labelPrefix + " " + (newValue ? "✓" : "✗")));
                })
                .bounds(x, y, width, height)
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
    public static EditBox createNumberField(Font textRenderer, String placeholder, Supplier<Integer> getter, Consumer<Integer> setter, int x, int y, int width, int height) {
        EditBox field = new EditBox(textRenderer, x, y, width, height, Component.literal(placeholder));
        field.setValue(String.valueOf(getter.get()));
        field.setResponder(text -> {
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
    public static EditBox createTextField(Font textRenderer, String placeholder, Supplier<String> getter, Consumer<String> setter, int x, int y, int width, int height) {
        EditBox field = new EditBox(textRenderer, x, y, width, height, Component.literal(placeholder));
        field.setValue(getter.get());
        field.setResponder(setter);
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
    public static Button createButton(String text, Runnable action, int x, int y, int width, int height) {
        return Button.builder(Component.literal(text), button -> action.run())
                .bounds(x, y, width, height)
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
    public static Button createOptionButton(String labelPrefix, String[] options, Supplier<Integer> currentIndex, Consumer<Integer> setter, int x, int y, int width, int height) {
        return Button.builder(
                Component.literal(labelPrefix + ": " + options[currentIndex.get()]),
                button -> {
                    int newIndex = (currentIndex.get() + 1) % options.length;
                    setter.accept(newIndex);
                    button.setMessage(Component.literal(labelPrefix + ": " + options[newIndex]));
                })
                .bounds(x, y, width, height)
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
    public static EditBox createValidatedNumberField(Font textRenderer, String placeholder, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int x, int y, int width, int height) {
        EditBox field = new EditBox(textRenderer, x, y, width, height, Component.literal(placeholder));
        field.setValue(String.valueOf(getter.get()));
        field.setResponder(text -> {
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
    public static Button[] createValueAdjustButtons(String labelPrefix, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int step, int x, int y, int totalWidth, int height) {
        int buttonWidth = 30;
        int displayWidth = totalWidth - buttonWidth * 2;
        
        Button decreaseButton = Button.builder(
                Component.literal("-"),
                button -> {
                    int newValue = Math.max(min, getter.get() - step);
                    setter.accept(newValue);
                })
                .bounds(x, y, buttonWidth, height)
                .build();

        Button displayButton = Button.builder(
                Component.literal(labelPrefix + ": " + getter.get()),
                button -> {})
                .bounds(x + buttonWidth, y, displayWidth, height)
                .build();

        Button increaseButton = Button.builder(
                Component.literal("+"),
                button -> {
                    int newValue = Math.min(max, getter.get() + step);
                    setter.accept(newValue);
                })
                .bounds(x + buttonWidth + displayWidth, y, buttonWidth, height)
                .build();

        return new Button[]{decreaseButton, displayButton, increaseButton};
    }
} 