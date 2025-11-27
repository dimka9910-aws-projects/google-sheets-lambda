package com.github.dimka9910.dto;


import com.google.api.services.sheets.v4.model.Color;
import lombok.Getter;

@Getter
public enum ColorEnum {



    WHITE(new Color().setRed(1f).setGreen(1f).setBlue(1f)),
    BLACK(new Color().setRed(0.9f).setGreen(0.9f).setBlue(0.9f)), // светло-серый почти белый
    RED(new Color().setRed(1f).setGreen(0.8f).setBlue(0.8f)),
    GREEN(new Color().setRed(0.8f).setGreen(1f).setBlue(0.8f)),
    BLUE(new Color().setRed(0.8f).setGreen(0.8f).setBlue(1f)),
    YELLOW(new Color().setRed(1f).setGreen(1f).setBlue(0.8f)),
    CYAN(new Color().setRed(0.8f).setGreen(1f).setBlue(1f)),
    MAGENTA(new Color().setRed(1f).setGreen(0.8f).setBlue(1f)),
    ORANGE(new Color().setRed(1f).setGreen(0.9f).setBlue(0.8f)),
    PINK(new Color().setRed(1f).setGreen(0.9f).setBlue(0.95f)),
    GRAY(new Color().setRed(0.9f).setGreen(0.9f).setBlue(0.9f)),
    LIGHT_GRAY(new Color().setRed(0.97f).setGreen(0.97f).setBlue(0.97f)),
    DARK_GRAY(new Color().setRed(0.85f).setGreen(0.85f).setBlue(0.85f));


    Color color;

    ColorEnum(Color color) {
        this.color = color;
    }
}
