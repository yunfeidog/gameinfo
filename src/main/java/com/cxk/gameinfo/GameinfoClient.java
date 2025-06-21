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
    public static String modName = "gameinfo";
    public static GameInfoConfig config = new GameInfoConfig(); // 初始化配置
    public static GameInfoCommand command = new GameInfoCommand(); // 注册指令
    public static HudOverlay hudOverlay = new HudOverlay(); // 创建HUD覆盖层

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register(command);
        KeybindHandler.register(); // 注册按键绑定
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(modName, "custom_text"), hudOverlay); // 注册HUD元素
    }
}
