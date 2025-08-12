package com.pandaPass;

import javafx.application.Application;
import javafx.stage.Stage;

public class PandaPassFX extends Application {

    @Override
    public void start(Stage primaryStage){
        AppLifecycle.startApplication(primaryStage);
    }

    public static void main(String[] args){
        launch(args);
    }
}