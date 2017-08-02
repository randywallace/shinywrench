package com.randywallace.shinywrench.logback;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.UnsupportedEncodingException;

public class JavaFXTextAreaAppender<E> extends UnsynchronizedAppenderBase<E> {

    private TextArea logTextArea;
    private Encoder<E> encoder;

    @Override
    public void start() {
        this.logTextArea = new TextArea();
        super.start();
    }

    @Override
    protected void append(E eventObject) {
         if (!isStarted()) {
             return;
         }
         byte[] encoded_event = this.encoder.encode(eventObject);
         String message = null;
         try {
             message = new String(encoded_event, "UTF-8");
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
         String finalMessage = message;
         Platform.runLater( () ->
           this.logTextArea.appendText(finalMessage)
         );
    }

    public TextArea getLogtextArea() {
        return this.logTextArea;
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

     public void setEncoder(Encoder<E> encoder) {
         this.encoder = encoder;
    }
}
