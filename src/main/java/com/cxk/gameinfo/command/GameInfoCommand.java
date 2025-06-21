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
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GameInfoCommand implements CommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {

        // /gi 配置类命令
        LiteralArgumentBuilder<ServerCommandSource> gi = CommandManager.literal("gi");

        dispatcher.register(CommandManager.literal("gi").executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            GameInfoConfigScreen screen = new GameInfoConfigScreen(client.currentScreen);
            client.execute(() -> client.setScreen(screen));
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }));


        gi.then(CommandManager.literal("帧数").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showFPS = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("时间").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showTimeAndDays = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("坐标").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showCoordinates = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("下届坐标").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showNetherCoordinates = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("群系").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showBiome = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("x坐标").then(CommandManager.argument("position", IntegerArgumentType.integer()).executes(ctx -> {
            GameinfoClient.config.xPos = IntegerArgumentType.getInteger(ctx, "position");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("y坐标").then(CommandManager.argument("position", IntegerArgumentType.integer()).executes(ctx -> {
            GameinfoClient.config.yPos = IntegerArgumentType.getInteger(ctx, "position");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("颜色").then(CommandManager.argument("color", StringArgumentType.string()).executes(ctx -> {
            String color = StringArgumentType.getString(ctx, "color");
            GameinfoClient.config.color = HexUtil.toDecimal("0xFF" + color);
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("标注").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.remark = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("大小").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(ctx -> {
            GameinfoClient.config.scale = DoubleArgumentType.getDouble(ctx, "scale");
            GameinfoClient.config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));

        gi.then(CommandManager.literal("启用").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.enabled = BoolArgumentType.getBool(ctx, "state");
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        })));
        dispatcher.register(gi);
    }
}
