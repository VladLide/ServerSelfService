package application.models.net.mysql.interface_tables;

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
	private LocalDateTime  date_update = LocalDateTime.now();
	private ImageView img = new ImageView();
	private Scales scaleServer = null;
	private Scales scale = null;
	private Timer connect = new Timer();
	private MySQL db = null;
	private AtomicBoolean saveToConnect = new AtomicBoolean(true);
	private static Logger logger = LogManager.getLogger(ScaleItemMenu.class);

	public ScaleItemMenu() {
		super();
		this.name = "Server";
	}
	public ScaleItemMenu(Scales scale) {
		super();
		this.id = scale.getId();
		this.name = scale.getName();
		this.scaleServer = scale;
		img.setFitWidth(30);
		img.setFitHeight(15);
		img.setImage(status(scale.getUpdate(),0));
		this.date_update = scale.getDate_update();
		connect();
	}

	public static ObservableList<ScaleItemMenu> get() {
    	ResultSet result = MainCtrl.getDB().getSelect(Scales.getSql(0,""));
    	ObservableList<ScaleItemMenu> row = FXCollections.observableArrayList();
    	try {
    		while(result.next()) {
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
		scaleServer.setUpdate((statusServer==0&&statusScale==2)?statusScale:statusServer);
		switch(scaleServer.getUpdate()) {
			case -3:{
				color = Color.BLACK; //Не вірно введений адрес вагів
				break;
			}
			case -2:{
				color = Color.RED;//Не має зв'язку
				break;
			}
			case -1:{
				color = Color.YELLOW;//звязь была потеряна не успев обновиться
				break;
			}
			case 0:{
				color = Color.GREEN;//товары на бд актуальны на
				break;
			}
			case 1:{
				color = Color.GREENYELLOW;//обновляются не отключайте
				break;
			}
			case 2:{
				color = Color.BLUE;//товаров на весах нет
				break;
			}
			default:{
				color = Color.BLACK;
			}
		}
		return (new Rectangle(img.getFitWidth(),img.getFitHeight(),color)).snapshot(null, null);
	}
	public void connect() {
		if(!scaleServer.getIp_address().matches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})")||scaleServer.getIp_address().matches("(localhost)|(127\\.0\\.0\\.1)")||scaleServer.getIp_address().length()<7) {
			scaleServer.setUpdate(-3);
			img.setImage(status(scaleServer.getUpdate(),0));
		}else {
			scaleServer.setUpdate(-2);
		}
		if(scaleServer.getUpdate()!=-3) {
			connect.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						if(isConnectionDB()) {
							if(Goods.getCountGoodsLoad(scaleServer.getId(),MainCtrl.getDB())>0) {
								scaleServer.setUpdate(1);
							}else {
								if(Goods.getCountGoods(scaleServer.getId(),getDB())<1){
									scaleServer.setUpdate(2);
								}else {
									scaleServer.setUpdate(0);
								}
							}
						}else {
							if(Goods.getCountGoodsLoad(scaleServer.getId(),MainCtrl.getDB() )>0) {
								scaleServer.setUpdate(-1);
							}else {
								scaleServer.setUpdate(-2);
							}
						}
					}catch (SQLException e) {
						System.out.println("ScaleInfo: error " + e.getMessage());
						scaleServer.setUpdate(-2);
					}finally {
						Platform.runLater(()-> {
							img.setImage(status(scaleServer.getUpdate(),/*getScale().getUpdate()*/0));
						});
							if(saveToConnect.get())scaleServer.save(MainCtrl.getDB());
					}
					if(!MainWindowCtrl.existInstance())connect.cancel();
				}
			}, 0, 60000);
		}
	}
	public static String getSql() {
		String sql = "SELECT scales.update, count(goods.id) as cout FROM scales" + 
				"	Left join goods on goods.id_scales = scales.id;";
		return sql;
	}

	public void setScaleServer(Scales scale) {
		this.id = scale.getId();
		this.name = scale.getName();
		this.scaleServer = scale;
		img.setImage(status(scale.getUpdate(),0));
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
	public Scales getScale() {
		if(scale==null) {
			this.scale = Scales.get(scaleServer.getId(), "", db);
		}
		return this.scale;
	}
	public void updataScale() {
		this.scale = Scales.get(scaleServer.getId(), "", db);
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public MySQL getDB() {
		if(db==null) {
			this.db = new MySQL(scaleServer.getId());
		}
		return db;
	}
	
	public void setDb(MySQL db) {
		this.db = db;
	}
	
	public boolean existConnectionDB() {
		return getDB().getDBConnection()!=null;
	}
	public boolean isConnectionDB() throws SQLException {
		return (existConnectionDB())?getDB().getDBConnection().isValid(5):false;
	}
	public ImageView getImg() {
		return img;
	}
	public Timer getConnect() {
		return connect;
	}
	public Scales getScaleServer() {
		return scaleServer;
	}
	public void setSaveToConnect(boolean value) {
		saveToConnect.set(value);
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
