package com.cxk.gameinfo.command;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GameInfoCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        // hud fps [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("帧数").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            GameInfoConfig config = GameinfoClient.config;
            config.showFPS = state;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud time [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("时间").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            GameInfoConfig config = GameinfoClient.config;
            config.showTimeAndDays = state;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud coordinates [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("坐标").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            GameInfoConfig config = GameinfoClient.config;
            config.showCoordinates = state;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud nether [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("下届坐标").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            GameInfoConfig config = GameinfoClient.config;
            config.showNetherCoordinates = state;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud biome [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("群系").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            GameInfoConfig config = GameinfoClient.config;
            config.showBiome = state;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud x [position: int]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("x坐标").then(CommandManager.argument("position", IntegerArgumentType.integer()).executes(context -> {
            int position = IntegerArgumentType.getInteger(context, "position");
            GameInfoConfig config = GameinfoClient.config;
            config.xPos = position;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud y [position: int]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("y坐标").then(CommandManager.argument("position", IntegerArgumentType.integer()).executes(context -> {
            int position = IntegerArgumentType.getInteger(context, "position");
            GameInfoConfig config = GameinfoClient.config;
            config.yPos = position;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud color [color: string]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("颜色").then(CommandManager.argument("color", StringArgumentType.string()).executes(context -> {
            String color = StringArgumentType.getString(context, "color");
            GameInfoConfig config = GameinfoClient.config;
            config.color = Integer.decode("0x" + color);
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud  标注 [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("标注").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            GameInfoConfig config = GameinfoClient.config;
            config.remark = state;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud 大小 [scale: double]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("大小").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> {
            double scale = DoubleArgumentType.getDouble(context, "scale");
            GameInfoConfig config = GameinfoClient.config;
            config.scale = scale;
            config.saveConfig();
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud enable [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("启用").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            GameInfoConfig config = GameinfoClient.config;
            //todo
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("reload")).executes(
                context -> {
                    GameInfoConfig config = GameinfoClient.config;
                    config.loadConfig();
                    return ControlFlowAware.Command.SINGLE_SUCCESS;
                }
        ));
    }
}
