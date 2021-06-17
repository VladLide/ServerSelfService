package application.models.net.mysql.interface_tables;

import application.enums.ScaleStatus;
import application.controllers.MainCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Scales;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScaleItemMenu {
	private int id = 0;
	private String name = "";
	private LocalDateTime date_update = LocalDateTime.now();
	private ImageView img = new ImageView();
	private Scales scale = null;
	private Timer connect = new Timer();
	private MySQL db = null;
	private AtomicBoolean saveToConnect = new AtomicBoolean(true);
	private ScaleStatus status;
	private static Logger logger = LogManager.getLogger(ScaleItemMenu.class);

	public ScaleItemMenu() {
		super();
		this.name = "Server";
	}

	public ScaleItemMenu(Scales scale) {
		super();
		this.id = scale.getId();
		this.name = scale.getName();
		this.scale = scale;
		img.setFitWidth(30);
		img.setFitHeight(15);
		img.setImage(status(scale.getUpdate(), 0));
		this.date_update = scale.getDate_update();
		connect();
	}

	public static ObservableList<ScaleItemMenu> get() {
		ResultSet result = MainCtrl.getDB().getSelect(Scales.getSql(0, ""));
		ObservableList<ScaleItemMenu> row = FXCollections.observableArrayList();
		try {
			while (result.next()) {
				ScaleItemMenu item = new ScaleItemMenu(new Scales(result));
				row.add(item);
			}
		} catch (NullPointerException | SQLException e) {
			logger.error("ScaleInfo: error db {}", e.getMessage(), e);
		}
		return row;
	}

	private Image status(int statusServer, int statusScale) {
		Color color = null;
		scale.setUpdate((statusServer == 0 && statusScale == 2) ? statusScale : statusServer);
		switch (scale.getUpdate()) {
		case -3: {
			color = Color.BLACK; // Не вірно введений адрес вагів
				this.status = ScaleStatus.INCORRECT_SCALE_ADDRESS;
			break;
		}
		case -2: {
			color = Color.RED;// Не має зв'язку
				this.status = ScaleStatus.NO_CONNECTION;
			break;
		}
		case -1: {
			color = Color.YELLOW;// звязь была потеряна не успев обновиться
				this.status = ScaleStatus.CONNECTION_WAS_LOST_WITHOUT_UPDATE;
			break;
		}
		case 0: {
			color = Color.GREEN;// товары на бд актуальны на
				this.status = ScaleStatus.PRODUCTS_AT_DATABASE_ARE_UP_TO_DATE;
			break;
		}
		case 1: {
			color = Color.GREENYELLOW;// обновляются не отключайте
				this.status = ScaleStatus.UPDATING_DONT_SHUTDOWN;
			break;
		}
		case 2: {
			color = Color.BLUE;// товаров на весах нет
				this.status = ScaleStatus.NO_PRODUCTS_AT_SCALES;
			break;
		}
		default: {
			color = Color.BLACK;
		}
		}
		return (new Rectangle(img.getFitWidth(), img.getFitHeight(), color)).snapshot(null, null);
	}

	public void connect() {
		if (!scale.getIp_address().matches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})")
				|| scale.getIp_address().matches("(localhost)|(127\\.0\\.0\\.1)")
				|| scale.getIp_address().length() < 7) {
			scale.setUpdate(-3);
			img.setImage(status(scale.getUpdate(), 0));
		} else {
			scale.setUpdate(-2);
		}
		if (scale.getUpdate() != -3) {
			connect.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						if (isConnectionDB()) {
							if (Goods.getCountGoodsLoad(scale.getId(), MainCtrl.getDB()) > 0) {
								scale.setUpdate(1);
							} else {
								if (Goods.getCountGoods(0, getDB()) < 1) {
									scale.setUpdate(2);
								} else {
									scale.setUpdate(0);
								}
							}
						} else {
							if (Goods.getCountGoodsLoad(scale.getId(), MainCtrl.getDB()) > 0) {
								scale.setUpdate(-1);
							} else {
								scale.setUpdate(-2);
							}
						}
					} catch (SQLException e) {
						System.out.println("ScaleInfo: error " + e.getMessage());
						scale.setUpdate(-2);
					} finally {
						Platform.runLater(() -> {
							img.setImage(status(scale.getUpdate(), /* getScale().getUpdate() */0));
						});
						if (saveToConnect.get())
							scale.save(MainCtrl.getDB());
					}
					if (!MainWindowCtrl.existInstance())
						connect.cancel();
				}
			}, 0, 60000);
		}
	}

	public static String getSql() {
		String sql = "SELECT scales.update, count(goods.id) as cout FROM scales"
				+ "	Left join goods on goods.id_scales = scales.id;";
		return sql;
	}

	public void setScale(Scales scale) {
		this.id = scale.getId();
		this.name = scale.getName();
		this.scale = scale;
		img.setImage(status(scale.getUpdate(), 0));
		this.date_update = scale.getDate_update();
		getDB().setConfig(scale.getId());
		saveToConnect.set(true);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate_update() {
		return date_update.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
	}

	public void setDate_update(LocalDateTime date_update) {
		this.date_update = date_update;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MySQL getDB() {
		if (db == null) {
			this.db = new MySQL(scale.getId());
		}
		return db;
	}

	public void setDb(MySQL db) {
		this.db = db;
	}

	public boolean existConnectionDB() {
		return getDB().getDBConnection() != null;
	}

	public boolean isConnectionDB() throws SQLException {
		return (existConnectionDB()) ? db.isDBConnection() : false;
	}

	public ImageView getImg() {
		return img;
	}

	public Timer getConnect() {
		return connect;
	}

	public Scales getScale() {
		return scale;
	}

	public void setSaveToConnect(boolean value) {
		saveToConnect.set(value);
	}

	public ScaleStatus getStatus() {
		return status;
	}

	public void setStatus(ScaleStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ScaleItemMenu that = (ScaleItemMenu) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
