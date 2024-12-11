package com.cxk.gameinfo;

import com.cxk.gameinfo.client.HudCommand;
import com.cxk.gameinfo.client.HudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * @author houyunfei
 */
public class GameinfoClient implements ClientModInitializer {
    HudOverlay hudOverlay;


    @Override
    public void onInitializeClient() {
        // 注册HUD渲染回调
        hudOverlay = new HudOverlay();
        HudRenderCallback.EVENT.register(hudOverlay);
        CommandRegistrationCallback.EVENT.register(HudCommand::register);
    }
}
