package com.randywallace.shinywrench;

import java.awt.event.ActionListener;
import java.net.URL;

import com.randywallace.shinywrench.model.SystemProfile;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import dorkbox.systemTray.util.JavaFX;
import dorkbox.util.CacheUtil;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainSystemTray {

	private static final URL SHINY_WRENCH_AWS = MainSystemTray.class.getResource("favicon-96x96.png");
	// private ActionListener callbackGray;
	private SystemTray systemTray;
	private Stage primaryStage;
	private SystemProfile systemProfile;

	public MainSystemTray(Stage primaryStage, SystemProfile systemProfile) {
		this.primaryStage = primaryStage;
		this.systemProfile = systemProfile;
	}

	public void doSetup() {
		CacheUtil.clear(); // for test apps, make sure the cache is always reset. You should never do this in production.
		this.systemTray = SystemTray.get();
		if (this.systemTray == null) {
			throw new RuntimeException("Unable to load SystemTray!");
		}
		//SystemTray.ENABLE_SHUTDOWN_HOOK = false;
		this.systemTray.setTooltip("Shiny Wrench");
		this.systemTray.setImage(SHINY_WRENCH_AWS);
		this.systemTray.setStatus("No Mail");

		//		this.callbackGray = new ActionListener() {
		//			@Override
		//			public void actionPerformed(final java.awt.event.ActionEvent e) {
		//				final MenuItem entry = (MenuItem) e.getSource();
		//				MainSystemTray.this.systemTray.setStatus(null);
		//				MainSystemTray.this.systemTray.setImage(BLACK_TRAIN);
		//
		//				entry.setCallback(null);
		//				//	                systemTray.setStatus("Mail Empty");
		//				MainSystemTray.this.systemTray.getMenu().remove(entry);
		//				System.err.println("POW");
		//			}
		//		};

		Menu mainMenu = this.systemTray.getMenu();

		//		MenuItem greenEntry = new MenuItem("Green Mail", new ActionListener() {
		//			@Override
		//			public void actionPerformed(final java.awt.event.ActionEvent e) {
		//				final MenuItem entry = (MenuItem) e.getSource();
		//				MainSystemTray.this.systemTray.setStatus("Some Mail!");
		//				MainSystemTray.this.systemTray.setImage(GREEN_TRAIN);
		//
		//				entry.setCallback(MainSystemTray.this.callbackGray);
		//				entry.setImage(BLACK_MAIL);
		//				entry.setText("Delete Mail");
		//				//                systemTray.remove(menuEntry);
		//			}
		//		});
		//		greenEntry.setImage(GREEN_MAIL);
		//		// case does not matter
		//		greenEntry.setShortcut('G');
		//		mainMenu.add(greenEntry);

		//		Checkbox checkbox = new Checkbox("Euro € Mail", new ActionListener() {
		//			@Override
		//			public void actionPerformed(final java.awt.event.ActionEvent e) {
		//				System.err.println("Am i checked? " + ((Checkbox) e.getSource()).getChecked());
		//			}
		//		});
		//		checkbox.setShortcut('€');
		//		mainMenu.add(checkbox);

		mainMenu.add(new Separator());

		//		mainMenu.add(new MenuItem("About", new ActionListener() {
		//			@Override
		//			public void actionPerformed(final java.awt.event.ActionEvent e) {
		//				try {
		//					Desktop.browseURL("https://github.com/dorkbox/SystemTray");
		//				} catch (IOException e1) {
		//					e1.printStackTrace();
		//				}
		//			}
		//		}));

		//		Menu submenu = new Menu("Options", BLUE_CAMPING);
		//		submenu.setShortcut('t');
		//		mainMenu.add(submenu);
		//
		//		MenuItem disableMenu = new MenuItem("Disable menu", BLACK_BUS, new ActionListener() {
		//			@Override
		//			public void actionPerformed(final java.awt.event.ActionEvent e) {
		//				MenuItem source = (MenuItem) e.getSource();
		//				source.getParent().setEnabled(false);
		//			}
		//		});
		//		submenu.add(disableMenu);
		//
		//		submenu.add(new MenuItem("Hide tray", LT_GRAY_BUS, new ActionListener() {
		//			@Override
		//			public void actionPerformed(final java.awt.event.ActionEvent e) {
		//				MainSystemTray.this.systemTray.setEnabled(false);
		//			}
		//		}));
		//		submenu.add(new MenuItem("Remove menu", BLACK_FIRE, new ActionListener() {
		//			@Override
		//			public void actionPerformed(final java.awt.event.ActionEvent e) {
		//				MenuItem source = (MenuItem) e.getSource();
		//				source.getParent().remove();
		//			}
		//		}));

		this.systemTray.getMenu().add(new MenuItem("Show", new ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				if (MainSystemTray.this.primaryStage.isShowing()) {

					Platform.runLater(() -> {
						MainSystemTray.this.primaryStage.toFront();
					});

				} else {
					Platform.runLater(() -> {
						MainSystemTray.this.primaryStage.show();
						MainSystemTray.this.primaryStage.toFront();
					});
				}
			}
		}));

		this.systemTray.getMenu().add(new MenuItem("Quit", new ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				MainSystemTray.this.systemProfile.saveConfig();
				MainSystemTray.this.systemTray.shutdown();

				if (!JavaFX.isEventThread()) {
					JavaFX.dispatch(new Runnable() {
						@Override
						public void run() {
							MainSystemTray.this.primaryStage.hide(); // must do this BEFORE Platform.exit() otherwise odd errors show up
							Platform.exit(); // necessary to close javaFx
						}
					});
				} else {
					MainSystemTray.this.primaryStage.hide(); // must do this BEFORE Platform.exit() otherwise odd errors show up
					Platform.exit(); // necessary to close javaFx
				}

				//System.exit(0);  not necessary if all non-daemon threads have stopped.
			}
		})).setShortcut('q'); // case does not matter
	}
}
