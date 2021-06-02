package application.controllers;

import application.controllers.windows.MainWindowCtrl;
import application.models.PackageSend;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
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

public class MainCtrl {
    private Timer refresh;
    private MySQL db = null;
    private ObservableList<ScaleItemMenu> scales = FXCollections.observableArrayList();
    private ObservableList<PackageSend> packs = FXCollections.observableArrayList();
    //private FTP ftp = null;
    private static MainCtrl instance = null;
	
	public MainCtrl(/*Stage mainStage*/) {
		super();
		/*this.ftp = new FTP();
		try {
			ftp.open();
			String urlLoad = "/resources/system/";
			FTPFile[] files = ftp.getFtp().listFiles(urlLoad);
			if(files.length>0) {
				for (FTPFile ftpFile : files){
					if(ftpFile.isFile()) {
						int n = ftp.getConfigNETItemInt("id");
						if(ftpFile.getName().compareToIgnoreCase("configNet_"+n+".sys")==0) {
							ftp.downloadFile(urlLoad+ftpFile.getName(), Utils.getPath("sys","configNet.sys"));
						}
						if(ftpFile.getName().compareToIgnoreCase("configButton_"+n+".sys")==0) {
							ftp.downloadFile(urlLoad+ftpFile.getName(), Utils.getPath("sys","configButton.sys"));
						}
						if(ftpFile.getName().compareToIgnoreCase("configImport_"+n+".sys")==0) {
							ftp.downloadFile(urlLoad+ftpFile.getName(), Utils.getPath("sys","configImport.sys"));
						}
						if(ftpFile.getName().compareToIgnoreCase("configSettings_"+n+".sys")==0) {
							ftp.downloadFile(urlLoad+ftpFile.getName(), Utils.getPath("sys","configSettings.sys"));
						}
		    		}
			    }
			}
			ftp.close();
		}catch (Exception e) {
			ftp.addLog("Помилка FTP:"+e.getMessage());
			ftp.writeLog();
		}*/
        /*this.db.DbConnection();
     	this.scale = Scales.get(db.getInt("id"),db);
     	if(this.scale==null) {
     		this.scale = new Scales(db);
     		this.scale.setName(TextBox.options[0][2]+db.getInt("id"));
     	}*/
		//this.mainStage = mainStage;
	}
	public static MainCtrl getInstance() {
		if(instance==null) {
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
    	if(me.refresh!=null)me.refresh.cancel();
    	/*if(this.ftp!=null) {
    		ftp.close();
    		ftp.closeLog();
    	}*/
		System.exit(0);
	}
	public static void startRefresh() {
		MainCtrl me = getInstance();
		me.refresh = new Timer();
		me.refresh.schedule(new TimerTask() {
			@Override
            public void run() {
				if(MainCtrl.getPacks().size()>0) {
					Platform.runLater(()->{
						MainWindowCtrl.getFooterCtrl().startTask(MainCtrl.getPacks().get(0));
						MainCtrl.getPacks().remove(0);
					});
				}
				/*RefreshScale temp = new RefreshScale(inst);
				if((scale.getBoolean("ftp")||scale.getConfigItem("direct_load").length()<1)&&ftp.getFtp().isConnected())
					temp.loadFTP();
				else
					temp.loadFolders();*/
			}
    	},0,1000);
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
		if(me.db==null) {
			me.db = new MySQL(-1);
		}
		me.db.getDBConnection();
		return me.db;
	}
	public static ObservableList<ScaleItemMenu> getScales() {
		MainCtrl me = getInstance();
		if(me.scales.isEmpty()) {
			me.scales=ScaleItemMenu.get();
		}
		return me.scales;
	}
	public static ObservableList<ScaleItemMenu> refreshScales() {
		MainCtrl me = getInstance();
		me.scales.forEach(scale-> scale.getConnect().cancel());
		me.scales.clear();
		return getScales();
	}
	/*public FTP getFtp() {
		return ftp;
	}
	public void setFtp(FTP ftp) {
		this.ftp = ftp;
	}*/
	public static ObservableList<PackageSend> getPacks() {
		return getInstance().packs;
	}
	public static void addPacks(PackageSend pack) {
		getInstance().packs.add(pack);
	}
}
