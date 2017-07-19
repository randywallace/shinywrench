package com.randywallace.shinywrench;

import java.io.IOException;

import com.randywallace.shinywrench.model.SystemProfile;
import com.randywallace.shinywrench.view.ProfileOverviewController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private SystemProfile systemProfile;
	private MainSystemTray systemTray;

	@Override
	public void start(Stage primaryStage) {
		Platform.setImplicitExit(false);

		this.systemProfile = new SystemProfile();

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Shiny Wrench - Developer AWS Credentials Configurator");

		this.systemTray = new MainSystemTray(this.primaryStage, this.systemProfile);
		this.systemTray.doSetup();

		initRootLayout();

		showProfileOverview();

	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
			this.rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(this.rootLayout);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			this.primaryStage.setScene(scene);
			this.primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the profile overview inside the root layout.
	 */
	public void showProfileOverview() {
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
			e.printStackTrace();
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
		launch(args);
	}

	public SystemProfile getSystemProfile() {
		return this.systemProfile;
	}
}
