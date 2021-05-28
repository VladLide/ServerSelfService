package application.controllers.windows;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.models.Configs;
import application.models.TextBox;
import application.models.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SizeTemplateCtrl{
    public Stage thisStage;
    private TemplateCtrl editor;
	private ObservableList<String> templates = FXCollections.observableArrayList();
    private int[] size;
    private Boolean openEdit = false;
    
	@FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"),"window","SizeTemplate");
    @FXML
    private URL location = getClass().getResource(Utils.getView("window", "SizeTemplate"));
    @FXML
    private TextField widthCheck;
    @FXML
    private TextField heightCheck;
    @FXML
    private TextField name;
    @FXML
    private AnchorPane keyboard;
    @FXML
    private Button widthPlusButton;
    @FXML
    private Button heightMinusButton;
    @FXML
    private Button widthMinusButton;
    @FXML
    private Button ok;
    @FXML
    private Button close;
    @FXML
    private Button heightPlusButton;
	
    public SizeTemplateCtrl(TemplateCtrl stage) {
        this.thisStage = new Stage();
        this.editor = stage;
        this.thisStage.initModality(Modality.WINDOW_MODAL);
        this.thisStage.initOwner(stage.getStage());
        this.templates = FXCollections.observableArrayList();
        try {
        	FXMLLoader loader = new FXMLLoader(location);
            loader.setController(this);
            this.thisStage.setScene(new Scene(loader.load()));
            this.thisStage.initStyle(StageStyle.UNDECORATED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showStage() {
    	this.thisStage.showAndWait();
    }
    public void closeStage() {
    	this.openEdit = false;
    	this.thisStage.close();
    }
    private void continion() {
    	Boolean f = true;
		for(String value : this.templates) {
			if(value.compareToIgnoreCase(name.getText())==0) {
				f=false;
				break;
			}
		}
    	if(f) {
    		try {
    			this.editor.getTemplate().setPrefSize(Double.parseDouble(widthCheck.getText())*7.5, Double.parseDouble(heightCheck.getText())*7.5);
    		}catch (Exception e) {
    			this.editor.getTemplate().setPrefSize(40*7.5, 58*7.5);
			}
	    	this.editor.getPaneSave().setPane(this.editor.getTemplate());
	    	this.editor.getPaneSave().setName(name.getText());
	    	this.editor.getPaneSave().setWidthContent((float)this.editor.getTemplate().getPrefWidth());
	    	this.editor.getPaneSave().setHeightContent((float)this.editor.getTemplate().getPrefHeight());
	    	this.openEdit = true;
	    	this.thisStage.hide();
	    	this.thisStage.close();
    	}else {
    		TextBox.alertOpenDialog(AlertType.WARNING, "warningName");
    	}
    }
	
    @FXML
    void initialize() {
    	close.setOnAction(event->closeStage());
    	ok.setOnAction(event->continion());
    	widthPlusButton.setOnAction(event ->widthCheck.setText((Integer.parseInt(widthCheck.getText())+1)+""));
    	heightPlusButton.setOnAction(event ->heightCheck.setText(Integer.parseInt(heightCheck.getText())+1+""));
    	widthMinusButton.setOnAction(event ->widthCheck.setText(Integer.parseInt(widthCheck.getText())-1+""));
    	heightMinusButton.setOnAction(event ->heightCheck.setText(Integer.parseInt(heightCheck.getText())-1+""));
    }
    public void setTemplates(ObservableList<String> names) {
    	this.templates = names;
	}
    public int[] getSize() {
		return size;
	}
	public void setSize(int[] size) {
		this.size = size;
	}
	public Boolean isOpenEdit() {
		return openEdit;
	}
	public void setOpenEdit(Boolean openEdit) {
		this.openEdit = openEdit;
	}
}

