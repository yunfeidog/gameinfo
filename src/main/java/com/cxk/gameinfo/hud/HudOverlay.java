package com.cxk.gameinfo.hud;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class HudOverlay implements HudRenderCallback {
    MinecraftClient client = MinecraftClient.getInstance();

    private int color = 0;


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


    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        PlayerEntity player = client.player;
        GameInfoConfig config = GameinfoClient.config;
        if (player != null) {
            World world = player.getEntityWorld();
            BlockPos pos = player.getBlockPos();
            int x = pos.getX();
            int z = pos.getZ();
            TextRenderer textRenderer = client.textRenderer;
            int xPos = config.xPos;
            int yPos = config.yPos;
            this.color = config.color;

            if (config.showFPS) {
                int fps = "unspecified".equals(client.fpsDebugString) ? -1 : Integer.parseInt(client.fpsDebugString.split(" ")[0]);
                textRenderer.drawWithShadow(matrixStack, "FPS:  ", xPos, yPos, color);
                int width = textRenderer.getWidth("FPS: ") + textRenderer.getWidth(" ");
                textRenderer.drawWithShadow(matrixStack, fps == -1 ? "未知" : String.valueOf(fps), xPos + width, yPos, 0xFFFFFF);
                yPos += 10;
            }

            if (config.showTimeAndDays) {
                long timeOfDay = world.getTimeOfDay() % 24000;
                int hours = (int) ((6 + (timeOfDay / 1000)) % 24);
                int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);
                int days = (int) (world.getTimeOfDay() / 24000);

                textRenderer.drawWithShadow(matrixStack, "天数: ", xPos, yPos, color);
                int width = textRenderer.getWidth("天数: ");
                textRenderer.drawWithShadow(matrixStack, String.valueOf(days), xPos + width, yPos, 0xFFFFFF);

                width += textRenderer.getWidth(String.valueOf(days)) + textRenderer.getWidth("  时间: ");
                textRenderer.drawWithShadow(matrixStack, "  时间: ", xPos + textRenderer.getWidth("天数: ") + textRenderer.getWidth(String.valueOf(days)), yPos, color);
                textRenderer.drawWithShadow(matrixStack, String.format("%02d:%02d", hours, minutes), xPos + width, yPos, 0xFFFFFF);
                yPos += 10;
            }

            if (config.showCoordinates) {
                String directionString = getDirectionString();
                String xyz = String.format("%d %d %d - %s", pos.getX(), pos.getY(), pos.getZ(), directionString);
                textRenderer.drawWithShadow(matrixStack, "XYZ: ", xPos, yPos, color);
                int width = textRenderer.getWidth("XYZ: ");
                textRenderer.drawWithShadow(matrixStack, xyz, xPos + width, yPos, 0xFFFFFF);
                yPos += 10;
            }

            if (config.showNetherCoordinates) {
                String n_xz = "";
                if (world.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) {
                    n_xz = String.format("%d %d", x / 8, z / 8);
                } else if (world.getRegistryKey().getValue().equals(World.NETHER.getValue())) {
                    n_xz = String.format("%d %d", x * 8, z * 8);
                }
                textRenderer.drawWithShadow(matrixStack, "N-XZ: ", xPos, yPos, color);
                int width = textRenderer.getWidth("N-XZ: ");
                textRenderer.drawWithShadow(matrixStack, n_xz, xPos + width, yPos, 0xFFFFFF);
                yPos += 10;
            }

            if (config.showBiome) {
                Biome biome = world.getBiome(pos);
                String biomeKey;
                Identifier biomeId = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                if (biomeId != null) {
                    biomeKey = "biome." + biomeId.getNamespace() + "." + biomeId.getPath();
                } else {
                    System.out.println("未找到生物群系ID，使用默认值");
                    biomeKey = "biome.minecraft.plains"; // fallback
                }
                String prefix = "生物群系: ";

                textRenderer.drawWithShadow(matrixStack, prefix, xPos, yPos, color);
                int width = textRenderer.getWidth(prefix);

                String biomeName = I18n.translate(biomeKey); // 翻译 key
                textRenderer.drawWithShadow(matrixStack, biomeName, xPos + width, yPos, 0xFFFFFF);
                yPos += 10;
            }

            if (config.remark) {
                String version = "版本：";
                String versionValue = config.version;
                int rightX = client.getWindow().getScaledWidth() - textRenderer.getWidth(version + versionValue) - 2;
                textRenderer.drawWithShadow(matrixStack, version, rightX, 2, color);
                rightX += textRenderer.getWidth(version);
                textRenderer.drawWithShadow(matrixStack, versionValue, rightX, 2, 0xFFFFFF);
            }
        }
    }


}
