package com.fw.main;

public class Config {
    final String projectName;
    int initWindowWidth;
    int initWindowHeight;
    public String getProjectName() { return projectName; }
    public int getInitWindowWidth() { return initWindowWidth; }
    public int getInitWindowHeight() { return initWindowHeight; }

    public Config(Config.Builder builder) {
        this.projectName = builder.projectName;
        this.initWindowWidth = builder.initWindowWidth;
        this.initWindowHeight = builder.initWindowHeight;
    }

    public static class Builder {
        final String projectName;
        int initWindowWidth = 1200;
        int initWindowHeight = 300;
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
    }
}
