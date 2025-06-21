package com.cxk.gameinfo;

import com.cxk.gameinfo.command.GameInfoCommand;
import com.cxk.gameinfo.config.GameInfoConfig;
import com.cxk.gameinfo.hud.HudOverlay;
import com.cxk.gameinfo.keybind.KeybindHandler;
import net.fabricmc.api.ClientModInitializer;


public class GameinfoClient implements ClientModInitializer {

    public static GameInfoConfig config = new GameInfoConfig(); // 初始化配置

    @Override
    public void onInitializeClient() {
        HudOverlay.register(); // 注册HUD元素
        GameInfoCommand.register(); // 注册指令
        KeybindHandler.register(); // 注册按键绑定
    }
}
