package application.models;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Scales;
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
    	/*this.refreshTimer.schedule(new TimerTask() {
			@Override
            public void run() {
				RefreshSend.updateServer(scales,db);
				scales.forEach(scale->{
					try {
						scale.getScale().getDb().getDBConnection().close();
						scale.getScale().getDb().dbConnection = null;
					} catch (NullPointerException e) {
						System.out.println(scale.getScale().getId()+" name: "+scale.getScale().getName()+" addres: "+scale.getScale().getIp_address()+" - "+e.getMessage());
					} catch (SQLException e) {
						System.out.println(scale.getScale().getId()+" name: "+scale.getScale().getName()+" addres: "+scale.getScale().getIp_address()+" - "+e.getMessage());
					}
				});
				scales = ScaleInfo.get(admin);
				scales.forEach(scale->{
					scale.connect();
				});
				ObservableList<ScaleInfo> scaleInfo = ScaleInfo.get(admin);
				if(scales.size()!=0) {
					ObservableList<ScaleInfo> scaleInfoTMP = scales;
					for(int i = 0 ; i<scaleInfo.size();i++) {
						scaleInfo.get(i).getCheckBox().setSelected(scaleInfoTMP.get(i).getCheckBox().isSelected());
					}
				}
				scales = scaleInfo;
				Platform.runLater(()-> {
					admin.loadInfoDBScales();
				});
			}
    	},0,10000);*/
	}
	/*public static boolean updateServer(ObservableList<ScaleInfo> scales, MySQL db) {
		ObservableList<Goods> plu = Goods.getGoodsLoad(db);
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
				}/
				if(id>0)value.del(curId,db);
			});
			return true;
		}else {
			return false;
		}
	}
	public static Scales get(ObservableList<ScaleInfo> scales, int id) {
		for(ScaleInfo value : scales) {
			if(value.getId()==id) {
				return value.getScale();
			}
		}
		return null;
	}*/
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
