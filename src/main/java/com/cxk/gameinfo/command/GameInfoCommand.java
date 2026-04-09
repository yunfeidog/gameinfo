package com.cxk.gameinfo.command;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.util.HexUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class GameInfoCommand implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandRegistryAccess) {
        // /gi 配置类命令
        LiteralArgumentBuilder<FabricClientCommandSource> gi = ClientCommands.literal("gi");

        gi.then(ClientCommands.literal("帧数").then(ClientCommands.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showFPS = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("时间").then(ClientCommands.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showTimeAndDays = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("坐标").then(ClientCommands.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showCoordinates = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("下届坐标").then(ClientCommands.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showNetherCoordinates = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("群系").then(ClientCommands.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.showBiome = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("x坐标").then(ClientCommands.argument("position", IntegerArgumentType.integer()).executes(ctx -> {
            GameinfoClient.config.xPos = IntegerArgumentType.getInteger(ctx, "position");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("y坐标").then(ClientCommands.argument("position", IntegerArgumentType.integer()).executes(ctx -> {
            GameinfoClient.config.yPos = IntegerArgumentType.getInteger(ctx, "position");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("颜色").then(ClientCommands.argument("color", StringArgumentType.string()).executes(ctx -> {
            String color = StringArgumentType.getString(ctx, "color");
            GameinfoClient.config.color = HexUtil.toDecimal("0xFF" + color);
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("标注").then(ClientCommands.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.remark = BoolArgumentType.getBool(ctx, "state");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("大小").then(ClientCommands.argument("scale", DoubleArgumentType.doubleArg()).executes(ctx -> {
            GameinfoClient.config.scale = DoubleArgumentType.getDouble(ctx, "scale");
            GameinfoClient.config.saveConfig();
            return 1;
        })));

        gi.then(ClientCommands.literal("启用").then(ClientCommands.argument("state", BoolArgumentType.bool()).executes(ctx -> {
            GameinfoClient.config.enabled = BoolArgumentType.getBool(ctx, "state");
            return 1;
        })));
        dispatcher.register(gi);
    }
}
