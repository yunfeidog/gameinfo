package com.cxk.gameinfo.command;

import com.cxk.gameinfo.GameinfoClient;
import com.cxk.gameinfo.config.GameInfoConfig;
import com.cxk.gameinfo.gui.GameInfoConfigScreen;
import com.cxk.gameinfo.util.HexUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GameInfoCommand implements CommandRegistrationCallback {

    public static void register() {
        GameInfoCommand command = new GameInfoCommand();
        CommandRegistrationCallback.EVENT.register(command);
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        // 新增 /gi 命令打开GUI
        dispatcher.register(CommandManager.literal("gi").executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() -> {
                client.setScreen(new GameInfoConfigScreen(client.currentScreen));
            });
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }));

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
            System.out.println("颜色" + color);
            GameInfoConfig config = GameinfoClient.config;
            config.color = HexUtil.toDecimal("0xFF" + color);
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
    }
}
