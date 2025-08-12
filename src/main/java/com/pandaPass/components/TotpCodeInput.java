package com.pandaPass.components;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;
import java.util.function.Consumer;

public class TotpCodeInput {
    private final List<TextField> codeFields;
    private Consumer<String> onComplete;

    public TotpCodeInput(List<TextField> fields){
        this.codeFields = fields;

        for(int idx = 0; idx < fields.size(); idx++){
            configTextField(fields.get(idx), idx);
        }

        this.codeFields.getFirst().requestFocus();

    }

    private void configTextField(TextField tf, int index){
        // Prevent more than 1 characters and other than decimals
        tf.setTextFormatter(new TextFormatter<>(change -> {
            if(change.getControlNewText().length() > 1) return null;
            if(!change.getText().matches("\\d*")) return null;
            return change;
        }));

        disableFocusTraversal(tf);

        tf.setOnKeyPressed(event -> {
            // Stay on text if empty (prohibits TAB from going traversal cycle)
            if(event.getCode() == KeyCode.TAB){
                if(tf.getText().isEmpty()){
                    event.consume();
                } else if(index < codeFields.size() - 1){
                    moveCaretRight(index);
                    event.consume();
                }
            } else if(event.getCode() == KeyCode.BACK_SPACE) {
                if(index > 0){
                    if(!tf.getText().isEmpty()){
                        tf.clear();
                    }
                    moveCaretLeft(index);
                }
            } else if(event.getCode() == KeyCode.LEFT && index > 0){
                moveCaretLeft(index);
            } else if(event.getCode() == KeyCode.RIGHT && index < codeFields.size() - 1){
                moveCaretRight(index);
            } else if(event.getCode() == KeyCode.ENTER){
                verify();
            }
        });

        tf.setOnKeyTyped(event -> {
            String character = event.getCharacter();
            if(character.matches("\\d")){
                tf.setText(character);
                if(index < codeFields.size() - 1){
                    moveCaretRight(index);
                } else {
                    // Delay verify to run after text is displayed
                    Platform.runLater(this::verify);
                }
            }
            event.consume();
        });
    }

    private void moveCaretLeft(int index){
        TextField prev = codeFields.get(index - 1);
        prev.requestFocus();
        prev.positionCaret(prev.getText().length());
    }

    private void moveCaretRight(int index){
        TextField next = codeFields.get(index + 1);
        next.requestFocus();
        next.positionCaret(next.getText().length());
    }

    public String getCode(){
        StringBuilder sb = new StringBuilder();
        codeFields.forEach(field -> sb.append(field.getText()));
        return sb.toString();
    }

    public void clear(){
        codeFields.forEach(TextField::clear);
        codeFields.getFirst().requestFocus();
    }

    public void setOnComplete(Consumer<String> handler){
        this.onComplete = handler;
    }

    public boolean isComplete(){
        return codeFields.stream().noneMatch(field -> field.getText().isEmpty());
    }

    public void verify(){
        if(isComplete() && onComplete != null){
            onComplete.accept(getCode());
        }
    }

    private void disableFocusTraversal(TextField tf){
        tf.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.TAB){
                event.consume();
            }
        });
    }
}
