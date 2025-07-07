package com.cxk.gameinfo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkLoader implements CommandRegistrationCallback {

    private static final Map<String, Set<ChunkPos>> loadedChunks = new HashMap<>();

    @Override
    public void register(CommandDispatcher<ServerCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        commandDispatcher.register(CommandManager.literal("chunk")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("load")
                        .executes(this::loadChunk))
                .then(CommandManager.literal("unload")
                        .executes(this::unloadChunk))
                .then(CommandManager.literal("list")
                        .executes(this::listChunks))
                .then(CommandManager.literal("check")
                        .then(CommandManager.argument("x", IntegerArgumentType.integer())
                                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                                        .executes(this::checkChunk)))));
    }


    private int loadChunk(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        World world = source.getWorld();
        BlockPos pos = source.getPlayer().getBlockPos();
        ChunkPos chunkPos = new ChunkPos(pos);
        String worldKey = getWorldKey(world);

        loadedChunks.computeIfAbsent(worldKey, k -> new HashSet<>()).add(chunkPos);

        // 强制加载区块
        world.getChunkManager().setChunkForced(chunkPos, true);

        source.sendFeedback(() -> Text.literal(
                        String.format("§a已加载区块 [%d, %d] 在世界 %s",
                                chunkPos.x, chunkPos.z, world.getRegistryKey().getValue())),
                true);

        return 1;
    }

    private int unloadChunk(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        World world = source.getWorld();
        ServerPlayerEntity player = source.getPlayer();

        ChunkPos chunkPos = new ChunkPos(player.getBlockPos());
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        if (chunks != null && chunks.remove(chunkPos)) {
            // 取消强制加载
            world.getChunkManager().setChunkForced(chunkPos, false);
            source.sendFeedback(() -> Text.literal(
                            String.format("§c已取消加载区块 [%d, %d] 在世界 %s",
                                    chunkPos.x, chunkPos.z, world.getRegistryKey().getValue())),
                    true);

            if (chunks.isEmpty()) {
                loadedChunks.remove(worldKey);
            }
        } else {
            source.sendFeedback(() -> Text.literal(
                            String.format("§e区块 [%d, %d] 未被此mod加载",
                                    chunkPos.x, chunkPos.z)),
                    false);
        }

        return 1;
    }

    private int listChunks(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        World world = source.getWorld();
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        if (chunks == null || chunks.isEmpty()) {
            source.sendFeedback(() -> Text.literal(
                            String.format("§e当前世界 %s 没有被此mod加载的区块",
                                    world.getRegistryKey().getValue())),
                    false);
        } else {
            source.sendFeedback(() -> Text.literal(
                            String.format("§a世界 %s 中被此mod加载的区块:",
                                    world.getRegistryKey().getValue())),
                    false);

            for (ChunkPos chunk : chunks) {
                source.sendFeedback(() -> Text.literal(
                                String.format("§f  - [%d, %d]", chunk.x, chunk.z)),
                        false);
            }
        }

        return 1;
    }

    private int checkChunk(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        World world = source.getWorld();
        int x = IntegerArgumentType.getInteger(context, "x");
        int z = IntegerArgumentType.getInteger(context, "z");
        BlockPos blockPos = new BlockPos(x, 0, z);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        boolean isThisMod = chunks != null && chunks.contains(chunkPos);
        boolean isLoaded = world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z);

        source.sendFeedback(() -> Text.literal(
                        String.format("§f区块 [%d, %d] 状态检查: 是否加载: %b, 是否被此mod设置为永久加载: %b", chunkPos.x, chunkPos.z, isLoaded, isThisMod)),
                false);

        int loadedChunkCount = world.getChunkManager().getLoadedChunkCount();
        for (Long forcedChunk : world.getChunkManager().getForcedChunks()) {
            System.out.println("Forced Chunk: " + forcedChunk);
        }

        if (isLoaded) {
            source.sendFeedback(() -> Text.literal(
                            String.format("§a区块 [%d, %d] 已加载，当前已加载区块数量为：%d", chunkPos.x, chunkPos.z, loadedChunkCount)),
                    false);
        } else {
            source.sendFeedback(() -> Text.literal(
                            String.format("§c区块 [%d, %d] 未加载", chunkPos.x, chunkPos.z)),
                    false);
        }


        return 1;
    }

    private String getWorldKey(World world) {
        return world.getRegistryKey().getValue().toString();
    }


}