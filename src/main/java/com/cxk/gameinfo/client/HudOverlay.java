package com.cxk.gameinfo.client;

import com.cxk.gameinfo.GameinfoClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class HudOverlay implements HudRenderCallback {
    MinecraftClient client = MinecraftClient.getInstance();

    private int color = 0;

    // 是否开启全部展示
    public boolean isShowAll = false;

    private static final KeyBinding toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.gameinfo.toggleHud", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_F1, // The keycode of the key
            "category.gameinfo" // The translation key of the keybinding's category.
    ));

    public HudOverlay() {
        // Register the key press event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                toggleHudVisibility();
            }
        });
    }

    private void toggleHudVisibility() {
        HudConfig hudConfig = GameinfoClient.hudConfig;
        hudConfig.remark = isShowAll;
        hudConfig.showFPS = isShowAll;
        hudConfig.showTimeAndDays = isShowAll;
        hudConfig.showCoordinates = isShowAll;
        hudConfig.showNetherCoordinates = isShowAll;
        hudConfig.showBiome = isShowAll;
        isShowAll = !isShowAll;
        //
        // boolean newState = !hudConfig.isShowFPS(); // Assuming all states are the same
        // hudConfig.setShowFPS(newState);
        // hudConfig.setShowTimeAndDays(newState);
        // hudConfig.setShowCoordinates(newState);
        // hudConfig.setShowNetherCoordinates(newState);
        // hudConfig.setShowBiome(newState);
        // hudConfig.updateConfig(hudConfig);
        //
    }

    public void onHudRender(DrawContext drawContext, float tickDelta) {
        PlayerEntity player = client.player;
        HudConfig hudConfig = GameinfoClient.hudConfig;

        if (player != null) {
            World world = player.getEntityWorld();
            BlockPos pos = player.getBlockPos();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            TextRenderer textRenderer = client.textRenderer;

            int xPos = hudConfig.getxPos();
            int yPos = hudConfig.getyPos();
            this.color = hudConfig.getColor();

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
                // String title1 = "作者:yunfei";
                // String title2 = "gameinfo偏牧定制版-禁止转载";

                String title1 = "版本：";// 蓝色
                String title2 = "1.21";// 白色
                int rightX = client.getWindow().getScaledWidth() - textRenderer.getWidth("版本：1.21") - 2;
                drawContext.drawTextWithShadow(textRenderer, title1, rightX, 2, color);
                rightX += textRenderer.getWidth(title1);
                drawContext.drawTextWithShadow(textRenderer, title2, rightX, 2, 0xFFFFFF);


                // float scale = (float) hudConfig.getScale();
                // drawContext.getMatrices().push();
                // drawContext.getMatrices().scale(scale, scale, scale);
                // int rightX = client.getWindow().getScaledWidth() - textRenderer.getWidth("版本：") - 2;
                // int rightX2 = client.getWindow().getScaledWidth() - textRenderer.getWidth(title2) - 2;
                // 靠右边
                // int rightX = (int) (client.getWindow().getScaledWidth() / scale - textRenderer.getWidth(title1));
                // int rightX2 = (int) (client.getWindow().getScaledWidth() / scale - textRenderer.getWidth(title2));
                // int rightY = 2;
                // drawContext.drawTextWithShadow(textRenderer, title1, rightX, rightY, color);
                // rightY += textRenderer.fontHeight;
                // drawContext.drawTextWithShadow(textRenderer, title2, rightX2, rightY, color);
                // drawContext.getMatrices().pop();
            }
        }
    }


    private void renderFPS(DrawContext drawContext, TextRenderer textRenderer, int xPos, int yPos, MinecraftClient client) {
        int fps = "unspecified".equals(client.fpsDebugString) ? -1 : Integer.parseInt(client.fpsDebugString.split(" ")[0]);
        drawContext.drawTextWithShadow(textRenderer, "FPS:  ", xPos, yPos, color);
        int width = textRenderer.getWidth("FPS: ");
        width += textRenderer.getWidth(" ");
        drawContext.drawTextWithShadow(textRenderer, fps == -1 ? "未知" : String.valueOf(fps), width, yPos, 0xFFFFFF);
        // // 渲染版本号 版本：1.21
        // width += textRenderer.getWidth(String.valueOf(fps));
        // width += textRenderer.getWidth(" ");
        // String version = "版本: ";
        // drawContext.drawTextWithShadow(textRenderer, version, xPos + width, yPos, color);
        // width += textRenderer.getWidth(version);
        // drawContext.drawTextWithShadow(textRenderer, "1.21", xPos + width, yPos, 0xFFFFFF);
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
