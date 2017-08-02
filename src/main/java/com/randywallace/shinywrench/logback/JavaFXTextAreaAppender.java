package com.randywallace.shinywrench.logback;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.UnsupportedEncodingException;

public class JavaFXTextAreaAppender<E> extends UnsynchronizedAppenderBase<E> {

    private static TextArea logTextArea;
    private Encoder<E> encoder;

    @Override
    public void start() {
        logTextArea = new TextArea();
        super.start();
    }

    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }
        String message = null;
        try {
            message = new String(this.encoder.encode(eventObject), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String finalMessage = message;
        Platform.runLater( () ->
          logTextArea.appendText(finalMessage)
        );
    }

    public static TextArea getLogtextArea() {
        return logTextArea;
    }


    public Encoder<E> getEncoder() {
        return encoder;
    }

     public void setEncoder(Encoder<E> encoder) {
         this.encoder = encoder;
    }
}
