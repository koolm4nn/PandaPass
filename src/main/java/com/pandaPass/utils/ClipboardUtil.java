package com.pandaPass.utils;

import javafx.animation.PauseTransition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;

public class ClipboardUtil {
    /**
     * Copies the given string to clipboard and clears it after certain seconds.
     * @param content String to copy to clipboard
     * @param clearSeconds Delay after which the clipboard is cleared
     */
    public static void copyContentToClipboardWithTimeout(String content, int clearSeconds){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);

        // Schedule clearing of content
        PauseTransition delay = new PauseTransition(Duration.seconds(clearSeconds));
        delay.setOnFinished(_ -> clipboard.clear());
        delay.play();
    }
}
