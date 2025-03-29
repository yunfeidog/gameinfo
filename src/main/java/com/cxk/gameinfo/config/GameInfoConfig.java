package com.cxk.gameinfo.config;

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
    public int color = 0x00FFFF; // 文字颜色
    public Integer xPos = 3; // x 坐标
    public Integer yPos = 3; // y 坐标
    public boolean remark = true; // 是否显示备注
    public double scale = 0.5; // 文字
    public String version = "1.21.5";

    private static final String CONFIG_FILE = "config" + File.separator + "gameinfo.properties";

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
        } else {
            loadConfig();
        }
    }

    public GameInfoConfig() {
        loadConfig();
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
            color = Integer.parseInt(properties.getProperty("color", "00FFFF"), 16);
            xPos = Integer.parseInt(properties.getProperty("xPos", "3"));
            yPos = Integer.parseInt(properties.getProperty("yPos", "3"));
            remark = Boolean.parseBoolean(properties.getProperty("remark", "true"));
            scale = Double.parseDouble(properties.getProperty("scale", "0.5"));
            version = properties.getProperty("version", "1.21.5");
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
        properties.setProperty("color", Integer.toHexString(color));
        properties.setProperty("xPos", xPos.toString());
        properties.setProperty("yPos", yPos.toString());
        properties.setProperty("remark", Boolean.toString(remark));
        properties.setProperty("scale", Double.toString(scale));
        properties.setProperty("version", version);
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
