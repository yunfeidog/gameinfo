package com.cxk.gameinfo.hud;

import com.cxk.gameinfo.GameinfoClient;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class BlockInfoHudRenderer implements HudElement {

    private static Field currentBreakingProgressField;

    static {
        try {
            // 尝试获取currentBreakingProgress字段
            currentBreakingProgressField = net.minecraft.client.network.ClientPlayerInteractionManager.class.getDeclaredField("currentBreakingProgress");
            currentBreakingProgressField.setAccessible(true);
        } catch (Exception e) {
            // 如果反射失败，字段保持null
            currentBreakingProgressField = null;
        }
    }

    private float getPreciseBreakingProgress(MinecraftClient client) {
        if (currentBreakingProgressField != null && client.interactionManager != null) {
            try {
                return (Float) currentBreakingProgressField.get(client.interactionManager);
            } catch (Exception e) {
                // 反射失败，回退到原方法
            }
        }
        // 回退到原来的方法
        int intProgress = client.interactionManager.getBlockBreakingProgress();
        return intProgress >= 0 ? intProgress / 10.0f : -1.0f;
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        // 检查配置是否启用方块信息显示
        if (!GameinfoClient.config.showBlockInfo) return;

        MinecraftClient client = MinecraftClient.getInstance();
        // 确保客户端和世界存在
        if (client == null || client.world == null || client.player == null) return;
        // 获取玩家正在看的目标
        HitResult hitResult = client.crosshairTarget;

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos pos = blockHitResult.getBlockPos();
            World world = client.world;
            BlockState blockState = world.getBlockState(pos);

            // 获取方块对应的物品堆栈
            ItemStack itemStack = new ItemStack(blockState.getBlock());

            // 获取方块名称
            String blockName = blockState.getBlock().getName().getString();

            // 获取坐标信息
            String positionInfo = String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ());

            // 获取玩家手持物品
            ItemStack heldItem = client.player.getMainHandStack();

            // 检查工具有效性
            String progressText = "";
            String toolIcon = "";
            if (!heldItem.isEmpty()) {
                // 检查工具是否正确类型
                boolean isCorrectTool = heldItem.isSuitableFor(blockState);
                // 检查是否需要工具
                boolean requiresTool = blockState.isToolRequired();
                Block block = blockState.getBlock();
                boolean isUnbreakableBlock = block == Blocks.BEDROCK || block == Blocks.BARRIER;
                boolean canHarvest;
                if (isUnbreakableBlock) {
                    canHarvest = false;
                } else if (requiresTool) {
                    canHarvest = isCorrectTool;
                } else {
                    canHarvest = true;
                }
                toolIcon = canHarvest ? " ✓" : " ✗";
            }

            // 检查挖掘进度
            float preciseProgress = getPreciseBreakingProgress(client);
            boolean isBreaking = preciseProgress >= 0;

            // 如果正在挖掘，添加进度百分比
            if (isBreaking) {
                progressText = String.format(" %.0f%%", preciseProgress * 100);
            }

            // 获取屏幕尺寸
            int screenWidth = client.getWindow().getScaledWidth();

            // 创建完整的物品名称（包含工具图标和进度）
            String fullBlockName = blockName + toolIcon + progressText;
            Text nameText = Text.literal(fullBlockName);
            Text posText = Text.literal(positionInfo);

            // 计算文本宽度
            int nameWidth = client.textRenderer.getWidth(nameText);
            int posWidth = client.textRenderer.getWidth(posText);
            int maxTextWidth = Math.max(nameWidth, posWidth);

            // 定义布局参数
            int itemSize = 16; // 物品图标大小
            int padding = 8; // 内边距
            int textPadding = 6; // 图标和文字之间的间距
            int lineHeight = 11; // 行高
            int borderThickness = 1; // 边框厚度

            // 计算总宽度和高度
            int contentWidth = itemSize + textPadding + maxTextWidth;
            int contentHeight = itemSize; // 使用物品图标高度
            int totalWidth = contentWidth + padding * 2 + borderThickness * 2;
            int totalHeight = contentHeight + padding * 2 + borderThickness * 2;

            int startX = (screenWidth - totalWidth) / 2;
            int startY = 10; // 距离屏幕顶部的距离

            // 绘制边框 (淡灰色)
            int borderColor = 0xFFAAAAAA; // 淡灰色边框
            drawContext.fill(startX, startY, startX + totalWidth, startY + borderThickness, borderColor); // 上边框
            drawContext.fill(startX, startY + totalHeight - borderThickness, startX + totalWidth, startY + totalHeight, borderColor); // 下边框
            drawContext.fill(startX, startY, startX + borderThickness, startY + totalHeight, borderColor); // 左边框
            drawContext.fill(startX + totalWidth - borderThickness, startY, startX + totalWidth, startY + totalHeight, borderColor); // 右边框

            // 绘制进度条作为下边框
            if (isBreaking) {
                int progressWidth = (int) ((totalWidth - borderThickness * 2) * preciseProgress);
                drawContext.fill(startX + borderThickness, startY + totalHeight - borderThickness,
                        startX + borderThickness + progressWidth, startY + totalHeight, 0xFFCC8800); // 更淡的橙色
            }

            // 绘制背景（更淡的黑色半透明）
            int bgX = startX + borderThickness;
            int bgY = startY + borderThickness;
            int bgWidth = totalWidth - borderThickness * 2;
            int bgHeight = totalHeight - borderThickness * 2;
            drawContext.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0xB0000000); // 更淡的背景

            // 计算物品图标位置（垂直居中）
            int itemX = bgX + padding;
            int itemY = bgY + padding;

            // 计算文字位置
            int textX = itemX + itemSize + textPadding;
            int nameY = bgY + padding + 1; // 物品名称位置
            int posY = nameY + lineHeight; // 坐标位置

            // 绘制物品图标
            drawContext.drawItem(itemStack, itemX, itemY);

            // 绘制文本
            // 物品名称（淡白色），工具图标根据状态着色，进度百分比用橙色
            if (toolIcon.isEmpty() && progressText.isEmpty()) {
                drawContext.drawText(client.textRenderer, nameText, textX, nameY, 0xFFE0E0E0, true); // 淡白色
            } else {
                // 分别绘制物品名称、工具图标和进度百分比
                Text blockNameOnly = Text.literal(blockName);
                drawContext.drawText(client.textRenderer, blockNameOnly, textX, nameY, 0xFFE0E0E0, true); // 淡白色

                int currentX = textX + client.textRenderer.getWidth(blockNameOnly);

                // 绘制工具图标
                if (!toolIcon.isEmpty()) {
                    Text iconText = Text.literal(toolIcon);
                    int iconColor = toolIcon.contains("✓") ? 0xFF70C070 : 0xFFC07070; // 更淡的绿色和红色
                    drawContext.drawText(client.textRenderer, iconText, currentX, nameY, iconColor, true);
                    currentX += client.textRenderer.getWidth(iconText);
                }

                // 绘制进度百分比 并且不是0
                if (!progressText.isEmpty() && preciseProgress > 0) {
                    Text progressTextObj = Text.literal(progressText);
                    drawContext.drawText(client.textRenderer, progressTextObj, currentX, nameY, 0xFFCC8800, true); // 淡橙色
                }
            }

            // 坐标信息（淡青色）
            drawContext.drawText(client.textRenderer, posText, textX, posY, 0xFF70C0C0, true); // 更淡的青色
        }
    }
}