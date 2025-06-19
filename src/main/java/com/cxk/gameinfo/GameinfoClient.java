package com.cxk.gameinfo;

import com.cxk.gameinfo.command.GameInfoCommand;
import com.cxk.gameinfo.config.GameInfoConfig;
import com.cxk.gameinfo.hud.HudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * @author houyunfei
 */
public class GameinfoClient implements ClientModInitializer {

    public static GameInfoConfig config = new GameInfoConfig();
    MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        HudOverlay hudOverlay = new HudOverlay();
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,Identifier.of("gameinfo", "custom_text"), hudOverlay); // 注册HUD元素
//        registerHud();
        CommandRegistrationCallback.EVENT.register(GameInfoCommand::register);
    }

    public static void registerHud() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("gameinfo", "custom_text"), (context, tickCounter) -> {
            logger("Custom HUD element registered");
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    "GameInfo HUD",
                    10, 12,
                    0xFFFF0000,
                    false
            );

        });
    }


    public static void logger(String message) {
        System.out.println("[GameInfo] " + message);
    }
}
