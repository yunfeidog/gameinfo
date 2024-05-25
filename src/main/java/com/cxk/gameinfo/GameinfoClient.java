package com.cxk.gameinfo;

import com.cxk.gameinfo.client.HudCommand;
import com.cxk.gameinfo.client.HudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;

/**
 * @author houyunfei
 */
public class GameinfoClient implements ClientModInitializer {
    // public static LiteralArgumentBuilder<ServerCommandSource> commandLiteral;


    @Override
    public void onInitializeClient() {


        // 注册HUD渲染回调
        HudRenderCallback.EVENT.register(new HudOverlay());


        // commandLiteral = CommandManager.literal("gameinfo").requires((source) -> true);
        // CommandRegistry.INSTANCE.register(false, dispatcher -> HudCommand.register());
        CommandRegistrationCallback.EVENT.register(HudCommand::register);


    }
}
