package application.controllers.windows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.PlaceType;
import application.controllers.MainCtrl;
import application.controllers.parts.ContentCtrl;
import application.controllers.parts.FooterCtrl;
import application.controllers.parts.LogCtrl;
import application.controllers.parts.SidebarCtrl;
import application.models.Configs;
import application.models.Utils;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Scales;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Templates;
import application.models.objectinfo.ItemContent;
import application.models.objectinfo.NodeTree;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainWindowCtrl {
    private Stage mainStage = null;
    private static MainWindowCtrl mainWindow = null;
    private SidebarCtrl sidebarCtrl = null;
    private ContentCtrl contentCtrl = null;
    private FooterCtrl footerCtrl = null;
    private LogCtrl logCtrl = null;

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"),"window","MainWindow");
    @FXML
    private URL location = getClass().getResource(Utils.getView("window", "MainWindow"));
    @FXML
    private BorderPane main;
    @FXML
    private AnchorPane center;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private AnchorPane sidebar;
    @FXML
    private SplitPane centerSplitPane;
    @FXML
    private AnchorPane content;
    @FXML
    private AnchorPane logPane;
    @FXML
    private AnchorPane footer;
    @FXML
    private Label url;

    public MainWindowCtrl() {}
    public static void loadView(Stage stage) {
		MainWindowCtrl mw = getInstance();
		mw.mainStage = stage;
    	try {
            FXMLLoader loader = new FXMLLoader(mw.location,mw.resources);
            loader.setController(mw);
            mw.mainStage.setScene(new Scene(loader.load()));
            mw.mainStage.setTitle("Server SelfService");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public static MainWindowCtrl getInstance() {
		if(mainWindow==null) {
			mainWindow = new MainWindowCtrl();
		}
		return mainWindow;
	}
	public static boolean existInstance() {
		return (mainWindow==null)?false:true;
	}
	public static void showStage() {
		getInstance().mainStage.show();
		openMain();
    }
	public static void close() {
		getInstance().mainStage.close();
		mainWindow = null;
		MainCtrl.close();
	}
	
	public static void openMain() {
		MainWindowCtrl mw = getInstance();
		if(mw.logCtrl==null) {
			mw.logCtrl = new LogCtrl(mw.logPane);
		}else {
			mw.logCtrl.loadLog(mw.logPane);
		}
		if(mw.sidebarCtrl==null) {
			mw.sidebarCtrl = new SidebarCtrl(mw.sidebar);
		}else {
			mw.sidebarCtrl.loadSidebar(mw.sidebar);
		}
		if(mw.contentCtrl==null) {
			mw.contentCtrl = new ContentCtrl(mw.content);
		}else {
			mw.contentCtrl.loadContent(mw.content);
		}
		if(mw.footerCtrl==null) {
			mw.footerCtrl = new FooterCtrl(mw.footer);
		}else {
			mw.footerCtrl.loadFooter(mw.footer);
		}
	}
	public TemplateCtrl openTemplate(Templates item,
	                                 MySQL db,
	                                 String ipAddress,
	                                 PlaceType placeType) {
		TemplateCtrl template = new TemplateCtrl(item, db);
		template.setIpAddress(ipAddress);
		template.setPlaceType(placeType);
		template.show();
		return template;
	}
	public ProductCtrl openPlu(Goods item,
	                           String source,
	                           MySQL db,
	                           String ipAddress,
	                           PlaceType placeType) {
		ProductCtrl plu = new ProductCtrl(db);
		plu.setItem(item);
		plu.setSource(source);
		plu.setIpAddress(ipAddress);
		plu.setPlaceType(placeType);
		plu.show();
		return plu;
	}
	public CodeCtrl openCode(Codes item,
	                         String source,
	                         MySQL db,
	                         String ipAddress,
	                         PlaceType placeType) {
		CodeCtrl code = new CodeCtrl(db);
		code.setItem(item);
		code.setSource(source);
		code.setIpAddress(ipAddress);
		code.setPlaceType(placeType);
		code.show();
		return code;
	}
	public SectionCtrl openSection(Sections item,
	                               String source,
	                               MySQL db,
	                               String ipAddress,
	                               PlaceType placeType) {
		SectionCtrl section = new SectionCtrl(db);
		section.setItem(item);
		section.setSource(source);
		section.setIpAddress(ipAddress);
		section.setPlaceType(placeType);
		section.show();
		return section;
	}
	
    @FXML
    void initialize() {
    	getInstance().mainStage.setOnCloseRequest(event->{
    		close();
		});
    }
	public static void updateSidebar(Scales scale, boolean newItem) {
		MainWindowCtrl mw = getInstance();
		if(newItem)
			mw.sidebarCtrl.addItemMenu(new ScaleItemMenu(scale));
		else mw.sidebarCtrl.updateItemMenu(scale);
	}
	public static String getLog() {
		return getInstance().logCtrl.getLog();
	}
	public static void setLog(String line) {
		getInstance().logCtrl.setLog(line);
	}
	public static Stage getMainStage() {
		return getInstance().mainStage;
	}
	public static void setURL(String line) {
		getInstance().url.setText(line);
	}
	public static SidebarCtrl getSidebarCtrl() {
		return getInstance().sidebarCtrl;
	}
	public static ContentCtrl getContentCtrl() {
		return getInstance().contentCtrl;
	}
	public static FooterCtrl getFooterCtrl() {
		return getInstance().footerCtrl;
	}

	public static double getHeightOfCenterSplitPane() {
    	return getInstance().centerSplitPane.getHeight();
	}

	public static void updateHeightLogPane(double height) {
		Platform.runLater(() -> {
			MainWindowCtrl instance = getInstance();
			AnchorPane logPane = instance.logPane;
			logPane.setMaxHeight(height);
			logPane.setMinHeight(height);
		});
	}
}
