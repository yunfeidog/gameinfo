package com.cxk.gameinfo.command;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.gui.GameInfoConfigScreen;
import com.cxk.gameinfo.util.HexUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.ControlFlowAware;

public class GameInfoCommand implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        // /gi 配置类命令
        LiteralArgumentBuilder<FabricClientCommandSource> gi = ClientCommandManager.literal("gi");

        dispatcher.register(ClientCommandManager.literal("gi").executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            GameInfoConfigScreen screen = new GameInfoConfigScreen(client.currentScreen);
            client.execute(() -> client.setScreen(screen));
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }));


        gi.then(ClientCommandManager.literal("帧数").then(ClientCommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showFPS = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("时间").then(ClientCommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showTimeAndDays = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("坐标").then(ClientCommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showCoordinates = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("下届坐标").then(ClientCommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showNetherCoordinates = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("群系").then(ClientCommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showBiome = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("x坐标").then(ClientCommandManager.argument("position", IntegerArgumentType.integer()).executes(ctx -> {
            GameinfoClient.config.xPos = IntegerArgumentType.getInteger(ctx, "position");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("y坐标").then(ClientCommandManager.argument("position", IntegerArgumentType.integer()).executes(ctx -> {
            GameinfoClient.config.yPos = IntegerArgumentType.getInteger(ctx, "position");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("颜色").then(ClientCommandManager.argument("color", StringArgumentType.string()).executes(ctx -> {
            String color = StringArgumentType.getString(ctx, "color");
            GameinfoClient.config.color = HexUtil.toDecimal("0xFF" + color);
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("标注").then(ClientCommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.remark = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("大小").then(ClientCommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(ctx -> {
            GameinfoClient.config.scale = DoubleArgumentType.getDouble(ctx, "scale");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(ClientCommandManager.literal("启用").then(ClientCommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.enabled = BoolArgumentType.getBool(ctx, "state");
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));
        dispatcher.register(gi);
    }
}
