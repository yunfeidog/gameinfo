package com.cxk.gameinfo.hud;

import com.cxk.gameinfo.GameinfoClient;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockInfoHudRenderer implements HudElement {

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

            // 获取屏幕尺寸
            int screenWidth = client.getWindow().getScaledWidth();

            // 创建文本对象
            Text nameText = Text.literal(blockName);
            Text posText = Text.literal(positionInfo);

            // 计算文本宽度
            int nameWidth = client.textRenderer.getWidth(nameText);
            int posWidth = client.textRenderer.getWidth(posText);
            int maxTextWidth = Math.max(nameWidth, posWidth);

            // 定义布局参数
            int itemSize = 16; // 物品图标大小
            int padding = 8; // 内边距
            int textPadding = 4; // 图标和文字之间的间距
            int lineHeight = 10; // 行高

            // 计算总宽度和位置
            int totalWidth = itemSize + textPadding + maxTextWidth + padding * 2;
            int totalHeight = Math.max(itemSize, lineHeight * 2) + padding * 2;

            int startX = (screenWidth - totalWidth) / 2;
            int startY = 10; // 距离屏幕顶部的距离

            // 绘制半透明背景
            drawContext.fill(startX, startY, startX + totalWidth, startY + totalHeight, 0x80000000);

            // 计算物品图标位置（垂直居中）
            int itemX = startX + padding;
            int itemY = startY + padding + (totalHeight - padding * 2 - itemSize) / 2;

            // 计算文字位置
            int textAreaX = itemX + itemSize + textPadding;
            int textAreaWidth = maxTextWidth;
            int nameY = startY + padding + 2; // 名称位置（稍微向上）
            int posY = nameY + lineHeight + 2; // 坐标位置

            // 计算名称居中位置
            int nameTextX = textAreaX + (textAreaWidth - nameWidth) / 2;
            int posTextX = textAreaX; // 坐标保持左对齐

            // 绘制物品图标
            drawContext.drawItem(itemStack, itemX, itemY);

            // 绘制文本
            drawContext.drawText(client.textRenderer, nameText, nameTextX, nameY, 0xFFFFFFFF, true);
            drawContext.drawText(client.textRenderer, posText, posTextX, posY, 0xFF88FF88, true);
        }
    }
}