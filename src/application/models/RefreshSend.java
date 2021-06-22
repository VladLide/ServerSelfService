package application.models;

import java.security.AccessControlContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import application.controllers.MainCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.*
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RefreshSend {
	private Timer refreshTimer = null;
	//private volatile ObservableList<ScaleItemMenu> scales = FXCollections.observableArrayList();

	public RefreshSend() {
		super();
		this.refreshTimer = new Timer();
	}

	public void start() {
		if (this.refreshTimer == null) {
			this.refreshTimer = new Timer();
		}
    	this.refreshTimer.schedule(new TimerTask() {
			@Override
            public void run() {
				RefreshSend.updateServer(scales,db);
				
			}
    	},0,10000);
	}
	public static boolean updateServer(ObservableList<ScaleInfo> scales, MySQL db) {
		ObservableList<Distribute> distribute = Distribute.getList(0, 0, null, 0, db);
		if (!distribute.isEmpty()) {
			ObservableList<Object> arr = FXCollections.observableArrayList();
			PackageSend pack = new PackageSend();
			distribute.forEach(itemSend -> {
				TypeTable type = TypeTable.get(itemSend.getId_type_table(), "", "", db);
				Object object = getObj(type, itemSend); 
				if(itemSend.isBatch()) {
					if(itemSend.getUnique_item()>0) {
						
					} else {
						
					}
				} else {
					if(object != null) {
						arr.add(object);
					} else {
						
					}
				}
				if (pack.getType().length() < 1) {
					if(type==null) {
						MainWindowCtrl.setLog();
						itemSend.delete(MainCtrl.getDB());
					}
					pack.setType();
				} else {
					
				}
				case 1if (item.isSelected()) {
					arr.add(item.getObject());
				}
				}
			});
			if (!arr.isEmpty()) {
				pack.setItems(arr);
				MainCtrl.addPacks(pack);
			}
		}
		MainCtrl.addPacks(pack);
		MainWindowCtrl.getContentCtrl().getPack().setConnectSend(scales);
		if(plu.size()!=0) {
			plu.forEach(value->{
				int curId = value.getId();
				Scales scale = RefreshSend.get(scales,value.getId_scales());
				value.setId(0);
				int id = 0;
				if(scale!=null)if(scale.getDb().dbConnection!=null) {
					id = value.save(scale.getDb());
					ZonedDateTime timetmp = ZonedDateTime.now();
					scale.setDate_update(LocalDateTime.parse(timetmp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+ "T" +timetmp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
					scale.save(db);
				}
				value.save(db);
				/*if(scale!=null) {
					if(scale.getUpdate()==2) {
						scale.setUpdate(0);
						scale.save();
						new ScalesServer(scale,db).save();
					}
				}*/
				if(id>0)value.del(curId,db);
			});
			return true;
		}else {
			return false;
		}
	}
	
	private String command(int command) {
		switch (command) {
			case 1: {
				Goods goods = new Goods();
				goods.init();
				return goods.getId() + " - " + goods.getName();
			}
			case 2: {
				Goods.getList(0, 0, "", 0, 0, db);
				return tmp.getId() + " - " + tmp.getName();
			}
			default:
				return "";
		}
	}
	
	public static Scales get(ObservableList<ScaleInfo> scales, int id) {
		for(ScaleInfo value : scales) {
			if(value.getId()==id) {
				return value.getScale();
			}
		}
		return null;
	}
	

	private static Object getObj(TypeTable type, Distribute item) {
		MySQL db = MainCtrl.getDB();
		Integer unique_item = item.getUnique_item();
		if(unique_item == null) return null;
		switch (type.getType()) {
		case "scales": {
			return Scales.get(unique_item, "", db);
		}
		case "sections": {
			return Sections.get(unique_item, 0, -1, "", true, db);
		}
		case "goods": {
			Goods plu = Goods.get(0, unique_item, "", 0, 0, db);
			if (plu!=null) {
				plu.setId_barcodes(item.getId_barcodes());
				plu.setId_templates(item.getId_templates());
				plu.setPrice(item.getPrice());
			}
			return plu;
		}
		case "templates": {
			return Templates.get(unique_item, "", true, db);
		}
		case "codes": {
			return Codes.get(unique_item, "", db);
		}
		case "users": {
			return Users.get(unique_item, "", "", "", "", db);
		}
		case "access": {
			return null;
		}
		case "stocks": {
			return Stocks.get(unique_item, db);
		}
		case "objects_tara": {
			return ObjectsTara.get(0, unique_item, "", db);
		}
		case "bots_telegram": {
			return BotsTelegram.get(unique_item, "", "", "", -1, db);
		}
		case "users_telegram": {
			return UsersTelegram.get(unique_item, "", "", "", db);
		}
			default:
				return null;
		}
	}

	public void close() {
		this.refreshTimer.cancel();
	}

	public Timer getRefreshTimer() {
		return refreshTimer;
	}

	public void setRefreshTimer(Timer refreshTimer) {
		this.refreshTimer = refreshTimer;
	}
	/*public ObservableList<ScaleInfo> getScaleInfo() {
		return scales;
	}
	public void setScales(ObservableList<ScaleInfo> scales) {
		this.scales = scales;
	}
	public ObservableList<Scales> getScales() {
		ObservableList<Scales> scalesTemp = FXCollections.observableArrayList();
		for(ScaleInfo value : scales) {
			scalesTemp.add(value.getScale());
		}
		return scalesTemp;
	}*/
}
