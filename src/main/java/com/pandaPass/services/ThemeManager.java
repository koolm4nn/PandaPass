package com.pandaPass.services;

import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemeManager {
    public enum Theme {
        LIGHT, DARK
    }

    private static Theme currentTheme = Theme.LIGHT;

    private static final String APPLICATION_CSS = "/styles/application.css";
    private static final String LIGHT_CSS = "/styles/light-theme.css";
    private static final String DARK_CSS = "/styles/dark-theme.css";

    private static final String DARK_ICON_PATH = "/icons/theme/moon.png";
    private static final String LIGHT_ICON_PATH = "/icons/theme/sun.png";

    //private static final List<Scene> registeredScenes = new ArrayList<>();

    private static Scene mainScene;

    public static String getDarkIconPath(){
        return DARK_ICON_PATH;
    }

    public static String getLightIconPath(){
        return LIGHT_ICON_PATH;
    }

    public static void registerMainScene(Scene scene){
        mainScene = scene;
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(APPLICATION_CSS)).toExternalForm());
        applyCurrentTheme();
    }

    //public static void registerScene(Scene scene){
    //    scene.getStylesheets().clear();
    //    scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(APPLICATION_CSS)).toExternalForm());
    //    applyCurrentTheme(scene);
    //    registeredScenes.add(scene);
    //}

    public static void switchTheme(Theme theme){
        currentTheme = theme;
        //for(Scene scene : registeredScenes){
        //    applyCurrentTheme(scene);
        //}
        applyCurrentTheme();
    }

    private static void applyCurrentTheme(){
        mainScene.getStylesheets().removeIf(css -> css.contains("light-theme.css") || css.contains("dark-theme.css"));
        String themeCss = currentTheme == Theme.LIGHT? LIGHT_CSS : DARK_CSS;
        mainScene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(themeCss)).toExternalForm());
    }

    //private static void applyCurrentTheme(Scene scene){
    //    scene.getStylesheets().removeIf(css -> css.contains("light-theme.css") || css.contains("dark-theme.css"));
    //    String themeCss = currentTheme == Theme.LIGHT? LIGHT_CSS : DARK_CSS;
    //    scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(themeCss)).toExternalForm());
    //}

    public static ThemeManager.Theme getCurrentTheme(){
        return currentTheme;
    }
}
