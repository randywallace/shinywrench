package com.randywallace.shinywrench.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;

import com.randywallace.shinywrench.Main;
import com.randywallace.shinywrench.aws.TestAWSAccess;
import com.randywallace.shinywrench.model.Profile;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.util.Duration;

public class ProfileOverviewController {

	private static Logger LOG = LoggerFactory.getLogger(ProfileOverviewController.class);

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

	private Timeline watchExpiration;

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
		this.profileColumn.setCellValueFactory(cellData -> cellData.getValue().profileProperty());
		this.regionColumn.setCellValueFactory(cellData -> cellData.getValue().regionProperty());

		// Show default profile
        Platform.runLater(() -> {
                    this.profileTable.getSelectionModel().select(0);
                    this.profileTable.getFocusModel().focus(0);
                    showProfileDetails(this.profileTable.getSelectionModel().getSelectedItem());
                });

		// Listen for selection changes and show the person details when changed.
		this.profileTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showProfileDetails(newValue));

         startWatchingExpiration();
	}

	private void startWatchingExpiration() {
		this.watchExpiration = new Timeline(
			new KeyFrame(Duration.minutes(1), event -> {
				this.expirationLabel.setText(
					String.valueOf(this.profileTable.getSelectionModel().selectedItemProperty()
						.get().getExpirationMinutesFromNow()));
					LOG.info("Checked Expiration of profile " +
						this.profileTable.getSelectionModel().selectedItemProperty().get().getProfile());
				}
			)
		);

		this.watchExpiration.setCycleCount(Timeline.INDEFINITE);
		this.watchExpiration.play();
	}

	@FXML
	private void handleNewProfile() {
		Profile tempProfile = new Profile(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
		boolean okClicked = this.mainApp.showProfileEditDialog(tempProfile);
		if (okClicked) {
			this.mainApp.getSystemProfile().getProfileData().add(tempProfile);
			this.mainApp.getSystemProfile().saveConfig();
			this.profileTable.sort();
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
			this.mainApp.displayAlert(AlertType.WARNING,
                    "No Selection",
                    "No Profile Selected",
                    "Please select a profile in the table.");
		}
	}

	@FXML
	private void handleDeleteProfile() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		int selectedIndex = this.profileTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			Optional<ButtonType> action = this.mainApp.displayAlert(AlertType.CONFIRMATION,
					"Deletion Confirmation",
					null,
					"Are you sure you want to delete the " + selectedProfile.getProfile() + " profile?");
			if (action.get() == ButtonType.OK) {
				this.profileTable.getItems().remove(selectedIndex);
				this.mainApp.getSystemProfile().saveConfig();
				this.profileTable.sort();
			}
		} else {
			this.mainApp.displayAlert(AlertType.WARNING,
                    "No Selection",
                    "No Profile Selected",
                    "Please select a profile in the table.");
		}
	}

	@FXML
	private void handleRDSPassword() {
		this.mainApp.displayAlert(AlertType.INFORMATION,
                "Not Implemented",
                "",
                "This feature is not yet implemented.");
	}

	@FXML
	private void handleGetMfaCode() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		if (selectedProfile != null) {
			if (selectedProfile.getMfaSerial() != null && !selectedProfile.getMfaSerial().isEmpty()) {
				String mfaCode = this.mainApp.showMfaEntryDialog(selectedProfile, this.profileTable.getItems());
				if (mfaCode != null) {
					showProfileDetails(selectedProfile);
					this.mainApp.getSystemProfile().saveConfig();
				}
			} else {
				this.mainApp.displayAlert(AlertType.WARNING,
                        "No MFA Serial Configured",
                        "",
                        "Please edit the profile and add an MFA Serial");
			}
		} else {
			this.mainApp.displayAlert(AlertType.WARNING,
                    "No Selection",
                    "No Profile Selected",
                    "Please select a profile in the table.");
		}
	}

	@FXML
	private void handleTestAWSAccess() {
		Profile selectedProfile = this.profileTable.getSelectionModel().getSelectedItem();
		if (selectedProfile != null) {
			TestAWSAccess tester = new TestAWSAccess(selectedProfile.getAccessKeyId(),
					selectedProfile.getSecretAccessKey(),
					selectedProfile.getSessionToken(),
					selectedProfile.getRegion());
			if (tester.testS3ListBuckets()) {
				this.mainApp.displayAlert(AlertType.INFORMATION,
                        "S3 List Bucket Test Passed",
                        "S3 List Bucket Test Passed",
                        "");
			} else {
				this.mainApp.displayAlert(AlertType.ERROR,
                        "S3 List Bucket Test Failed",
                        "S3 List Bucket Test Failed",
                        "");
			}
		} else {
			this.mainApp.displayAlert(AlertType.WARNING,
                    "No Selection",
                    "No Profile Selected",
                    "Please select a profile in the table.");
		}
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
     *
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		this.profileTable.setItems(mainApp.getSystemProfile().getProfileData());
		this.profileColumn.setSortType(SortType.ASCENDING);
        this.profileTable.sortPolicyProperty().set(t -> {
            Comparator<Profile> comparator = (r1, r2)
                    -> r1.getProfile().equals("default") ? -1
                    : r2.getProfile().equals("default") ? 1
                    : t.getComparator() == null ? 0
                    : t.getComparator().compare(r1, r2);
            FXCollections.sort(this.profileTable.getItems(), comparator);
            return true;
        });
        this.profileTable.sort();
	}

	private void showProfileDetails(Profile profile) {
		if (profile != null) {
			// Fill the labels with info from the person object.
			this.profileLabel.textProperty().bind(profile.profileProperty());
			this.accessKeyIdLabel.textProperty().bind(profile.accessKeyIdProperty());
			this.mfaSerialLabel.textProperty().bind(profile.mfaSerialProperty());
			this.sessionTokenLabel.textProperty().bind(profile.sessionTokenProperty());
			this.regionLabel.textProperty().bind(profile.regionProperty());
			this.expirationLabel.setText(String.valueOf(profile.getExpirationMinutesFromNow()));
		} else {
			this.profileLabel.setText("");
			this.accessKeyIdLabel.setText("");
			this.mfaSerialLabel.setText("");
			this.sessionTokenLabel.setText("");
			this.regionLabel.setText("");
			this.expirationLabel.setText("");

		}
	}
}
