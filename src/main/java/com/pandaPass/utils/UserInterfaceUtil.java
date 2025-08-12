package com.pandaPass.utils;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;


public class UserInterfaceUtil {
    private final static long gapTextToIcon = 8;
    private final static long iconFitDimension = 20;
    public static void setButtonIconRight(Button button, String iconPath){
        ImageView icon = createIcon(iconPath);
        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.RIGHT);
        button.setGraphicTextGap(gapTextToIcon);
    }


    public static void setButtonIconLeft(Button button, String iconPath){
        ImageView icon = createIcon(iconPath);
        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(gapTextToIcon);
    }

    public static void setButtonIconCenterWithoutText(Button button, String iconPath){
        ImageView icon = createIcon(iconPath);
        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setGraphicTextGap(0);
    }

    private static ImageView createIcon(String iconPath){
        Image image = new Image(Objects.requireNonNull(UserInterfaceUtil.class.getResourceAsStream(iconPath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(iconFitDimension);
        imageView.setFitHeight(iconFitDimension);
        return imageView;
    }
}
