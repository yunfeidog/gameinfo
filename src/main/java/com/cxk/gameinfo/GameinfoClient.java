package com.cxk.gameinfo;

import com.cxk.gameinfo.command.GameInfoCommand;
import com.cxk.gameinfo.config.GameInfoConfig;
import com.cxk.gameinfo.hud.HudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

/**
 * @author houyunfei
 */
public class GameinfoClient implements ClientModInitializer {

    public static GameInfoConfig config = new GameInfoConfig();

    @Override
    public void onInitializeClient() {
        HudOverlay hudOverlay = new HudOverlay();
        HudRenderCallback.EVENT.register(hudOverlay); // 注册HUD渲染回调
        CommandRegistrationCallback.EVENT.register(GameInfoCommand::register);
    }
}
