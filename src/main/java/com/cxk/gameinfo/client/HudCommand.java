package com.cxk.gameinfo.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class HudCommand {

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

        // hud color [color: string]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("颜色").then(CommandManager.argument("color", StringArgumentType.string()).executes(context -> {
            String color = StringArgumentType.getString(context, "color");
            HudConfig instance = HudConfig.getInstance();
            // 转为16进制 color:	FFB6C1
            color = "0x" + color;
            int color1 = Integer.decode(color);
            instance.setColor(color1);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud  标注 [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("标注").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            HudConfig instance = HudConfig.getInstance();
            instance.setRemark(state);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud 大小 [scale: double]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("大小").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> {
            double scale = DoubleArgumentType.getDouble(context, "scale");
            HudConfig instance = HudConfig.getInstance();
            instance.setScale(scale);
            instance.updateConfig(instance);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));

        // hud enable [state: bool]
        dispatcher.register(CommandManager.literal("gameinfo").then(CommandManager.literal("启用").then(CommandManager.argument("state", BoolArgumentType.bool()).executes(context -> {
            boolean state = BoolArgumentType.getBool(context, "state");
            HudConfig hudConfig = HudConfig.getInstance();
            hudConfig.setShowFPS(state);
            hudConfig.setShowTimeAndDays(state);
            hudConfig.setShowCoordinates(state);
            hudConfig.setShowNetherCoordinates(state);
            hudConfig.setShowBiome(state);
            hudConfig.updateConfig(hudConfig);
            return ControlFlowAware.Command.SINGLE_SUCCESS;
        }))));
    }
}
