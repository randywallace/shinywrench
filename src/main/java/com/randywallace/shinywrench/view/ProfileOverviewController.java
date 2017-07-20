package com.randywallace.shinywrench.view;

import com.randywallace.shinywrench.Main;
import com.randywallace.shinywrench.model.Profile;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
		}
	}

	@FXML
	private void handleEditProfile() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		if (selectedProfile != null) {
			boolean okClicked = this.mainApp.showProfileEditDialog(selectedProfile);
			if (okClicked) {
				showProfileDetails(selectedProfile);
			}

		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Profile Selected");
			alert.setContentText("Please select a person in the table.");

			alert.showAndWait();
		}
	}

	@FXML
	private void handleDeleteProfile() {
		int selectedIndex = this.profileTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			this.profileTable.getItems().remove(selectedIndex);
			System.out.println(this.mainApp.getSystemProfile().getProfileData().toString());
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
		Integer mfaCode = this.mainApp.showMfaEntryDialog();
		if (mfaCode != null)
			System.out.println(mfaCode.toString());
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
