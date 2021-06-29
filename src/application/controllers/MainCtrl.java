package application.controllers;

import application.controllers.windows.MainWindowCtrl;
import application.models.PackageSend;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Distribute;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class MainCtrl {
	private Timer refresh;
	private MySQL db = null;
	private ObservableList<ScaleItemMenu> scales = FXCollections.observableArrayList();
	private ObservableList<PackageSend> packs = FXCollections.observableArrayList();
	// private FTP ftp = null;
	private static MainCtrl instance = null;

	public MainCtrl(/* Stage mainStage */) {
		super();
	}

	public static MainCtrl getInstance() {
		if (instance == null) {
			instance = new MainCtrl();
		}
		return instance;
	}

	public static void openMainWindow(Stage stage) {
		MainWindowCtrl.loadView(stage);
		MainWindowCtrl.showStage();
		MainWindowCtrl.getSidebarCtrl().loadMenu(getScales());
		startRefresh();
	}

	public static void close() {
		MainCtrl me = getInstance();
		if (me.refresh != null)
			me.refresh.cancel();
		System.exit(0);
	}

	public static void startRefresh() {
		Distribute distribute = new Distribute(0,0,0,0,0);
		MainCtrl me = getInstance();

		AtomicInteger timeout = new AtomicInteger(0);

		me.refresh = new Timer();
		me.refresh.schedule(new TimerTask() {
			@Override
			public void run() {
				if (timeout.get() % 60000 == 0) {
					distribute.update();
					timeout.set(0);
				}

				if (MainCtrl.getPacks().size() > 0) {
					Platform.runLater(() -> {
						MainWindowCtrl.getFooterCtrl().startTask(MainCtrl.getPacks().get(0));
						MainCtrl.getPacks().remove(0);
					});
				}

				timeout.incrementAndGet();
			}
		}, 0, 1000);
	}

	public static AnchorPane loadAnchorPane(AnchorPane anchorPane, FXMLLoader loader) throws IOException {
		AnchorPane load = loader.load();
		anchorPane.getChildren().add(load);
		AnchorPane.setBottomAnchor(load, 0.0);
		AnchorPane.setTopAnchor(load, 0.0);
		AnchorPane.setLeftAnchor(load, 0.0);
		AnchorPane.setRightAnchor(load, 0.0);
		return load;
	}

	public static BorderPane loadBorderPane(AnchorPane anchorPane, FXMLLoader loader) throws IOException {
		BorderPane load = loader.load();
		anchorPane.getChildren().add(load);
		AnchorPane.setBottomAnchor(load, 0.0);
		AnchorPane.setTopAnchor(load, 0.0);
		AnchorPane.setLeftAnchor(load, 0.0);
		AnchorPane.setRightAnchor(load, 0.0);
		return load;
	}

	public static MySQL getDB() {
		MainCtrl me = getInstance();
		if (me.db == null) {
			me.db = new MySQL(-1);
		}
		me.db.getDBConnection();
		return me.db;
	}

	public static ObservableList<ScaleItemMenu> getScales() {
		MainCtrl me = getInstance();
		if (me.scales.isEmpty()) {
			me.scales = ScaleItemMenu.get();
		}
		return me.scales;
	}

	public static ObservableList<ScaleItemMenu> refreshScales() {
		MainCtrl me = getInstance();
		me.scales.forEach(scale -> scale.getConnect().cancel());
		me.scales.clear();
		return getScales();
	}

	public static ObservableList<PackageSend> getPacks() {
		return getInstance().packs;
	}

	public static void addPacks(PackageSend pack) {
		getInstance().packs.add(pack);
	}
}
