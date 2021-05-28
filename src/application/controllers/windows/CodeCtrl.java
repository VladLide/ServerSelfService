package application.controllers.windows;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import application.models.Configs;
import application.models.TextBox;
import application.models.Utils;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Codes;
import application.models.objectinfo.ItemTemplate;
import application.views.languages.uk.parts.ContentInfo;
import application.views.languages.uk.windows.CodeInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CodeCtrl {
    private Stage stage;
	private Codes item = null;
    private ObservableList<Codes> barcodes;
    private ObservableList<String> barcodesName = FXCollections.observableArrayList();
    private MySQL db = null;

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "window", "Code");
    @FXML
    private URL location = getClass().getResource(Utils.getView("window", "Code"));
    @FXML
    private TableView<Codes> dataTable;
    @FXML
    private TableView<ItemTemplate> itemsTable;
    @FXML
    private ComboBox<?> typeCode;
    @FXML
    private TextField number;
    @FXML
    private TextField name;
    @FXML
    private TextField prefixValue;
    @FXML
    private TextField mask;
    @FXML
    private Label source;
    @FXML
    private Button deleteField;
	@FXML
    private Button clearField;
    @FXML
    private Button save;
    @FXML
    private Button delete;
    @FXML
    private Button clear;

    public CodeCtrl(MySQL db) {
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
	public ObservableList<TableColumn<Codes, ?>> loadDataTable(ObservableList<String[]> colInfo) {
    	ObservableList<TableColumn<Codes, ?>> col = FXCollections.observableArrayList();
		colInfo.forEach((v)->{
    		switch(v[0]) {
	    		case "Integer":{
	    			TableColumn<Codes, Integer> item = new TableColumn<Codes, Integer>(v[1]);
	    			item.setCellValueFactory(new PropertyValueFactory<Codes, Integer>(v[2]));
	    	    	col.add(item);
	    	    	break;
	    		}
	    		case "String":{
	    			TableColumn<Codes, String> item = new TableColumn<Codes, String>(v[1]);
	    	    	item.setCellValueFactory(new PropertyValueFactory<Codes, String>(v[2]));
	    	    	col.add(item);
	    	    	break;
	    		}
    		}
    	});
		return col;
	}
	public ObservableList<TableColumn<ItemTemplate, ?>> loadItemsTable(ObservableList<String[]> colInfo) {
    	ObservableList<TableColumn<ItemTemplate, ?>> col = FXCollections.observableArrayList();
		colInfo.forEach((v)->{
    		switch(v[0]) {
	    		case "Integer":{
	    			TableColumn<ItemTemplate, Integer> item = new TableColumn<ItemTemplate, Integer>(v[1]);
	    			item.setCellValueFactory(new PropertyValueFactory<ItemTemplate, Integer>(v[2]));
	    	    	col.add(item);
	    	    	break;
	    		}
	    		case "String":{
	    			TableColumn<ItemTemplate, String> item = new TableColumn<ItemTemplate, String>(v[1]);
	    	    	item.setCellValueFactory(new PropertyValueFactory<ItemTemplate, String>(v[2]));
	    	    	col.add(item);
	    	    	break;
	    		}
    		}
    	});
		return col;
	}
    private void continion() {
    	if(mask.getText().length()==12){
    		Boolean f = true;
    		for(String value : barcodesName) {
    			if(value.compareToIgnoreCase(name.getText())==0) {
    				f=false;
    				break;
    			}
    		}
    		if(f||this.item!=null) {
    			Codes temp = new Codes();
	    		int prefix = StringUtils.countMatches(mask.getText(),"P");
	    		int code = StringUtils.countMatches(mask.getText(),"C");
	    		int unit = StringUtils.countMatches(mask.getText(),"U");
	    		if(name.getText().length()<1)name.setText(prefix+"/"+code+"/"+unit);
	    		temp.setAll(name.getText(),prefix,code,unit,prefixValue.getText());
	    		temp.setMask(mask.getText());
	    		int id = 0;
				try {
					id = Integer.parseInt(this.number.getText());
					//temp.setId(id);
				}catch (Exception e) {
	    			if(this.item!=null)temp.setId(this.item.getId());
	    		}
				try {
					if(this.item==null) {
						temp.setId(id);
		    			if(temp.save(db)>0) {
		    				TextBox.alertOpenDialog(AlertType.INFORMATION,"addBarcodeYes");
			        		//close.onActionProperty();
			        	}else{
			        		TextBox.alertOpenDialog(AlertType.WARNING,"addBarcodeNot");
			        	}
		    		}else {
						temp.setId(this.item.getId());
						temp.setName(this.item.getName());
		    			if(this.item.getId()!=id)temp.updateId(id,db);
		    			if(this.item.getName().compareToIgnoreCase(name.getText())!=0)temp.updateName(name.getText(),db);
			    		if(temp.save(db)>-1) {
			    			TextBox.alertOpenDialog(AlertType.INFORMATION,"editBarcodeYes");
			        	}else{
			        		TextBox.alertOpenDialog(AlertType.WARNING, "editBarcodeNo");
			        	}
		    		}
				}catch( Exception e ) {
					System.out.println(e);
		    	}
    		} else TextBox.alertOpenDialog(AlertType.WARNING, "warningName");
		}else TextBox.alertOpenDialog(AlertType.ERROR, "editBarcodeNo");
    	this.load();
    }
	public void loadBarcode() {
    	ResultSet resul = db.getSelect("SELECT * FROM barcodes");
    	try {
			while(resul.next()) {
				String key = resul.getString(3) + "/" + resul.getInt(4) + "/" + resul.getInt(5);
				if(resul.getString("name").length()<1)this.barcodesName.add(key);
				else this.barcodesName.add(resul.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    public void load() {
    	itemsTable.getColumns().addAll(loadItemsTable(ContentInfo.getColumnsContent("templateCodes")));
    	itemsTable.setItems(ItemTemplate.getList(CodeInfo.ItemsTemplate));
    	this.barcodes = Codes.getList(0,"",db);
	    number.setText("");
		name.setText("");
	    mask.setText("");
	    this.item = null;
	    save.setDisable(true);
	    dataTable.getColumns().addAll(loadDataTable(ContentInfo.getColumnsContent("templateCodes")));
	    dataTable.setItems(this.barcodes);
	    this.loadBarcode();
    }

    @FXML
    void initialize() {
    	this.load();
    	stage.setOnCloseRequest(event->close());
    	save.setOnAction(event->continion());
    	deleteField.setOnAction(event ->mask.setText((String)mask.getText().subSequence(0, mask.getText().length()-1)));
    	clearField.setOnAction(event ->mask.setText(""));
    	number.textProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	name.textProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	mask.textProperty().addListener((obs, oldText, newText) ->save.setDisable(false));
    	delete.setOnAction(event ->{
    		int index = dataTable.getSelectionModel().getSelectedIndex();
            if (TextBox.alertOpenDialog(AlertType.CONFIRMATION, "deleteBarcode?", item.getName()).get() == ButtonType.OK) {
	    		if(index>-1) {
	    			if(this.item.delete(db)) {
	    				TextBox.alertOpenDialog(AlertType.INFORMATION, "deleteBarcodeYes");
		        	    this.item = null;
		    			this.load();
	            	}else{
	            		TextBox.alertOpenDialog(AlertType.WARNING, "deleteBarcodeNo");
	            	}
	    		}
            }
    	});
    	clear.setOnAction(event ->{
    	    number.setText("");
    		name.setText("");
    		prefixValue.setText("");
    		mask.setText("");
    	    this.item = null;
    	    save.setDisable(true);
    	});
    	itemsTable.setOnMouseClicked(event ->{
    		int index = itemsTable.getSelectionModel().selectedIndexProperty().get();
			String str = mask.getText();
			String[] barcodeSymbol = {"P","C","U"};
	    	if(mask.getText().length()<12){
				switch(index) {
					case 0:{
						if(str.length()<3) {
							mask.setText(str+barcodeSymbol[index]);
						}
						break;
					}
					case 1:{
						if(1<str.length()&&str.length()<=7) {
							mask.setText(str+barcodeSymbol[index]);
						}
						break;
					}
					case 2:{
						if(4<str.length()&&str.length()<=12) {
							mask.setText(str+barcodeSymbol[index]);
						}
						break;
					}
				}
	    	}
		});
    	dataTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    		if (newSelection != null) {
				this.item = newSelection;
			    number.setText(newSelection.getId()+"");
			    prefixValue.setText(newSelection.getPrefix_val());
    			name.setText(newSelection.getName());
        	    mask.setText(newSelection.getMask());
        	    save.setDisable(true);
    		}
		} );
    	if(item!=null)dataTable.getSelectionModel().select(item);
    }
	public Codes getItem() {
		return item;
	}
	public void setItem(Codes item) {
		this.item = item;
	}
	public void setSource(String source) {
		this.source.setText(source);
	}
}
