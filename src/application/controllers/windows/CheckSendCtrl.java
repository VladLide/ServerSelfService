package application.controllers.windows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.MainCtrl;
import application.models.Configs;
import application.models.Utils;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.objectinfo.ItemContent;
import application.views.languages.uk.parts.ContentInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CheckSendCtrl {
    private Stage stage = new Stage();

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"),"window","CheckSend");
    @FXML
    private URL location = getClass().getResource(Utils.getView("window", "CheckSend"));
    @FXML
    private TableView<ItemContent> table;
    @FXML
    private CheckBox checkAll;
    @FXML
    private Button send;

    public CheckSendCtrl() {
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
		loadTable();
    	stage.showAndWait();
    }
    public void close() {
    	stage.close();
    }

	public void loadTable() {
		table.getColumns().addAll(MainWindowCtrl.getContentCtrl().loadTable(ContentInfo.getInstance().columnsContent.get("send")));
		LoadInfo();
	}
	public void LoadInfo() {
		ObservableList<ItemContent> arr = ItemContent.getCheckSend(MainCtrl.getScales());
		ScaleItemMenu menuItem = new ScaleItemMenu();
		ItemContent item = new ItemContent();
		item.setObject(menuItem);
		item.setNumber(arr.size()+1);
		item.setId(menuItem.getId());
		item.setName(menuItem.getName());
		arr.add(item);
		table.setItems(arr);
	}

	@FXML
    void initialize() {
    	stage.setOnCloseRequest(event->{
    		close();
    	});
    	send.setOnAction(event->{
    		ObservableList<ScaleItemMenu> arr = FXCollections.observableArrayList();
    		table.getItems().forEach((v)->{
    			if(v.getCheckBox().isSelected())
    				arr.add((ScaleItemMenu)v.getObject());
    		});
    		MainWindowCtrl.getContentCtrl().getPack().setConnectSend(arr);
    		close();
    	});
    	checkAll.setOnAction(event->{
    		table.getItems().forEach((v)->{
    			v.setCheckBox(checkAll.isSelected());
    		});
    	});
    }

	public Stage getStage() {
		return stage;
	}
}