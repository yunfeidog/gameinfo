package com.cxk.gameinfo.hud;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.lwjgl.glfw.GLFW;

public class HudOverlay implements HudElement {
    MinecraftClient client = MinecraftClient.getInstance();
    static int DEFAULT_COLOR = Colors.WHITE; // 默认颜色为白色
    private int color = DEFAULT_COLOR;

    // 是否开启全部展示
    public boolean isShowAll = true;

    private static final KeyBinding toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.gameinfo.toggleHud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F1, "category.gameinfo"));


    public HudOverlay() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                toggleHudVisibility();
            }
        });
    }

    private void toggleHudVisibility() {
        GameInfoConfig config = GameinfoClient.config;
        config.closeGameInfo(isShowAll);
        isShowAll = !isShowAll;
    }

    public void onHudRender(DrawContext drawContext, float tickDelta) {
        PlayerEntity player = client.player;
        GameInfoConfig config = GameinfoClient.config;
        if (player != null) {
            World world = player.getWorld();
            BlockPos pos = player.getBlockPos();
            int x = pos.getX();
            int z = pos.getZ();
            TextRenderer textRenderer = client.textRenderer;
            int xPos = config.xPos;
            int yPos = config.yPos;
            this.color = config.color;
            if (config.showFPS) {
                renderFPS(drawContext, textRenderer, xPos, yPos, client);
                yPos += 10;
            }

            if (config.showTimeAndDays) {
                renderTimeAndDays(drawContext, textRenderer, xPos, yPos, world);
                yPos += 10;
            }

            if (config.showCoordinates) {
                renderCoordinates(drawContext, textRenderer, xPos, yPos, pos, world);
                yPos += 10;
            }

            if (config.showNetherCoordinates) {
                renderNetherCoordinates(drawContext, textRenderer, xPos, yPos, x, z, world);
                yPos += 10;
            }

            if (config.showBiome) {
                renderBiome(drawContext, textRenderer, xPos, yPos, pos, world);
                yPos += 10;
            }

            if (config.remark) {
                String version = "版本：";// 蓝色
                String versionValue = config.version;
                int rightX = client.getWindow().getScaledWidth() - textRenderer.getWidth(version + versionValue) - 2;
                drawContext.drawText(textRenderer, version, rightX, 2, color, false);
                rightX += textRenderer.getWidth(version);
                drawContext.drawText(textRenderer, versionValue, rightX, 2, DEFAULT_COLOR, false);
            }
        }
    }


    private void renderFPS(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, MinecraftClient client) {
        int fps = "unspecified".equals(client.fpsDebugString) ? -1 : Integer.parseInt(client.fpsDebugString.split(" ")[0]);
        drawContext.drawText(textRenderer, "FPS:  ", xPos, yPos, color, false);
        int width = textRenderer.getWidth("FPS: ");
        width += textRenderer.getWidth(" ");
        drawContext.drawText(textRenderer, fps == -1 ? "未知" : String.valueOf(fps), width, yPos, DEFAULT_COLOR, false);
    }

    private void renderTimeAndDays(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, World world) {
        long timeOfDay = world.getTimeOfDay() % 24000;
        // 时间为0的时候对应的是6:00
        int hours = (int) ((6 + (timeOfDay / 1000)) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);
        int days = (int) (world.getTimeOfDay() / 24000);

        drawContext.drawText(textRenderer, "天数: ", xPos, yPos, color, false);
        int width = textRenderer.getWidth("天数: ");
        drawContext.drawText(textRenderer, String.valueOf(days), xPos + width, yPos, DEFAULT_COLOR, false);

        width += textRenderer.getWidth(String.valueOf(days)) + textRenderer.getWidth("  时间: ");
        drawContext.drawText(textRenderer, "  时间: ", xPos + textRenderer.getWidth("天数: ") + textRenderer.getWidth(String.valueOf(days)), yPos, color, false);
        drawContext.drawText(textRenderer, String.format("%02d:%02d", hours, minutes), xPos + width, yPos, DEFAULT_COLOR, false);
    }

    private void renderCoordinates(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, BlockPos pos, World world) {
        String directionString = getDirectionString();
        String xyz = String.format("%d %d %d - %s", pos.getX(), pos.getY(), pos.getZ(), directionString);
        drawContext.drawText(textRenderer, "XYZ: ", xPos, yPos, color, false);
        int width = textRenderer.getWidth("XYZ: ");
        drawContext.drawText(textRenderer, xyz, xPos + width, yPos, DEFAULT_COLOR, false);
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

    private void renderNetherCoordinates(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, int x, int z, World world) {
        String n_xz = "";
        if (world.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) {
            n_xz = String.format("%d %d", x / 8, z / 8);
        } else if (world.getRegistryKey().getValue().equals(World.NETHER.getValue())) {
            n_xz = String.format("%d %d", x * 8, z * 8);
        }
        drawContext.drawText(textRenderer, "N-XZ: ", xPos, yPos, color, false);
        int width = textRenderer.getWidth("N-XZ: ");
        drawContext.drawText(textRenderer, n_xz, xPos + width, yPos, DEFAULT_COLOR, false);
    }

    private void renderBiome(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, BlockPos pos, World world) {
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        Identifier biomeId = biomeEntry.getKey().map(RegistryKey::getValue).orElse(null);
        String biomeName = biomeId == null ? "未知生物群系" : biomeId.getPath();
        drawContext.drawText(textRenderer, String.format("生物群系: %s", biomeName), xPos, yPos, DEFAULT_COLOR, false);
    }

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        onHudRender(context, 0);
    }

    public static void logger(String message) {
        System.out.println("[调试]: " + message);
    }
}
