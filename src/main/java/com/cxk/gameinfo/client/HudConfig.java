package com.cxk.gameinfo.client;


public class HudConfig {
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


    // 在 HudConfig 类中添加一个方法来更新配置
    public void updateConfig(HudConfig newConfig) {
        this.showFPS = newConfig.isShowFPS();
        this.showTimeAndDays = newConfig.isShowTimeAndDays();
        this.showCoordinates = newConfig.isShowCoordinates();
        this.showNetherCoordinates = newConfig.isShowNetherCoordinates();
        this.showBiome = newConfig.isShowBiome();
        this.xPos = newConfig.getxPos();
        this.yPos = newConfig.getyPos();
        this.color = newConfig.getColor();
        this.remark = newConfig.isRemark();
        this.scale = newConfig.getScale();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean isRemark() {
        return remark;
    }

    public void setRemark(boolean remark) {
        this.remark = remark;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

}
