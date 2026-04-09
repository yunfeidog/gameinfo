package com.cxk.gameinfo.hud;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.SharedConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class HudOverlay implements HudElement {
    static Minecraft client;
    static Font textRenderer;
    static int DEFAULT_COLOR = CommonColors.WHITE; // 默认颜色为白色
    static int DEFAULT_HEIGHT = 10;
    static int color = DEFAULT_COLOR;
    static GameInfoConfig config;

    public HudOverlay() {
        client = Minecraft.getInstance();
        textRenderer = client.font;
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
    public void extractRenderState(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        if (textRenderer == null) {
            textRenderer = client.font; // 确保文本渲染器已初始化
            if (textRenderer == null) {
                logger("文本渲染器仍然为null，无法渲染HUD信息");
            }
        }

        Player player = client.player;
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


    private void renderPlayerEquipment(GuiGraphicsExtractor drawContext) {
        if (!config.showEquipment) return;
        LocalPlayer player = client.player;
        if (player == null || client.options.hideGui) return;
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
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
            drawContext.item(stack, startX, currentY);
            // 计算并渲染耐久度文本
            int maxDamage = stack.getMaxDamage();
            int durability = stack.getMaxDamage() - stack.getDamageValue();
            String durabilityText = String.format("%d/%d", durability, maxDamage);
            // 耐久度文本显示在物品图标右侧
            int textX = startX + 20; // 物品图标宽度是16像素，加4像素间距
            int textY = currentY + 5; // 垂直居中对齐
            drawContext.text(textRenderer, durabilityText, textX, textY, color, true);
        }
    }

    private static @NotNull List<ItemStack> getShowItemStacks(LocalPlayer player) {
        List<ItemStack> durableItems = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        for (EquipmentSlot slot : armorSlots) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.isDamageableItem()) {
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


    private void renderRemark(GuiGraphicsExtractor drawContext) {
        if (!config.remark) return;
        String versionLabel = "版本：";
        String versionValue = SharedConstants.getCurrentVersion().name();
        int y = 2;
        int x = client.getWindow().getGuiScaledWidth() - textRenderer.width(versionLabel + versionValue) - 2;
        drawContext.text(textRenderer, versionLabel, x, y, color, true);
        x += textRenderer.width(versionLabel);
        drawContext.text(textRenderer, versionValue, x, y, DEFAULT_COLOR, true);
    }


    private int renderFPS(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showFPS) return 0;
        int fps = client.getFps();
        String fpsText = "FPS: ";
        drawContext.text(textRenderer, fpsText, x, y, color, true);
        int width = textRenderer.width(fpsText);
        drawContext.text(textRenderer, fps == -1 ? "未知" : String.valueOf(fps), x + width, y, DEFAULT_COLOR, true);
        return DEFAULT_HEIGHT;
    }

    private int renderTimeAndDays(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showTimeAndDays) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        Level world = player.level();

        long timeOfDay = world.getLevelData().getGameTime() % 24000;
        // 时间为0的时候对应的是6:00
        int hours = (int) ((6 + (timeOfDay / 1000)) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);
        int days = (int) (world.getLevelData().getGameTime() / 24000);
        String daysText = "天数: ";
        drawContext.text(textRenderer, daysText, x, y, color, true);
        int width = textRenderer.width(daysText);
        drawContext.text(textRenderer, String.valueOf(days), x + width, y, DEFAULT_COLOR, true);
        width += textRenderer.width(String.valueOf(days));

        String timeText = "  时间: ";
        drawContext.text(textRenderer, timeText, x + width, y, color, true);
        width += textRenderer.width(timeText);

        String formatDate = String.format("%02d:%02d", hours, minutes);
        drawContext.text(textRenderer, formatDate, x + width, y, DEFAULT_COLOR, true);
        return DEFAULT_HEIGHT;
    }

    private int renderCoordinates(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showCoordinates) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        BlockPos pos = player.blockPosition(); // 获取玩家的方块位置
        String directionString = getDirectionString();
        String xyzText = "XYZ: ";
        drawContext.text(textRenderer, xyzText, x, y, color, true);
        int width = textRenderer.width(xyzText);
        String xyz = String.format("%d %d %d - %s", pos.getX(), pos.getY(), pos.getZ(), directionString);
        drawContext.text(textRenderer, xyz, x + width, y, DEFAULT_COLOR, true);
        return DEFAULT_HEIGHT;
    }


    private int renderNetherCoordinates(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showNetherCoordinates) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        Level world = player.level();
        BlockPos pos = player.blockPosition();

        String coordinateText = "";
        if (world.dimension().identifier().equals(Level.OVERWORLD.identifier())) {
            coordinateText = String.format("%d %d", pos.getX() / 8, pos.getZ() / 8);
        } else if (world.dimension().identifier().equals(Level.NETHER.identifier())) {
            coordinateText = String.format("%d %d", pos.getX() * 8, pos.getZ() * 8);
        }

        String coordinateLabel = "N-XZ: ";
        drawContext.text(textRenderer, coordinateLabel, x, y, color, true);
        int width = textRenderer.width(coordinateLabel);
        drawContext.text(textRenderer, coordinateText, x + width, y, DEFAULT_COLOR, true);
        return DEFAULT_HEIGHT;
    }

    private int renderBiome(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showBiome) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        Level world = player.level();
        BlockPos pos = player.blockPosition();

        Holder<Biome> biomeEntry = world.getBiome(pos);
        Identifier biomeId = biomeEntry.unwrapKey().map(ResourceKey::identifier).orElse(null);
        String biomeName;
        if (biomeId == null) {
            biomeName = "未知生物群系";
        } else {
            // 获取本地化翻译文本，比如 "biome.minecraft.plains" -> 平原
            String translationKey = "biome." + biomeId.getNamespace() + "." + biomeId.getPath();
            biomeName = Component.translatable(translationKey).getString();
        }
        String biomeLabel = "生物群系: ";
        drawContext.text(textRenderer, biomeLabel, x, y, color, true);
        int width = textRenderer.width(biomeLabel);
        drawContext.text(textRenderer, biomeName, x + width, y, DEFAULT_COLOR, true);
        return DEFAULT_HEIGHT;
    }


    private String getDirectionString() {
        Direction direction = null;
        if (client.player != null) {
            direction = client.player.getDirection();
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
