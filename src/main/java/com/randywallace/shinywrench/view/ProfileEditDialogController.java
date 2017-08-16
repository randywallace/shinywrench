package com.randywallace.shinywrench.view;

import com.randywallace.shinywrench.model.Profile;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileEditDialogController {

	private static Logger LOG = LoggerFactory.getLogger(ProfileEditDialogController.class);
	@FXML
	private TextField profileField;
	@FXML
	private TextField accessKeyIdField;
	@FXML
	private TextField secretAccessKeyField;
	@FXML
	private TextField sessionTokenField;
	@FXML
	private TextField regionField;
	@FXML
	private TextField mfaSerialField;
	@FXML
	private TextField sourceProfileField;

	private Stage dialogStage;
	private Profile profile;
	private boolean okClicked = false;

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

	/**
	 * Sets the person to be edited in the dialog.
	 * 
	 * @param profile
	 */
	public void setPerson(Profile profile) {
		this.profile = profile;

		this.profileField.setText(profile.getProfile());
		this.accessKeyIdField.setText(profile.getAccessKeyId());
		this.secretAccessKeyField.setText(profile.getSecretAccessKey());
		this.sessionTokenField.setText(profile.getSessionToken());
		this.regionField.setText(profile.getRegion());
		this.mfaSerialField.setText(profile.getMfaSerial());
		this.sourceProfileField.setText(profile.getSourceProfile());
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return this.okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			this.profile.setProfile(this.profileField.getText());
			this.profile.setAccessKeyId(this.accessKeyIdField.getText());
			this.profile.setSecretAccessKey(this.secretAccessKeyField.getText());
			this.profile.setSessionToken(this.sessionTokenField.getText());
			if ( this.regionField.getText() == null || this.regionField.getText().isEmpty()) {
				this.profile.setRegion("us-east-1");
			} else {
				this.profile.setRegion(this.regionField.getText());
			}
			this.profile.setMfaSerial(this.mfaSerialField.getText());
			this.profile.setSourceProfile(this.sourceProfileField.getText());

			this.okClicked = true;
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
		
		if (this.profileField.getText() == null || this.profileField.getText().length() == 0) {
			errorMessage += "You must set a Profile Name!\n";
		}
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
