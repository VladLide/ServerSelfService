package application.controllers.windows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import application.controllers.MainCtrl;
import application.controllers.parts.ContentCtrl;
import application.models.Configs;
import application.models.Utils;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Templates;
import application.models.objectinfo.ItemContent;
import application.models.objectinfo.NodeTree;
import application.views.languages.uk.parts.ContentInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChooseCtrl {
    private Stage stage;
	private ObservableList<ItemContent> items = null;
    private MySQL db = null;

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "window", "Choose");
    @FXML
    private URL location = getClass().getResource(Utils.getView("window", "Choose"));
    @FXML
    private TableView<ItemContent> dataTable;
    @FXML
    private Button connect;
    @FXML
    private Button close;

    public ChooseCtrl(NodeTree node, ObservableList<ItemContent> items, MySQL db) {
    	this.db = db;
        this.stage = new Stage();
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.initOwner(MainWindowCtrl.getMainStage());
        try {
        	FXMLLoader loader = new FXMLLoader(location,resources);
            loader.setController(this);
            this.stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void show() {
    	this.stage.showAndWait();
    }
    public void close() {
    	this.stage.close();
    }
    
	public void load(NodeTree node) {
		dataTable.getItems().clear();
		dataTable.getColumns().clear();
		ObservableList<Object> items = FXCollections.observableArrayList();
		switch (node.getType()) {
			case "products":{
				if(node.getLevel()==2) {
					if(MainCtrl.getDB().isDBConnection()) {
						items = Goods.getListObj(0,0,"",0,0,MainCtrl.getDB());
					}
				}else {
					ScaleItemMenu scale = (ScaleItemMenu) node.getUpObject().getObject();
					if(scale.getScaleServer().getUpdate()>=0)
						if(scale.getDB().isDBConnection()) {
							items = Goods.getListObj(0,0,"",0,0,scale.getDB());
						}
				}
				break;
			}
			case "sections":{
				if(node.getLevel()==2) {
					if(MainCtrl.getDB().isDBConnection()) {
						items = Sections.getListObj(0,-1,0,"",false,MainCtrl.getDB());
					}
				}else {
					ScaleItemMenu scale = (ScaleItemMenu) node.getUpObject().getObject();
					if(scale.getScaleServer().getUpdate()>=0)
						if(scale.getDB().isDBConnection()) {
							items = Sections.getListObj(0,-1,0,"",false,scale.getDB());
						}
				}
				break;
			}
			case "templates":{
				if(node.getLevel()==2) {
					if(MainCtrl.getDB().isDBConnection()) {
						items = Templates.getListObj(0,"",false,MainCtrl.getDB());
					}
				}else {
					ScaleItemMenu scale = (ScaleItemMenu) node.getUpObject().getObject();
					if(scale.getScaleServer().getUpdate()>=0)
						if(scale.getDB().isDBConnection()) {
							items = Templates.getListObj(0,"",false,scale.getDB());
						}
				}
				break;
			}
			case "templateCodes":{
				if(node.getLevel()==2) {
					if(MainCtrl.getDB().isDBConnection()) {
						items = Codes.getListObj(0,"",MainCtrl.getDB());
					}
				}else {
					ScaleItemMenu scale = (ScaleItemMenu) node.getUpObject().getObject();
					if(scale.getScaleServer().getUpdate()>=0)
						if(scale.getDB().isDBConnection()) {
							items = Codes.getListObj(0,"",scale.getDB());
						}
				}
				
				break;
			}
			case "settings":{
				
				break;
			}
			default:
				break;
		}
		dataTable.getColumns().addAll(ContentCtrl.loadTable(ContentInfo.getInstance().columnsContent.get(node.getType())));
		dataTable.getItems().setAll(ItemContent.get(items));
	}
    
    @FXML
    void initialize() {
        assert dataTable != null : "fx:id=\"dataTable\" was not injected: check your FXML file 'Choose.fxml'.";
        assert close != null : "fx:id=\"close\" was not injected: check your FXML file 'Choose.fxml'.";
        assert connect != null : "fx:id=\"connect\" was not injected: check your FXML file 'Choose.fxml'.";

    }
}
