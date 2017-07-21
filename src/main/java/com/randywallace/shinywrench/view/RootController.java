package com.randywallace.shinywrench.view;

import com.randywallace.shinywrench.Main;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class RootController {
	
	private Main mainApp;
	
	public RootController() {
	}
	
	@FXML
	private void handleClose() {
		this.mainApp.getSystemProfile().saveConfig();
		this.mainApp.getPrimaryStage().hide(); // must do this BEFORE Platform.exit() otherwise odd errors show up
		Platform.exit(); // necessary to close javaFx
	}
	
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;

	}
}
