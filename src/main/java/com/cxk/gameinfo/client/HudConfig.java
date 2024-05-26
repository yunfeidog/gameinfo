package com.cxk.gameinfo.client;


public class HudConfig {
    private boolean showFPS = true;
    private boolean showTimeAndDays = true;
    private boolean showCoordinates = true;
    private boolean showNetherCoordinates = true;
    private boolean showBiome = true;
    private Integer xPos = 3;
    private Integer yPos = 3;


    private volatile static HudConfig hudConfig;

    public static HudConfig getInstance() {
        if (hudConfig == null) {
            hudConfig = new HudConfig();
        }
        return hudConfig;
    }

    // 在 HudConfig 类中添加一个方法来更新配置
    public void updateConfig(HudConfig newConfig) {
        this.showFPS = newConfig.isShowFPS();
        this.showTimeAndDays = newConfig.isShowTimeAndDays();
        this.showCoordinates = newConfig.isShowCoordinates();
        this.showNetherCoordinates = newConfig.isShowNetherCoordinates();
        this.showBiome = newConfig.isShowBiome();
        this.xPos = newConfig.getxPos();
        this.yPos = newConfig.getyPos();
    }

    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }

    public boolean isShowFPS() {
        return showFPS;
    }

    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }

    public boolean isShowTimeAndDays() {
        return showTimeAndDays;
    }

    public void setShowTimeAndDays(boolean showTimeAndDays) {
        this.showTimeAndDays = showTimeAndDays;
    }

    public boolean isShowCoordinates() {
        return showCoordinates;
    }

    public void setShowCoordinates(boolean showCoordinates) {
        this.showCoordinates = showCoordinates;
    }

    public boolean isShowNetherCoordinates() {
        return showNetherCoordinates;
    }

    public void setShowNetherCoordinates(boolean showNetherCoordinates) {
        this.showNetherCoordinates = showNetherCoordinates;
    }

    public boolean isShowBiome() {
        return showBiome;
    }

    public void setShowBiome(boolean showBiome) {
        this.showBiome = showBiome;
    }

    public static HudConfig getHudConfig() {
        return hudConfig;
    }

    public static void setHudConfig(HudConfig hudConfig) {
        HudConfig.hudConfig = hudConfig;
    }
}
