package com.worldpay.forms;

public class BrowserInfo {
    private Boolean javaEnabled;
    private Boolean javascriptEnabled;
    private String language;
    private Integer colorDepth;
    private Integer screenHeight;
    private Integer screenWidth;
    private String timeZone;

    private String userAgent;

    public BrowserInfo() {
    }

    public Boolean getJavaEnabled() {
        return javaEnabled;
    }

    public void setJavaEnabled(Boolean javaEnabled) {
        this.javaEnabled = javaEnabled;
    }

    public Boolean getJavascriptEnabled() {
        return javascriptEnabled;
    }

    public void setJavascriptEnabled(Boolean javascriptEnabled) {
        this.javascriptEnabled = javascriptEnabled;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(Integer colorDepth) {
        this.colorDepth = colorDepth;
    }

    public Integer getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(Integer screenHeight) {
        this.screenHeight = screenHeight;
    }

    public Integer getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(Integer screenWidth) {
        this.screenWidth = screenWidth;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
