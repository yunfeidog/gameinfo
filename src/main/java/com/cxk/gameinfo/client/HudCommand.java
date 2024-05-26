package com.cxk.gameinfo.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class HudCommand {
    public static void register() {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("hud");

        command.then(CommandManager.literal("fps")).then(CommandManager.argument("show", BoolArgumentType.bool()).executes(context -> {
            boolean showFPS = BoolArgumentType.getBool(context, "show");
            HudConfig instance = HudConfig.getInstance();
            instance.setShowFPS(showFPS);
            instance.updateConfig(instance);
            // 显示设置结果
            if (showFPS) {
                context.getSource().sendMessage(Text.of("Show FPS: true"));
            } else {
                context.getSource().sendMessage(Text.of("Show FPS: false"));
            }
            return 1;
        }));
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        // hud fps [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("帧数").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            HudConfig instance = HudConfig.getInstance();
            instance.setShowFPS(state);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud time [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("时间").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            HudConfig instance = HudConfig.getInstance();
            instance.setShowTimeAndDays(state);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud coordinates [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("坐标").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            HudConfig instance = HudConfig.getInstance();
            instance.setShowCoordinates(state);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud nether [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("下届坐标").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            HudConfig instance = HudConfig.getInstance();
            instance.setShowNetherCoordinates(state);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud biome [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("群系").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            HudConfig instance = HudConfig.getInstance();
            instance.setShowBiome(state);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud x [position: int]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("x坐标").then(CommandManager.argument("position", IntegerArgumentType.integer()).executes(context -> {
            int position = IntegerArgumentType.getInteger(context, "position");
            HudConfig instance = HudConfig.getInstance();
            instance.setxPos(position);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud y [position: int]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("y坐标").then(CommandManager.argument("position", IntegerArgumentType.integer()).executes(context -> {
            int position = IntegerArgumentType.getInteger(context, "position");
            HudConfig instance = HudConfig.getInstance();
            instance.setyPos(position);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));
    }
}
