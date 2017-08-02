package com.randywallace.shinywrench;

import java.io.IOException;

import com.randywallace.shinywrench.logback.JavaFXTextAreaAppender;
import com.randywallace.shinywrench.model.Profile;
import com.randywallace.shinywrench.model.SystemProfile;
import com.randywallace.shinywrench.view.*;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main extends Application {

	private static Logger LOG = LoggerFactory.getLogger(Main.class);

	private Stage primaryStage;
	private BorderPane rootLayout;
	private SystemProfile systemProfile;
	private MainSystemTray systemTray;
	static String MSG_OPEN = "open";

	@Override
	public void start(Stage primaryStage) {

		Platform.setImplicitExit(false);
		
		this.systemProfile = new SystemProfile();

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Shiny Wrench - Developer AWS Credentials Configurator");

		LOG.info("Starting up ShinyWrench");

		switch (System.getProperty("os.name")) {
		case "Linux":
		case "Mac OS X":
		case "Windows 10":
			this.systemTray = new MainSystemTray(this.primaryStage, this.systemProfile);
			this.systemTray.doSetup();
			break;
		
		default:
			break;
		}

		initRootLayout();

		showProfileOverview();

	}

	/**
	 * Initializes the root layout.
	 */
	private void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
			this.rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(this.rootLayout);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			RootController controller = loader.getController();
			controller.setMainApp(this);
			
			this.primaryStage.setScene(scene);
			this.primaryStage.show();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void showLog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/Log.fxml"));
			AnchorPane logView = loader.load();

			Stage logStage = new Stage();
			Scene scene = new Scene(logView);
			logStage.setScene(scene);
			LogController logController = loader.getController();
			logController.setTextArea(JavaFXTextAreaAppender.getLogtextArea());
			logStage.show();

		} catch (IOException e) {
		    LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Shows the profile overview inside the root layout.
	 */
	private void showProfileOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/ProfileOverview.fxml"));
			AnchorPane profileOverview = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			this.rootLayout.setCenter(profileOverview);

			// Give the controller access to the main app.
			ProfileOverviewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
		    LOG.error(e.getMessage(), e);
		}
	}

	public String showMfaEntryDialog(Profile currentProfile, ObservableList<Profile> profileList) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/MFACodeEntryDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter MFA Code");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			MFACodeEntryDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCurrentProfile(currentProfile);
			controller.setAvailableProfiles(profileList);
			dialogStage.showAndWait();
			return controller.getMfaCode();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;

		}

	}

	public boolean showProfileEditDialog(Profile profile) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/ProfileEditDialog.fxml"));
			AnchorPane page = loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Profile");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			ProfileEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(profile);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
		    LOG.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Returns the main stage.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	public static void main(String[] args) {
		String uniqueAppId = "ShinyWrench";
		try {
			JUnique.acquireLock(uniqueAppId);
		} catch (AlreadyLockedException e) {
			LOG.error(uniqueAppId + " Already running! Bailing!");
			System.exit(1);
		}
		launch(args);
	}

	public SystemProfile getSystemProfile() {
		return this.systemProfile;
	}

}
