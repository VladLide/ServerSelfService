package application.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import application.controllers.pages.Admin;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Scales;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class ScaleInfo {
	private Admin admin = null;
	private CheckBox checkBox = new CheckBox();
	private int id = 0;
	private String name = "";
	private LocalDateTime  date_update = LocalDateTime.now();
	private AnchorPane status = new AnchorPane();
	private Scales scale = null;
	
	public ScaleInfo(Scales scale, Admin admin) {
		super();
		this.admin = admin;
		this.id = scale.getId();
		this.name = scale.getName();
		this.status(scale.getUpdate());
		this.scale = scale;
		this.init();
		this.date_update = scale.getDate_update();
		this.status.setPrefSize(10, 10);
		this.status.setOnMouseEntered(event->this.admin.getStatus().setText(this.scale.getId()+" - "+TextBox.getStatus(this.scale.getUpdate())));
	}

	public static ObservableList<ScaleInfo> get(Admin admin) {
    	ResultSet resul = admin.getDb().getSelect(Scales.getSql(0,""));
    	ObservableList<ScaleInfo> row = FXCollections.observableArrayList();
    	try {
    		//int index = 0;
    		while(resul.next()) {
    			ScaleInfo item = new ScaleInfo(new Scales(resul),admin);
				if(item.scale.getId() != 0)
					row.add(item);
			}
		} catch (NullPointerException e) {
			System.out.println("ScaleInfo: error db " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("ScaleInfo: error db " + e.getMessage());
		}
		return row;
	}
	private void status(int update) {
		switch(update) {
			case -2:{
				this.status.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			}
			case -1:{
				this.status.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			}
			case 0:{
				this.status.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			}
			case 1:{
				this.status.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			}
			case 2:{
				this.status.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			}
			default:{
				this.status.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
			}
		}
	}
	public void connect() {
		Scales me = this.scale;
		try {
			if(!me.getIp_address().matches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})")||me.getIp_address().matches("(localhost)|(127\\.0\\.0\\.1)")||me.getIp_address().length()<7) {
				me.setUpdate(-2);
			}else {
				MySQL db = new MySQL(me.getIp_address(),1);
				if(db.getDBConnection()==null) {
					me.setUpdate(-1);
				}else {
					ResultSet resul = db.getSelect(ScaleInfo.getSql());
			    	try {
						while(resul.next()) {
							int status = resul.getInt(resul.findColumn("update"));
							int n = resul.getInt(resul.findColumn("cout"));
							if(status==0) {
								if(n==0)
									me.setUpdate(2);
								else
									me.setUpdate(0);
							}else {
								if(status==1)
									me.setUpdate(1);
								else
									if(n>0)me.setUpdate(0);
									else me.setUpdate(2);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
			    	db.dbConnection.close();
			    	db.dbConnection=null;
				}
			}
			this.status(me.getUpdate());
			this.scale = me;
			if(admin.getDb().getDBConnection()!=null)me.save(admin.getDb());
			if(me.getDb().getDBConnection()!=null)me.save(me.getDb());
		}catch (Exception e) {
			System.out.println("ScaleInfo: error " + e.getMessage());
		}
	}
	
	public void init() {
		this.checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.checkBox.setSelected(newValue);
            System.out.println("CheckBox для " + this + " изменен с " + oldValue + " в " + newValue);
        });
	}
	public static String getSql() {
		String sql = "SELECT scales.update, count(goods.id) as cout FROM scales" + 
				"	Left join goods on goods.id_scales = scales.id;";
		return sql;
	}
	public CheckBox getCheckBox() {
		return checkBox;
	}
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
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
	public AnchorPane getStatus() {
		return status;
	}
	public void setStatus(AnchorPane status) {
		this.status = status;
	}
	public Scales getScale() {
		return scale;
	}
	public void setScale(Scales scale) {
		this.scale = scale;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
