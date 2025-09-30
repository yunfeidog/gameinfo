package com.cxk.gameinfo.config;

import com.cxk.gameinfo.util.HexUtil;
import net.minecraft.util.Colors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GameInfoConfig {
    public boolean showFPS = true; // 是否显示FPS
    public boolean showTimeAndDays = true; // 是否显示时间
    public boolean showCoordinates = true; // 是否显示坐标
    public boolean showNetherCoordinates = true; // 是否显示下届坐标
    public boolean showBiome = false; // 是否显示群系
    public int color = Colors.YELLOW; // 文字颜色
    public Integer xPos = 3; // x 坐标
    public Integer yPos = 3; // y 坐标
    public boolean remark = true; // 是否显示备注
    public double scale = 0.5; // 文字
    public String version = "1.21.9";
    public boolean showEquipment = true;
    public boolean showFurnaceInfo = false; // 是否显示熔炉信息
    public boolean showEntityInfo = false; // 是否显示生物信息
    public boolean showBlockInfo = false; // 是否显示方块信息
    private static final String CONFIG_FILE = "config" + File.separator + "gameinfo.properties";

    public boolean enabled = true; // 是否启用游戏信息显示

    public GameInfoConfig() {
        loadConfig();
    }

    /**
     * @param flag true代表关闭，false代表开启
     */
    public void closeGameInfo(Boolean flag) {
        if (flag) {
            showFPS = false;
            showTimeAndDays = false;
            showCoordinates = false;
            showNetherCoordinates = false;
            showBiome = false;
            remark = false;
            showEquipment = false;
            showFurnaceInfo = false;
            showEntityInfo = false;
            showBlockInfo = false;
        } else {
            loadConfig();
        }
    }


    public void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            saveConfig(); // 如果文件不存在，保存默认配置
        }
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            showFPS = Boolean.parseBoolean(properties.getProperty("showFPS", "true"));
            showTimeAndDays = Boolean.parseBoolean(properties.getProperty("showTimeAndDays", "true"));
            showCoordinates = Boolean.parseBoolean(properties.getProperty("showCoordinates", "true"));
            showNetherCoordinates = Boolean.parseBoolean(properties.getProperty("showNetherCoordinates", "true"));
            showBiome = Boolean.parseBoolean(properties.getProperty("showBiome", "false"));
            color = (int) Long.parseLong(properties.getProperty("color", "0xFFFFFF00").replace("0x", ""), 16);
            xPos = Integer.parseInt(properties.getProperty("xPos", "3"));
            yPos = Integer.parseInt(properties.getProperty("yPos", "3"));
            remark = Boolean.parseBoolean(properties.getProperty("remark", "true"));
            scale = Double.parseDouble(properties.getProperty("scale", "0.5"));
            version = properties.getProperty("version", "1.21.6");
            showEquipment = Boolean.parseBoolean(properties.getProperty("showEquipment", "true"));
            showFurnaceInfo = Boolean.parseBoolean(properties.getProperty("showFurnaceInfo", "true"));
            showEntityInfo = Boolean.parseBoolean(properties.getProperty("showEntityInfo", "true"));
            showBlockInfo = Boolean.parseBoolean(properties.getProperty("showBlockInfo", "true"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        Properties properties = new Properties();
        properties.setProperty("showFPS", Boolean.toString(showFPS));
        properties.setProperty("showTimeAndDays", Boolean.toString(showTimeAndDays));
        properties.setProperty("showCoordinates", Boolean.toString(showCoordinates));
        properties.setProperty("showNetherCoordinates", Boolean.toString(showNetherCoordinates));
        properties.setProperty("showBiome", Boolean.toString(showBiome));
        properties.setProperty("color", HexUtil.toHex(color));
        properties.setProperty("xPos", xPos.toString());
        properties.setProperty("yPos", yPos.toString());
        properties.setProperty("remark", Boolean.toString(remark));
        properties.setProperty("scale", Double.toString(scale));
        properties.setProperty("version", version);
        properties.setProperty("showEquipment", Boolean.toString(showEquipment));
        properties.setProperty("showFurnaceInfo", Boolean.toString(showFurnaceInfo));
        properties.setProperty("showEntityInfo", Boolean.toString(showEntityInfo));
        properties.setProperty("showBlockInfo", Boolean.toString(showBlockInfo));
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
