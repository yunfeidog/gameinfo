package com.cxk.gameinfo.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;


public class HudOverlay implements HudRenderCallback {
    MinecraftClient client = MinecraftClient.getInstance();

    private int color = 0;


    public void onHudRender(DrawContext drawContext, float tickDelta) {
        PlayerEntity player = client.player;
        HudConfig hudConfig = HudConfig.getInstance();

        if (player != null) {
            World world = player.getEntityWorld();
            BlockPos pos = player.getBlockPos();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            TextRenderer textRenderer = client.textRenderer;

            int xPos = hudConfig.getxPos();
            int yPos = hudConfig.getyPos();
            int color1 = hudConfig.getColor();
            this.color = color1;

            if (hudConfig.isShowFPS()) {
                renderFPS(drawContext, textRenderer, xPos, yPos, client);
                yPos += 10;
            }

            if (hudConfig.isShowTimeAndDays()) {
                renderTimeAndDays(drawContext, textRenderer, xPos, yPos, world);
                yPos += 10;
            }

            if (hudConfig.isShowCoordinates()) {
                renderCoordinates(drawContext, textRenderer, xPos, yPos, pos, world);
                yPos += 10;
            }

            if (hudConfig.isShowNetherCoordinates()) {
                renderNetherCoordinates(drawContext, textRenderer, xPos, yPos, x, z, world);
                yPos += 10;
            }

            if (hudConfig.isShowBiome()) {
                renderBiome(drawContext, textRenderer, xPos, yPos, pos, world);
                yPos += 10;
            }

            if (hudConfig.isRemark()) {
                // 这里放到右上角，而不是左上角，不要用原来的xPos，yPos
                String title1 = "作者:yunfei";
                String title2 = "gameinfo偏牧定制版-禁止转载";
                float scale = (float) hudConfig.getScale();
                drawContext.getMatrices().push();
                drawContext.getMatrices().scale(scale, scale, scale);
                // 靠右边
                int rightX = (int) (client.getWindow().getScaledWidth() / scale - textRenderer.getWidth(title1));
                int rightX2 = (int) (client.getWindow().getScaledWidth() / scale - textRenderer.getWidth(title2));
                int rightY = 2;
                drawContext.drawTextWithShadow(textRenderer, title1, rightX, rightY, color);
                rightY += textRenderer.fontHeight;
                drawContext.drawTextWithShadow(textRenderer, title2, rightX2, rightY, color);
                drawContext.getMatrices().pop();
            }
        }
    }


    private void renderFPS(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, MinecraftClient client) {
        int fps = client.fpsDebugString.equals("unspecified") ? -1 : Integer.parseInt(client.fpsDebugString.split(" ")[0]);
        drawContext.drawTextWithShadow(textRenderer, "FPS:  ", xPos, yPos, color);
        int width = textRenderer.getWidth("FPS: ");
        drawContext.drawTextWithShadow(textRenderer, fps == -1 ? "未知" : String.valueOf(fps), xPos + width, yPos, 0xFFFFFF);
    }

    private void renderTimeAndDays(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, World world) {
        long timeOfDay = world.getTimeOfDay() % 24000;
        // 时间为0的时候对应的是6:00
        int hours = (int) ((6 + (timeOfDay / 1000)) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);
        int days = (int) (world.getTimeOfDay() / 24000);

        drawContext.drawTextWithShadow(textRenderer, "天数: ", xPos, yPos, color);
        int width = textRenderer.getWidth("天数: ");
        drawContext.drawTextWithShadow(textRenderer, String.valueOf(days), xPos + width, yPos, 0xFFFFFF);

        width += textRenderer.getWidth(String.valueOf(days)) + textRenderer.getWidth("  时间: ");
        drawContext.drawTextWithShadow(textRenderer, "  时间: ", xPos + textRenderer.getWidth("天数: ") + textRenderer.getWidth(String.valueOf(days)), yPos, color);
        drawContext.drawTextWithShadow(textRenderer, String.format("%02d:%02d", hours, minutes), xPos + width, yPos, 0xFFFFFF);
    }

    private void renderCoordinates(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, BlockPos pos, World world) {
        String directionString = getDirectionString();
        String xyz = String.format("%d %d %d - %s", pos.getX(), pos.getY(), pos.getZ(), directionString);
        drawContext.drawTextWithShadow(textRenderer, "XYZ: ", xPos, yPos, color);
        int width = textRenderer.getWidth("XYZ: ");
        drawContext.drawTextWithShadow(textRenderer, xyz, xPos + width, yPos, 0xFFFFFF);
    }

    @Nullable
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
        drawContext.drawTextWithShadow(textRenderer, "N-XZ: ", xPos, yPos, color);
        int width = textRenderer.getWidth("N-XZ: ");
        drawContext.drawTextWithShadow(textRenderer, n_xz, xPos + width, yPos, 0xFFFFFF);
    }

    private void renderBiome(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, BlockPos pos, World world) {
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        Identifier biomeId = biomeEntry.getKey().map(RegistryKey::getValue).orElse(null);
        String biomeName = biomeId == null ? "未知生物群系" : biomeId.getPath();
        drawContext.drawTextWithShadow(textRenderer, String.format("生物群系: %s", biomeName), xPos, yPos, 0xFFFFFF);
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        onHudRender(drawContext, 0);
    }
}
