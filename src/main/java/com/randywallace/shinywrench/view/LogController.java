package com.randywallace.shinywrench.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogController {
    private static Logger LOG = LoggerFactory.getLogger(LogController.class);

    @FXML
    private TextArea textArea;

    @FXML
    private void initialize() {
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> textArea.selectPositionCaret(textArea.getLength()));
            Platform.runLater(() -> textArea.deselect());
        });
    }

    public void setTextArea(TextArea inTextArea) {
        textArea.textProperty().bind(inTextArea.textProperty());
    }
}
