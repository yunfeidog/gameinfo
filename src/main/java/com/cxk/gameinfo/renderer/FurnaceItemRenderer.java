package com.cxk.gameinfo.renderer;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FurnaceItemRenderer implements BlockEntityRenderer<FurnaceBlockEntity> {

    private static final Logger log = LoggerFactory.getLogger(FurnaceItemRenderer.class);

    public FurnaceItemRenderer(BlockEntityRendererFactory.Context ctx) {

    }


    @Override
    public void render(FurnaceBlockEntity furnaceBlockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        BlockPos pos = furnaceBlockEntity.getPos();
        World world = furnaceBlockEntity.getWorld();
        getFurnaceItemsFromServer(pos);
//        FurnaceDataCache.debug();
        FurnaceDataCache.FurnaceItems furnaceData = FurnaceDataCache.getFurnaceData(pos);
        if (furnaceData == null) return;
        if (world == null) return;

        ItemStack input = furnaceData.input;
        ItemStack output = furnaceData.output;

        // 渲染输出物品（熔炉顶部）
        if (!output.isEmpty()) {
            matrices.push();
            matrices.translate(0.5, 1.3, 0.5); // 熔炉正上方
            renderRotatingItem(output, matrices, vertexConsumers, getLightLevel(world, pos), overlay, furnaceBlockEntity, tickProgress);
            matrices.pop();
        }


        // 渲染输入物品（2D风格贴在熔炉顶部）
        // 渲染输入物品（贴在熔炉正面上半部分）
        if (!input.isEmpty()) {
            matrices.push();

            // 获取熔炉朝向
            Direction facing = world.getBlockState(pos).get(Properties.HORIZONTAL_FACING);

            // 基础位置调整到方块中心
            matrices.translate(0.5f, 0.75f, 0.5f); // Y轴0.75f使其在上半部分

            // 根据熔炉朝向调整位置和旋转
            switch (facing) {
                case NORTH:
                    matrices.translate(0f, 0f, -0.505f); // 贴在北面
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    break;
                case SOUTH:
                    matrices.translate(0f, 0f, 0.505f); // 贴在南面
                    break;
                case WEST:
                    matrices.translate(-0.505f, 0f, 0f); // 贴在西面
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                    break;
                case EAST:
                    matrices.translate(0.505f, 0f, 0f); // 贴在东面
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
                    break;
            }

            // 物品缩放
            float scale =0.35f; // 缩放比例
            matrices.scale(scale,scale,0.01f); // Z轴很小，使其看起来像贴图

            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    input,
                    ItemDisplayContext.FIXED, // 使用FIXED模式更适合固定显示
                    WorldRenderer.getLightmapCoordinates(world, pos.up()),
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertexConsumers,
                    world,
                    0
            );
            matrices.pop();
        }

    }

    private void renderRotatingItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, FurnaceBlockEntity furnace, float tickProgress) {
        float angle = (furnace.getWorld().getTime() + tickProgress) % 360;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        float scale = 0.75f; // 缩放比例
        matrices.scale(scale, scale, scale); // 适当缩放

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ItemDisplayContext.GROUND,
                light,
                overlay,
                matrices,
                vertexConsumers,
                furnace.getWorld(),
                0
        );
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }


    // 从服务端获取熔炉物品数据
    private void getFurnaceItemsFromServer(BlockPos furnacePos) {
        MinecraftClient client = MinecraftClient.getInstance();
        IntegratedServer server = client.getServer();
        if (server == null) return;

        // 在服务端线程中执行
        server.execute(() -> {
            World serverWorld = null;
            if (client.world != null) {
                serverWorld = server.getWorld(client.world.getRegistryKey());
            }
            if (serverWorld == null) {
                return;
            }

            // 获取熔炉方块实体
            if (serverWorld.getBlockEntity(furnacePos) instanceof FurnaceBlockEntity furnace) {
                ItemStack input = furnace.getStack(0);
                ItemStack fuel = furnace.getStack(1);
                ItemStack output = furnace.getStack(2);

                // 切换回客户端线程显示结果
                client.execute(() -> {
                    // 更新缓存
//                    logger("更新熔炉数据缓存: " + furnacePos);
                    FurnaceDataCache.updateFurnaceData(furnacePos, input, fuel, output);
                });
            }
        });
    }

    public static void logger(String message) {
        System.out.println("[DEBUG]： " + message);
    }
}
