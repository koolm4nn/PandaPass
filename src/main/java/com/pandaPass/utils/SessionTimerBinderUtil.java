package com.pandaPass.utils;

import com.pandaPass.services.ServiceLocator;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;

public class SessionTimerBinderUtil {
    public static void bind(Button button){
        button.setOnAction(e -> resetSessionTimer());
    }

    public static void bind(TextInputControl inputControl){
        inputControl.textProperty().addListener((_, _, _) -> resetSessionTimer());
    }

    private static void resetSessionTimer(){
        ServiceLocator.getVaultSessionTimeoutService().resetTimer();
    }

}
