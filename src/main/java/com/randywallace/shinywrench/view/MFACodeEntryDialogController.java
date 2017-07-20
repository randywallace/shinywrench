package com.randywallace.shinywrench.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MFACodeEntryDialogController {
	@FXML
	private TextField mfaCodeField;

	private Stage dialogStage;
	private Integer mfaCode;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public Integer getMfaCode() {
		return this.mfaCode;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			this.mfaCode = Integer.parseInt(this.mfaCodeField.getText());
			this.dialogStage.close();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		this.dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";
		if (this.mfaCodeField.getText().length() != 6) {
			errorMessage += "MFA Code must be six digits!\n";
		}

		try {
			Integer.parseInt(this.mfaCodeField.getText());
		} catch (NumberFormatException e) {
			errorMessage += "MFA Code must be all numbers!\n";
		}

		//
		//		if (this.firstNameField.getText() == null || this.firstNameField.getText().length() == 0) {
		//			errorMessage += "No valid first name!\n";
		//		}
		//		if (this.lastNameField.getText() == null || this.lastNameField.getText().length() == 0) {
		//			errorMessage += "No valid last name!\n";
		//		}
		//		if (this.streetField.getText() == null || this.streetField.getText().length() == 0) {
		//			errorMessage += "No valid street!\n";
		//		}
		//
		//		if (this.postalCodeField.getText() == null || this.postalCodeField.getText().length() == 0) {
		//			errorMessage += "No valid postal code!\n";
		//		} else {
		//			// try to parse the postal code into an int.
		//			try {
		//				Integer.parseInt(this.postalCodeField.getText());
		//			} catch (NumberFormatException e) {
		//				errorMessage += "No valid postal code (must be an integer)!\n";
		//			}
		//		}
		//
		//		if (this.cityField.getText() == null || this.cityField.getText().length() == 0) {
		//			errorMessage += "No valid city!\n";
		//		}
		//
		//		if (this.birthdayField.getText() == null || this.birthdayField.getText().length() == 0) {
		//			errorMessage += "No valid birthday!\n";
		//		} else {
		//			if (!DateUtil.validDate(this.birthdayField.getText())) {
		//				errorMessage += "No valid birthday. Use the format dd.mm.yyyy!\n";
		//			}
		//		}
		//
		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.dialogStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}
	}

}
