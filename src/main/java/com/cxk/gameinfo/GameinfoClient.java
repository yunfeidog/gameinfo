package com.cxk.gameinfo;

import com.cxk.gameinfo.command.GameInfoCommand;
import com.cxk.gameinfo.config.GameInfoConfig;
import com.cxk.gameinfo.hud.HudOverlay;
import com.cxk.gameinfo.keybind.KeybindHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;


public class GameinfoClient implements ClientModInitializer {

    public static GameInfoConfig config = new GameInfoConfig();

    @Override
    public void onInitializeClient() {
        HudOverlay hudOverlay = new HudOverlay();
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("gameinfo", "custom_text"), hudOverlay); // 注册HUD元素
        CommandRegistrationCallback.EVENT.register(GameInfoCommand::register);
        KeybindHandler.register(); // 注册按键绑定
    }
}
