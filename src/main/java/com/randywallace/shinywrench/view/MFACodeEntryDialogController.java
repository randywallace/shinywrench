package com.randywallace.shinywrench.view;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.amazonaws.services.securitytoken.model.AWSSecurityTokenServiceException;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.randywallace.shinywrench.aws.GenerateSessionCredentials;
import com.randywallace.shinywrench.model.Profile;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MFACodeEntryDialogController {

	private static Logger LOG = LoggerFactory.getLogger(MFACodeEntryDialogController.class);
	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;
	@FXML
	private TextField mfaCodeField;
	@FXML
	private Label currentProfile;
	@FXML
	private ChoiceBox<String> sourceProfileChoices = new ChoiceBox<String>(FXCollections.observableArrayList());
	private Profile profile;
	private Stage dialogStage;
	private String mfaCode;
	private Credentials sessionCredentials;
	private ObservableList<Profile> profileList;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	    Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	    // Focus MFA Code entry on Stage display
	            mfaCodeField.requestFocus();
	            // Set OK Button so Enter can activate it on focus (instead of space)
	            okButton.defaultButtonProperty().bind(okButton.focusedProperty());
	        }
	    });
	}

	public void setCurrentProfile(Profile profile) {
		this.profile = profile;
		this.currentProfile.setText(profile.getProfile());
	}

	public void setAvailableProfiles(ObservableList<Profile> profileList) {
		this.profileList = profileList;
		for (Profile entry : this.profileList) {
			if (!this.profile.getProfile().equals(entry.getProfile())) {
				this.sourceProfileChoices.getItems().add(entry.getProfile());
			}
		}
		if ((this.profile.getSourceProfile() != null) && !this.profile.getSourceProfile().isEmpty()) {
			for (Profile entry : this.profileList) {
				if (this.profile.getSourceProfile().equals(entry.getProfile())) {
					this.sourceProfileChoices.getSelectionModel().select(entry.getProfile());
				}
			}
		}
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public String getMfaCode() {
		return this.mfaCode;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			this.mfaCode = this.mfaCodeField.getText();
			String source_profile_selection = this.sourceProfileChoices.getSelectionModel().getSelectedItem();
			String access_key_id = null;
			String secret_access_key = null;
			String region = null;
			String mfa_serial = null;
			this.profile.setSourceProfile(source_profile_selection);
			for (Profile entry : this.profileList) {
				if (entry.getProfile().equals(source_profile_selection)) {

					access_key_id = entry.getAccessKeyId();
					secret_access_key = entry.getSecretAccessKey();
					region = this.profile.getRegion();
					mfa_serial = this.profile.getMfaSerial();
					LOG.debug(source_profile_selection + " " + access_key_id + " " + secret_access_key + " " + region + " " + mfa_serial + " " + this.mfaCode);
					try {
						this.sessionCredentials = new GenerateSessionCredentials(access_key_id, secret_access_key, region)
								.getMFACredentials(mfa_serial, this.mfaCode);
						this.profile.setAccessKeyId(this.sessionCredentials.getAccessKeyId());
						this.profile.setSecretAccessKey(this.sessionCredentials.getSecretAccessKey());
						this.profile.setSessionToken(this.sessionCredentials.getSessionToken());
						this.profile.setExpiration(this.sessionCredentials.getExpiration());
					} catch (AWSSecurityTokenServiceException e) {
					    LOG.error(e.getMessage());
						Alert alert = new Alert(AlertType.ERROR);
						alert.initOwner(this.dialogStage);
						alert.setTitle("Invalid Fields");
						alert.setHeaderText("Please correct invalid fields");
						alert.setContentText(e.getErrorMessage());
						alert.showAndWait();

					}
				}
			}
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
