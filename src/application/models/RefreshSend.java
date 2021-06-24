package application.models;

import application.controllers.MainCtrl;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
//				RefreshSend.updateServer(scales, db);

			}
		}, 0, 10000);
	}

	public static boolean updateServer(/*ObservableList<ScaleInfo> scales*/ MySQL db) {
		List<Distribute> distribute = Distribute.getList(0, 0, null, 0, db);
		if (!distribute.isEmpty()) {
			ObservableList<Object> arr = FXCollections.observableArrayList();
			PackageSend pack = new PackageSend();
			distribute.forEach(itemSend -> {
				TypeTable type = TypeTable.get(itemSend.getId_type_table(), "", "", db);
				Object object = getObj(type, itemSend);
			});
		}
		return false;
	}

	private static Object getObj(TypeTable type, Distribute item) {
		MySQL db = MainCtrl.getDB();
		Integer unique_item = item.getUnique_item();
		if (unique_item == null) return null;
		switch (type.getType()) {
			case "scales": {
				return Scales.get(unique_item, "", db);
			}
			case "sections": {
				return Sections.get(unique_item, 0, -1, "", true, db);
			}
			case "goods": {
				Goods plu = Goods.get(0, unique_item, "", 0, 0, db);
				if (plu != null) {
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
