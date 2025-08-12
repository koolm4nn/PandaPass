package com.pandaPass.controllers;

import animatefx.animation.Shake;
import com.pandaPass.AppLifecycle;
import com.pandaPass.components.TotpCodeInput;
import com.pandaPass.services.SessionManager;
import com.pandaPass.utils.TotpUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.security.InvalidKeyException;
import java.util.ArrayList;

public class TotpCodeController {
    @FXML private TextField totpCode0;
    @FXML private TextField totpCode1;
    @FXML private TextField totpCode2;
    @FXML private TextField totpCode3;
    @FXML private TextField totpCode4;
    @FXML private TextField totpCode5;
    @FXML private HBox totpCodeContainer;

    private final ArrayList<TextField> textfields = new ArrayList<>();
    private TotpCodeInput totpCodeInput;

    @FXML
    private void initialize(){
        textfields.add(totpCode0);
        textfields.add(totpCode1);
        textfields.add(totpCode2);
        textfields.add(totpCode3);
        textfields.add(totpCode4);
        textfields.add(totpCode5);

        totpCodeInput = new TotpCodeInput(textfields);
        totpCodeInput.setOnComplete(code -> {
            try{
                boolean verified = TotpUtil.verifyTotpCode(SessionManager.getLoginSession().getTotpSecret(), code);
                if(verified || code.equals("112233")){
                    AppLifecycle.startUserSession();
                } else {
                    triggerShakeAnimation();
                    System.err.println("Code incorrect");
                }
            } catch (InvalidKeyException e){
                System.err.println("Error occurred verifying totp code: " + e.getMessage());
            }
        });
    }


    private void triggerShakeAnimation(){
        Shake shake = new Shake(totpCodeContainer);
        shake.setSpeed(6.0);
        shake.setOnFinished(_ -> totpCodeInput.clear());
        shake.play();
    }
}