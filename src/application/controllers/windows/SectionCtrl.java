package application.controllers.windows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.models.Configs;
import application.models.TextBox;
import application.models.Utils;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Sections;
import application.models.objectinfo.NodeTree;
import application.views.languages.uk.windows.SectionInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SectionCtrl {
    private Stage stage;
	private Sections item = null;
	private boolean newItem=true;
	private File file = null;
    private MySQL db = null;

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "window", "Section");
    @FXML
    private URL location = getClass().getResource(Utils.getView("window", "Section"));
    @FXML
    private Label source;
    @FXML
    private TreeTableView<NodeTree> dataTreeTable;
    @FXML
    private AnchorPane img;
    @FXML
    private Button addImg;
    @FXML
    private Button delImg;
    @FXML
    private TextField number;
    @FXML
    private TextField name;
    @FXML
    private ComboBox<String> up;
    @FXML
    private TextField with;
    @FXML
    private TextField to;
    @FXML
    private Button save;
    @FXML
    private Button clear;
    @FXML
    private Button delete;

    public SectionCtrl(MySQL db) {
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
	public ObservableList<TreeTableColumn<NodeTree, ?>> loadDataTable(ObservableList<String[]> colInfo) {
    	ObservableList<TreeTableColumn<NodeTree, ?>> col = FXCollections.observableArrayList();
		colInfo.forEach((v)->{
    		switch(v[0]) {
	    		case "Integer":{
	    			TreeTableColumn<NodeTree, Integer> item = new TreeTableColumn<NodeTree, Integer>(v[1]);
	    			item.setCellValueFactory(new TreeItemPropertyValueFactory<NodeTree, Integer>(v[2]));
	    	    	col.add(item);
	    	    	break;
	    		}
	    		case "String":{
	    			TreeTableColumn<NodeTree, String> item = new TreeTableColumn<NodeTree, String>(v[1]);
	    	    	item.setCellValueFactory(new TreeItemPropertyValueFactory<NodeTree, String>(v[2]));
	    	    	col.add(item);
	    	    	break;
	    		}
    		}
    	});
		return col;
	}
	private void loadImage(AnchorPane imgpanel) {
		try {
    		imgpanel.setBackground(new Background(new BackgroundImage(this.item.getImage(imgpanel), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
						BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
		}catch (Exception e) {
			imgpanel.setBackground(null);
			System.out.println("ButtonWithImage: no image - "+e);
		}
	}
	
	public void loadData() {
		if(dataTreeTable.getRoot()!=null)dataTreeTable.getRoot().getChildren().clear();
		TreeItem<NodeTree> root = new TreeItem<NodeTree>(new NodeTree(SectionInfo.root,"root",-1,"root"));
		root.setExpanded(true);
		Sections.getList(0, 0, 0, "",true, db).forEach(value->{
			TreeItem<NodeTree> main = new TreeItem<NodeTree> (new NodeTree(value.getId(),value.getName(),Utils.getTypeObj(value),value.getLevel(),value,root.getValue()));
			main.setExpanded(true); 
			loadNode(value.getId(),main);
			root.getChildren().add(main);
		});
		dataTreeTable.setRoot(root);
		dataTreeTable.setShowRoot(false);
		//menu.setContextMenu(arg0);
	}
	public void loadNode(int id, TreeItem<NodeTree> upNode) {
		Sections.getList(0, id, 0, "", true, db).forEach(value->{
			TreeItem<NodeTree> node = new TreeItem<NodeTree> (new NodeTree(value.getId(),value.getName(),Utils.getTypeObj(value),value.getLevel(),value,upNode.getValue()));
			node.setExpanded(false); 
			loadNode(value.getId(),node);
			upNode.getChildren().add(node);
		});
	}
    private void continion() {
    	if(name.getText().length()>2){
    		Boolean f = Sections.get(0,-1,0,name.getText(),false,db)==null;
    		if(f||item!=null) {
				try {
					int id = 0;
					try {
						id = Integer.parseInt(number.getText());
						//temp.setId(id);
	    			}catch (Exception e) {
	    				if(item!=null)id = item.getId();
	    			}
					String nameNew = name.getText();
	    			try {
	    				item.setNumber_s(Integer.parseInt(with.getText()));
	    			}catch (Exception e) {}
	    			try {
	    				this.item.setNumber_po(Integer.parseInt(to.getText()));
	    			}catch (Exception e) {}
	    			if(up.getValue()!=null) {
	    				Sections upSec = Sections.get(0,-1,0,up.getValue(),false,db);
	    				if(upSec!=null) {
	    					item.setId_up(upSec.getId());
	    					item.setLevel(upSec.getLevel()+1,db);
	    				}
	    			}
	    			if(file!=null) {
	    				item.setData(file);
	    				item.setDescription(file.getName());
	    			}
		    		if(newItem) {
		    			item.setName(nameNew);
		    			item.setId(id);
		    			if(item.save(db)>0) {
			        		TextBox.alertOpenDialog(AlertType.INFORMATION, "addSectionYes");
			        		if(id!=0)item.updateId(id,db);
			    			load();
			        	}else{
			        		TextBox.alertOpenDialog(AlertType.WARNING, "addSectionNo");
			        	}
		    		}else {
		    			if(item.getId()!=id)this.item.updateId(id,db);
		    			if(item.getName().compareToIgnoreCase(nameNew)!=0)this.item.updateName(nameNew,db);
			    		if(item.save(db)>-1) {
			    			TextBox.alertOpenDialog(AlertType.INFORMATION, "editSectionYes");
			        	}else{
			        		TextBox.alertOpenDialog(AlertType.WARNING, "editSectionNo");
			        	}
		    		}
				}catch( Exception e ) {
					System.out.println(e);
		    	}
    		}else TextBox.alertOpenDialog(AlertType.WARNING, "warningName");
		}else TextBox.alertOpenDialog(AlertType.ERROR, "editSectionNo");
 	    this.load();
    }
    public void clearLoad() {
    	number.setText("");
    	name.setText("");
		with.setText("");
		to.setText("");
		img.setBackground(null);
	    this.item = new Sections();
	    this.file = null;
	    save.setDisable(true);
	    up.setValue(null);
	    newItem = true;
    }
    public void load() {
    	clearLoad();
	    dataTreeTable.getColumns().addAll(loadDataTable(SectionInfo.getColumns("sections")));
	    loadData();
	    up.setItems(Sections.getLName(db));
    }
    
    @FXML
    void initialize() {
    	this.load();
    	save.setOnAction(event->continion());
    	number.textProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	name.textProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	with.textProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	to.textProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	up.getSelectionModel().selectedIndexProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	addImg.setOnAction(event -> {
    		FileChooser fileChooser = new FileChooser();
    		fileChooser.setTitle("Select Image");
    		fileChooser.getExtensionFilters().addAll(
    				new FileChooser.ExtensionFilter("JPG", "*.jpeg","*.jpg"),  
    				new FileChooser.ExtensionFilter("PNG", "*.png"));
    		File file = fileChooser.showOpenDialog(stage);
            if (file == null) {
            	TextBox.alertOpenDialog(AlertType.WARNING, "chooseImageNo");
            }else {
            	this.file = file;
                item.setData(file);
                save.setDisable(false);
                loadImage(img);
            }
    	});
    	delImg.setOnAction(event->{
    		img.setBackground(null);
    		this.item.setImg_data(null);
    		save.setDisable(false);
    	});
    	delete.setOnAction(event ->{
    		int index = dataTreeTable.getSelectionModel().selectedIndexProperty().get();
    		Boolean option = TextBox.alertOpenDialog(AlertType.CONFIRMATION, "deleteSection?", item.getName()).get()==ButtonType.OK;
            if(option!=null) {
            	if(index>-1) {
		    		if(option) {
		    			this.item.setImg_data(null);
		    			if(this.item.delete(true,db)) {
		    				TextBox.alertOpenDialog(AlertType.INFORMATION, "deleteSectionYes");
		    				this.item = null;
		    				this.load();
		    			}else{
		    				TextBox.alertOpenDialog(AlertType.WARNING, "deleteSectionNo");
		    			}
		    		}else {
		    			this.item.setImg_data(null);
		    			if(this.item.delete(false,db)) {
		    				TextBox.alertOpenDialog(AlertType.INFORMATION, "deleteSectionYes");
			        	    this.item = null;
			    			this.load();
		            	}else{
		    				TextBox.alertOpenDialog(AlertType.WARNING, "deleteSectionNo");
		            	}
		    		}
            	}
            }
    	});
    	clear.setOnAction(event ->{
    		clearLoad();
    	});
    	dataTreeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    	    if (newSelection != null) {
				this.item = (Sections)newSelection.getValue().getObject();
	    		number.setText(item.getId()+"");
    	    	name.setText(item.getName());
	    		with.setText(item.getNumber_s()+"");
	    		to.setText(item.getNumber_po()+"");
	    		Sections up = Sections.get(item.getId_up(), -1, 0, "",false, db);
	    		this.up.setValue((up!=null&&item.getId_up()>0)?up.getName():null);
	    		try {
	        		img.setBackground(new Background(new BackgroundImage(this.item.getImage(img), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
	    						BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
	    		}catch (Exception e) {
	    			img.setBackground(null);
	    			System.out.println("ButtonWithImage: no image - "+e);
	    		}
        	    save.setDisable(true);
    	    }
    	});
    }
	public Sections getItem() {
		return item;
	}
	public void setItem(Sections item) {
		this.item = item;
	}
	public void setSource(String source) {
		this.source.setText(source);
	}
}
