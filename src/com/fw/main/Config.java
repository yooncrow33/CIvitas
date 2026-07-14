package com.fw.main;

public class Config {
    final String projectName;
    int initWindowWidth;
    int initWindowHeight;
    boolean useKoreanModule;
    public String getProjectName() { return projectName; }
    public int getInitWindowWidth() { return initWindowWidth; }
    public int getInitWindowHeight() { return initWindowHeight; }
    public boolean isUseKoreanModule() {return useKoreanModule; }

    Config(Config.Builder builder) {
        this.projectName = builder.projectName;
        this.initWindowWidth = builder.initWindowWidth;
        this.initWindowHeight = builder.initWindowHeight;
        this.useKoreanModule = builder.useKoreanModule;
    }

    public static class Builder {
        final String projectName;
        int initWindowWidth = 1200;
        int initWindowHeight = 300;
        boolean useKoreanModule = false;
        public Builder(String projectName) {
            this.projectName = projectName;
        }

        public Builder setWindowWidth(int size) {
            initWindowWidth = size;
            return this;
        }
        public Builder setWindowHeight(int size) {
            initWindowHeight = size;
            return this;
        }
        public Builder setUseKoreanModule(boolean bool) {
            useKoreanModule = bool;
            return this;
        }
        public Config build() {
            return new Config(this);
        }
    }
}
