package com.mxjapp.calendarview.entity;

import android.graphics.Color;

public class StyleAttr {
    private int textColor;
    private int textDimColor;
    private int backgroundSelectedColor;
    private int textSelectedColor;
    private int markColor;
    private int textSize;//dp
    private int horizontalSpace;//dp
    private int verticalSpace;//dp

    public StyleAttr() {
        textColor= Color.parseColor("#333333");
        textDimColor=Color.parseColor("#999999");
        backgroundSelectedColor=Color.parseColor("#FFEEE1");
        textSelectedColor=Color.parseColor("#FF6B00");
        markColor=Color.parseColor("#FF6060");
        textSize=12;
        horizontalSpace=6;
        verticalSpace=6;
    }

    public int getTextColor() {
        return textColor;
    }

    public StyleAttr setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public int getTextDimColor() {
        return textDimColor;
    }

    public StyleAttr setTextDimColor(int textDimColor) {
        this.textDimColor = textDimColor;
        return this;
    }

    public int getBackgroundSelectedColor() {
        return backgroundSelectedColor;
    }

    public StyleAttr setBackgroundSelectedColor(int backgroundSelectedColor) {
        this.backgroundSelectedColor = backgroundSelectedColor;
        return this;
    }

    public int getTextSelectedColor() {
        return textSelectedColor;
    }

    public StyleAttr setTextSelectedColor(int textSelectedColor) {
        this.textSelectedColor = textSelectedColor;
        return this;
    }

    public int getMarkColor() {
        return markColor;
    }

    public StyleAttr setMarkColor(int markColor) {
        this.markColor = markColor;
        return this;
    }

    public int getTextSize() {
        return textSize;
    }

    public StyleAttr setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public StyleAttr setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
        return this;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public StyleAttr setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
        return this;
    }
}
