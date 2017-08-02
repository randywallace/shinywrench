package com.randywallace.shinywrench.view;

import com.randywallace.shinywrench.Main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootController {
	private static Logger LOG = LoggerFactory.getLogger(RootController.class);

	private Main mainApp;
	
	public RootController() {
	}
	
	@FXML
	private void handleClose() {
		this.mainApp.getSystemProfile().saveConfig();
		this.mainApp.getPrimaryStage().hide(); // must do this BEFORE Platform.exit() otherwise odd errors show up
		Platform.exit(); // necessary to close javaFx
	}

	@FXML
	private void handleLog() {
		this.mainApp.showLog();
	}
	
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}
}
