package com.randywallace.shinywrench.view;

import java.util.Optional;

import com.randywallace.shinywrench.Main;
import com.randywallace.shinywrench.aws.TestAWSAccess;
import com.randywallace.shinywrench.model.Profile;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ProfileOverviewController {

	@FXML
	private TableView<Profile> profileTable;
	@FXML
	private TableColumn<Profile, String> profileColumn;
	@FXML
	private TableColumn<Profile, String> regionColumn;

	@FXML
	private Label profileLabel;
	@FXML
	private Label accessKeyIdLabel;
	@FXML
	private Label sessionTokenLabel;
	@FXML
	private Label regionLabel;
	@FXML
	private Label outputLabel;
	@FXML
	private Label roleArnCodeLabel;
	@FXML
	private Label sourceProfileLabel;
	@FXML
	private Label mfaSerialLabel;
	@FXML
	private Label expirationLabel;

	// Reference to the main application.
	private Main mainApp;

	/**
	 * The constructor.
	 * The constructor is called before the initialize() method.
	 */
	public ProfileOverviewController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Initialize the profile table with the two columns.
		this.profileColumn.setCellValueFactory(cellData -> cellData.getValue().getProfile());
		this.regionColumn.setCellValueFactory(cellData -> cellData.getValue().getRegion());

		// Clear person details.
		showProfileDetails(null);

		// Listen for selection changes and show the person details when changed.
		this.profileTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showProfileDetails(newValue));
	}

	@FXML
	private void handleNewProfile() {
		Profile tempProfile = new Profile(null, null, null, null, null, null, null, null, null, null);
		boolean okClicked = this.mainApp.showProfileEditDialog(tempProfile);
		if (okClicked) {
			this.mainApp.getSystemProfile().getProfileData().add(tempProfile);
			this.mainApp.getSystemProfile().saveConfig();
		}

	}

	@FXML
	private void handleEditProfile() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		if (selectedProfile != null) {
			boolean okClicked = this.mainApp.showProfileEditDialog(selectedProfile);
			if (okClicked) {
				showProfileDetails(selectedProfile);
				this.mainApp.getSystemProfile().saveConfig();
			}

		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Profile Selected");
			alert.setContentText("Please select a profile in the table.");

			alert.showAndWait();
		}
	}

	@FXML
	private void handleDeleteProfile() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		int selectedIndex = this.profileTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(this.mainApp.getPrimaryStage());
			alert.setTitle("Deletion Confirmation");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete the " + selectedProfile.getProfile().getValue() + " profile?");
			Optional<ButtonType> action = alert.showAndWait();
			if (action.get() == ButtonType.OK) {
				this.profileTable.getItems().remove(selectedIndex);
				this.mainApp.getSystemProfile().saveConfig();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Profile Selected");
			alert.setContentText("Please select a profile in the table.");

			alert.showAndWait();
		}
	}

	@FXML
	private void handleGetMfaCode() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		if (selectedProfile != null ) {
			if (selectedProfile.getMfa_serial().getValue() != null && !selectedProfile.getMfa_serial().getValue().isEmpty()) {
				String mfaCode = this.mainApp.showMfaEntryDialog(selectedProfile, this.profileTable.getItems());
				if (mfaCode != null) {
					showProfileDetails(selectedProfile);
					this.mainApp.getSystemProfile().saveConfig();
				}
			} else  {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(this.mainApp.getPrimaryStage());
				alert.setTitle("No MFA Serial Configured");
				alert.setHeaderText("");
				alert.setContentText("Please edit the profile and add an MFA Serial");

				alert.showAndWait();
			}
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Profile Selected");
			alert.setContentText("Please select a profile in the table.");

			alert.showAndWait();
		}
	}

	@FXML
	private void handleTestAWSAccess() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		if (selectedProfile != null) {
			TestAWSAccess tester = new TestAWSAccess(selectedProfile.getAccess_key_id().getValue(),
					selectedProfile.getSecret_access_key().getValue(),
					selectedProfile.getSession_token().getValue(),
					selectedProfile.getRegion().getValue());
			if (tester.testS3ListBuckets()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.initOwner(this.mainApp.getPrimaryStage());
				alert.setTitle("S3 List Bucket Test Passed");
				alert.setHeaderText("S3 List Bucket Test Passed");
				alert.setContentText("");
				alert.showAndWait();
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(this.mainApp.getPrimaryStage());
				alert.setTitle("S3 List Bucket Test Failed");
				alert.setHeaderText("S3 List Bucket Test Failed");
				alert.setContentText("");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Profile Selected");
			alert.setContentText("Please select a profile in the table.");

			alert.showAndWait();
		}
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		this.profileTable.setItems(mainApp.getSystemProfile().getProfileData());
	}

	private void showProfileDetails(Profile profile) {
		if (profile != null) {
			// Fill the labels with info from the person object.
			this.profileLabel.setText(profile.getProfile().getValue());
			this.accessKeyIdLabel.setText(profile.getAccess_key_id().getValue());
			this.mfaSerialLabel.setText(profile.getMfa_serial().getValue());
			this.sessionTokenLabel.setText(profile.getSession_token().getValue());
			this.regionLabel.setText(profile.getRegion().getValue());
		} else {
			this.profileLabel.setText("");
			this.accessKeyIdLabel.setText("");
			this.mfaSerialLabel.setText("");
			this.sessionTokenLabel.setText("");
			this.regionLabel.setText("");

		}
	}
}
