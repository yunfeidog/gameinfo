package com.cxk.gameinfo.hud;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HudOverlay implements HudElement {
    static MinecraftClient client;
    static TextRenderer textRenderer;
    static int DEFAULT_COLOR = Colors.WHITE; // 默认颜色为白色
    static int DEFAULT_HEIGHT = 10;
    static int color = DEFAULT_COLOR;
    static GameInfoConfig config;

    public HudOverlay() {
        client = MinecraftClient.getInstance();
        textRenderer = client.textRenderer;
        config = GameinfoClient.config; // 获取配置
    }

    // 获取所有装备槽位
    static EquipmentSlot[] armorSlots = {EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD,};


    /**
     * 控制Hud 开启/关闭
     */
    public void toggleHudVisibility() {
        GameInfoConfig config = GameinfoClient.config;
        config.enabled = !config.enabled;
        config.saveConfig();
    }

    public static void logger(String message) {
        System.out.println("[DEBUG]： " + message);
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (textRenderer == null) {
            textRenderer = client.textRenderer; // 确保文本渲染器已初始化
            if (textRenderer == null) {
                logger("文本渲染器仍然为null，无法渲染HUD信息");
            }
        }

        PlayerEntity player = client.player;
        if (player == null) return;
        if (!config.enabled) return;

        int xPos = config.xPos;
        int yPos = config.yPos;
        color = config.color;

        // 左上角屏幕渲染
        yPos += renderFPS(drawContext, xPos, yPos);
        yPos += renderTimeAndDays(drawContext, xPos, yPos);
        yPos += renderCoordinates(drawContext, xPos, yPos);
        yPos += renderNetherCoordinates(drawContext, xPos, yPos);
        yPos += renderBiome(drawContext, xPos, yPos);

        // 右上角渲染版本信息
        renderRemark(drawContext);

        // 在玩家的装备栏附近渲染装备信息
        renderPlayerEquipment(drawContext);
    }


    private void renderPlayerEquipment(DrawContext drawContext) {
        if (!config.showEquipment) return;
        ClientPlayerEntity player = client.player;
        if (player == null || client.options.hudHidden) return;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        // 装备显示在物品栏右侧 物品栏宽度是182像素（9个槽位×20像素+2像素边距），所以91是物品栏右边缘，再加10像素间距
        int startX = screenWidth / 2 + 91 + 10;
        int startY = screenHeight - 22;

        // 先收集所有有耐久度的装备
        List<ItemStack> durableItems = getShowItemStacks(player);
        // 从下往上渲染（倒序）
        for (int i = 0; i < durableItems.size(); i++) {
            ItemStack stack = durableItems.get(i);
            // 计算当前行的Y位置（从baseY开始往上）
            int currentY = startY - (i * 20); // 每行间距20像素，向上排列
            // 渲染物品图标
            drawContext.drawItem(stack, startX, currentY);
            // 计算并渲染耐久度文本
            int maxDamage = stack.getMaxDamage();
            int durability = stack.getMaxDamage() - stack.getDamage();
            String durabilityText = String.format("%d/%d", durability, maxDamage);
            // 耐久度文本显示在物品图标右侧
            int textX = startX + 20; // 物品图标宽度是16像素，加4像素间距
            int textY = currentY + 5; // 垂直居中对齐
            drawContext.drawTextWithShadow(textRenderer, durabilityText, textX, textY, color);
        }
    }

    private static @NotNull List<ItemStack> getShowItemStacks(ClientPlayerEntity player) {
        List<ItemStack> durableItems = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        for (EquipmentSlot slot : armorSlots) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty() || !stack.isDamageable()) {
                DurabilityRecord.durabilityTracker.remove(slot);
                continue;
            }
            // 检查是否有经验修补附魔
            if (DurabilityRecord.hasExperienceMending(stack)) {
                // 清理该槽位的记录
                DurabilityRecord.durabilityTracker.remove(slot);
                continue;
            }
            // 检查耐久度是否在30秒内有变化
            if (DurabilityRecord.hasDurabilityChangedRecently(slot, stack, currentTime)) {
                durableItems.add(stack);
            }
        }
        return durableItems;
    }


    private void renderRemark(DrawContext drawContext) {
        if (!config.remark) return;
        String versionLabel = "版本：";
        String versionValue = SharedConstants.getGameVersion().name();
        int y = 2;
        int x = client.getWindow().getScaledWidth() - textRenderer.getWidth(versionLabel + versionValue) - 2;
        drawContext.drawTextWithShadow(textRenderer, versionLabel, x, y, color);
        x += textRenderer.getWidth(versionLabel);
        drawContext.drawTextWithShadow(textRenderer, versionValue, x, y, DEFAULT_COLOR);
    }


    private int renderFPS(DrawContext drawContext, int x, int y) {
        if (!config.showFPS) return 0;
        int fps = client.getCurrentFps();
        String fpsText = "FPS: ";
        drawContext.drawTextWithShadow(textRenderer, fpsText, x, y, color);
        int width = textRenderer.getWidth(fpsText);
        drawContext.drawTextWithShadow(textRenderer, fps == -1 ? "未知" : String.valueOf(fps), x + width, y, DEFAULT_COLOR);
        return DEFAULT_HEIGHT;
    }

    private int renderTimeAndDays(DrawContext drawContext, int x, int y) {
        if (!config.showTimeAndDays) return 0;
        ClientPlayerEntity player = client.player;
        if (player == null) return 0;
        World world = player.getEntityWorld();

        long timeOfDay = world.getTimeOfDay() % 24000;
        // 时间为0的时候对应的是6:00
        int hours = (int) ((6 + (timeOfDay / 1000)) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);
        int days = (int) (world.getTimeOfDay() / 24000);
        String daysText = "天数: ";
        drawContext.drawTextWithShadow(textRenderer, daysText, x, y, color);
        int width = textRenderer.getWidth(daysText);
        drawContext.drawTextWithShadow(textRenderer, String.valueOf(days), x + width, y, DEFAULT_COLOR);
        width += textRenderer.getWidth(String.valueOf(days));

        String timeText = "  时间: ";
        drawContext.drawTextWithShadow(textRenderer, timeText, x + width, y, color);
        width += textRenderer.getWidth(timeText);

        String formatDate = String.format("%02d:%02d", hours, minutes);
        drawContext.drawTextWithShadow(textRenderer, formatDate, x + width, y, DEFAULT_COLOR);
        return DEFAULT_HEIGHT;
    }

    private int renderCoordinates(DrawContext drawContext, int x, int y) {
        if (!config.showCoordinates) return 0;
        ClientPlayerEntity player = client.player;
        if (player == null) return 0;
        BlockPos pos = player.getBlockPos(); // 获取玩家的方块位置
        String directionString = getDirectionString();
        String xyzText = "XYZ: ";
        drawContext.drawTextWithShadow(textRenderer, xyzText, x, y, color);
        int width = textRenderer.getWidth(xyzText);
        String xyz = String.format("%d %d %d - %s", pos.getX(), pos.getY(), pos.getZ(), directionString);
        drawContext.drawTextWithShadow(textRenderer, xyz, x + width, y, DEFAULT_COLOR);
        return DEFAULT_HEIGHT;
    }


    private int renderNetherCoordinates(DrawContext drawContext, int x, int y) {
        if (!config.showNetherCoordinates) return 0;
        ClientPlayerEntity player = client.player;
        if (player == null) return 0;
        World world = player.getEntityWorld();
        BlockPos pos = player.getBlockPos();

        String coordinateText = "";
        if (world.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) {
            coordinateText = String.format("%d %d", pos.getX() / 8, pos.getZ() / 8);
        } else if (world.getRegistryKey().getValue().equals(World.NETHER.getValue())) {
            coordinateText = String.format("%d %d", pos.getX() * 8, pos.getZ() * 8);
        }

        String coordinateLabel = "N-XZ: ";
        drawContext.drawTextWithShadow(textRenderer, coordinateLabel, x, y, color);
        int width = textRenderer.getWidth(coordinateLabel);
        drawContext.drawTextWithShadow(textRenderer, coordinateText, x + width, y, DEFAULT_COLOR);
        return DEFAULT_HEIGHT;
    }

    private int renderBiome(DrawContext drawContext, int x, int y) {
        if (!config.showBiome) return 0;
        ClientPlayerEntity player = client.player;
        if (player == null) return 0;
        World world = player.getEntityWorld();
        BlockPos pos = player.getBlockPos();

        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        Identifier biomeId = biomeEntry.getKey().map(RegistryKey::getValue).orElse(null);
        String biomeName;
        if (biomeId == null) {
            biomeName = "未知生物群系";
        } else {
            // 获取本地化翻译文本，比如 "biome.minecraft.plains" -> 平原
            String translationKey = "biome." + biomeId.getNamespace() + "." + biomeId.getPath();
            biomeName = Text.translatable(translationKey).getString();
        }
        String biomeLabel = "生物群系: ";
        drawContext.drawTextWithShadow(textRenderer, biomeLabel, x, y, color);
        int width = textRenderer.getWidth(biomeLabel);
        drawContext.drawTextWithShadow(textRenderer, biomeName, x + width, y, DEFAULT_COLOR);
        return DEFAULT_HEIGHT;
    }


    private String getDirectionString() {
        Direction direction = null;
        if (client.player != null) {
            direction = client.player.getHorizontalFacing();
        }
        String directionString = null;
        if (direction != null) {
            directionString = switch (direction) {
                case NORTH -> "北";
                case SOUTH -> "南";
                case EAST -> "东";
                case WEST -> "西";
                default -> "未知";
            };
        }
        return directionString;
    }

}
