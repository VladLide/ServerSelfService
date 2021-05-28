package application.controllers.parts;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import application.controllers.MainCtrl;
import application.controllers.windows.CheckSendCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.models.Configs;
import application.models.PackageSend;
import application.models.Utils;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.interface_tables.ProductItem;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class ContentCtrl {
	private AnchorPane content;
	private NodeTree node = null;
	private PackageSend pack = null;
	
    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"),"part","Content");
    @FXML
    private URL location = getClass().getResource(Utils.getView("part", "Content"));
    @FXML
    private TableView<ItemContent> dataTable;
    @FXML
    private CheckBox check;
    @FXML
    private Button create;
    @FXML
    private Button edit;
    @FXML
    private Button delete;
    @FXML
    private Button send;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem createCM;
    @FXML
    private MenuItem editCM;
    @FXML
    private MenuItem deleteCM;
    @FXML
    private Menu connectCM;
    @FXML
    private MenuItem sectionsCM;
    @FXML
    private MenuItem templateCodesCM;
    @FXML
    private MenuItem templatesCM;
    @FXML
    private MenuItem stocksCM;

	public ContentCtrl(AnchorPane anchorPane) {
		try {
            FXMLLoader loader = new FXMLLoader(location,resources);
            loader.setController(this);
            content = MainCtrl.loadAnchorPane(anchorPane, loader);
        } catch (IOException e) {
        	content = null;
            MainWindowCtrl.setLog(e.getMessage());
        }
	}
	public void loadContent(AnchorPane anchorPane) {
		if(content!=null) {
			anchorPane.getChildren().add(content);
		}else {
			MainWindowCtrl.setLog(this.getClass().getName()+": error null fxml");
		}
	}
	public void showTableRedactorData(NodeTree node) {
		this.node = node;
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
		dataTable.getColumns().addAll(loadTable(ContentInfo.getInstance().columnsContent.get(node.getType())));
		dataTable.getItems().setAll(ItemContent.get(items));
	}
	public void operationsData(ItemContent item, Boolean del) {
		MainWindowCtrl mw = MainWindowCtrl.getInstance();
		MySQL db = null;
		String source = "";
		if(node.getLevel()==2) {
			db = MainCtrl.getDB();
			source = "Server (localhost)";
		}else {
			ScaleItemMenu sim = (ScaleItemMenu)node.getUpObject().getObject();
			db = sim.getDB();
			source = sim.getName()+" - "+sim.getId()+" ("+sim.getScaleServer().getIp_address()+")";
		}
		switch (node.getType()) {
			case "products":{
				if(del!=null&&item!=null){
					if(del)
						((Goods)item.getObject()).delete(db);
				}else {
					mw.openPlu((item!=null)?(Goods)item.getObject():null, source, db);
				}
				break;
			}
			case "sections":{
				if(del!=null&&item!=null){
					if(del)
						((Sections)item.getObject()).delete(false,db);
				}else {
					mw.openSection((item!=null)?(Sections)item.getObject():null, source, db);
				}
				break;
			}
			case "templates":{
				if(del!=null&&item!=null){
					if(del)
						((Templates)item.getObject()).delete(db);
				}else {
					mw.openTemplate((item!=null)?(Templates)item.getObject():null, db);
				}
				break;
			}
			case "templateCodes":{
				if(del!=null&&item!=null){
					if(del)
						((Codes)item.getObject()).delete(db);
				}else {
					mw.openCode((item!=null)?(Codes)item.getObject():null, source, db);
				}
				break;
			}
			case "settings":{
				
				break;
			}
			default:
				break;
		}
		dataTable.refresh();
	}
	public static ObservableList<TableColumn<ItemContent, ?>> loadTable(ObservableList<String[]> colInfo) {
    	ObservableList<TableColumn<ItemContent, ?>> col = FXCollections.observableArrayList();
    	try {
			colInfo.forEach((v)->{
	    		switch(v[0]) {
		    		case "Integer":{
		    			TableColumn<ItemContent, Integer> item = new TableColumn<ItemContent, Integer>(v[1]);
		    			item.setCellValueFactory(new PropertyValueFactory<ItemContent, Integer>(v[2]));
		    	    	col.add(item);
		    	    	break;
		    		}
		    		case "String":{
		    			TableColumn<ItemContent, String> item = new TableColumn<ItemContent, String>(v[1]);
		    	    	item.setCellValueFactory(new PropertyValueFactory<ItemContent, String>(v[2]));
		    	    	col.add(item);
		    	    	break;
		    		}
		    		case "Float":{
		    			TableColumn<ItemContent, Float> item = new TableColumn<ItemContent, Float>(v[1]);
		    			item.setCellValueFactory(new PropertyValueFactory<ItemContent, Float>(v[2]));
		    	    	col.add(item);
		    	    	break;
		    		}
		    		case "CheckBox":{
		    			TableColumn<ItemContent, CheckBox> item = new TableColumn<ItemContent, CheckBox>(v[1]);
		    			item.setCellValueFactory(new PropertyValueFactory<ItemContent, CheckBox>(v[2]));
		    	    	col.add(item);
		    	    	break;
		    		}
		    		case "LocalDateTime":{
		    			TableColumn<ItemContent, LocalDateTime> item = new TableColumn<ItemContent, LocalDateTime>(v[1]);
		    			item.setCellValueFactory(new PropertyValueFactory<ItemContent, LocalDateTime>(v[2]));
		    	    	col.add(item);
		    	    	break;
		    		}
		    		case "AnchorPane":{
		    			TableColumn<ItemContent, AnchorPane> item = new TableColumn<ItemContent, AnchorPane>(v[1]);
		    			item.setCellValueFactory(new PropertyValueFactory<ItemContent, AnchorPane>(v[2]));
		    	    	col.add(item);
		    	    	break;
		    		}
	    		}
	    	});
    	}catch (Exception e) {
			System.out.println("not find type");
		}
		return col;
	}
	private void addSend() {
		pack = new PackageSend();
		CheckSendCtrl check = new CheckSendCtrl();
		check.show();
		if(pack.getConnectSend().size()>0) {
			ObservableList<Object> arr = FXCollections.observableArrayList();
			dataTable.getItems().forEach((v)->{
				if(v.getCheckBox().isSelected()) {
					if(pack.getType().length()<1)pack.setType(v.getTypeOdject());
					arr.add(v.getObject());
				}
			});
			if(arr.size()>0) {
				pack.setItems(arr);
				MainCtrl.addPacks(pack);
			}
		}
	}
    @FXML
    void initialize() {
        assert create != null : "fx:id=\"create\" was not injected: check your FXML file 'Content.fxml'.";
        assert check != null : "fx:id=\"check\" was not injected: check your FXML file 'Content.fxml'.";
        assert dataTable != null : "fx:id=\"viewTreeTable\" was not injected: check your FXML file 'Content.fxml'.";
        assert delete != null : "fx:id=\"delete\" was not injected: check your FXML file 'Content.fxml'.";
        dataTable.setOnContextMenuRequested(event->{
        	ItemContent item = dataTable.getSelectionModel().getSelectedItem();
        	if(item!=null) {
    			editCM.setDisable(false);
    			deleteCM.setDisable(false);
    			connectCM.setDisable(false);
        	}else {
    			editCM.setDisable(true);
    			deleteCM.setDisable(true);
    			connectCM.setDisable(true);
        	}
        });
        send.setOnAction(event->{
        	//MainWindowCtrl.getFooterCtrl().startTask(event);
        	addSend();
        });
        check.setOnAction(event->{
        	dataTable.getItems().forEach((v)->{
        		v.setCheckBox(check.isSelected());
        	});
        });
        create.setOnAction(event->{
        	operationsData(null,null);
        });
        edit.setOnAction(event->{
        	ItemContent item = dataTable.getSelectionModel().getSelectedItem();
        	operationsData(item,null);
        });
        delete.setOnAction(event->{
        	ItemContent item = dataTable.getSelectionModel().getSelectedItem();
        	operationsData(item,true);
        	dataTable.getItems().remove(item);
        	dataTable.refresh();
        });
        createCM.setOnAction(event->{
        	operationsData(null,null);
        });
        editCM.setOnAction(event->{
        	ItemContent item = dataTable.getSelectionModel().getSelectedItem();
        	operationsData(item,null);
        });
        deleteCM.setOnAction(event->{
        	ItemContent item = dataTable.getSelectionModel().getSelectedItem();
        	operationsData(item,true);
        	dataTable.getItems().remove(item);
        	dataTable.refresh();
        });
        sectionsCM.setOnAction(event->{
        	
        });
        templateCodesCM.setOnAction(event->{
        	
        });
        templatesCM.setOnAction(event->{
        	
        });
        stocksCM.setOnAction(event->{
        	
        });
    }
	public Button getSend() {
		return send;
	}
	public PackageSend getPack() {
		return pack;
	}
}
