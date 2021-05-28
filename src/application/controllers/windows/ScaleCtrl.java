package application.controllers.windows;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.controllers.MainCtrl;
import application.models.Configs;
import application.models.TextBox;
import application.models.Utils;
import application.models.net.ConfigNet;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Scales;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScaleCtrl {
    public Stage stage;
	private Scales scale = null;
	private ScaleItemMenu scaleInfo = null; 

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"),"window","Scale");
    @FXML
    private URL location = getClass().getResource(Utils.getView("window", "Scale"));
    @FXML
    private Accordion content;
    @FXML
    private TitledPane scalePane;
    @FXML
    private TitledPane netPane;
    @FXML
    private TextField number;
    @FXML
    private TextField nameScale;
    @FXML
    private TextField addressScale;
    @FXML
    private TextField addressServer;
    @FXML
    private TextField login;
    @FXML
    private TextField pass;
    @FXML
    private TextField nameDB;
    @FXML
    private TextField port;
    @FXML
    private Button close;
    @FXML
    private Button save;

    public ScaleCtrl(Scales scale) {
    	this.scale = scale;
        this.stage = new Stage();
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.initOwner(MainWindowCtrl.getMainStage());
        try {
        	FXMLLoader loader = new FXMLLoader(location,resources);
            loader.setController(this);
            this.stage.setScene(new Scene(loader.load()));
            this.stage.initStyle(StageStyle.UNDECORATED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showStage() {
    	this.stage.showAndWait();
    }
    public void close() {
    	this.stage.close();
    }
    private void continion() {
    	String adrress = addressScale.getText();
    	if(!adrress.matches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})")||adrress.matches("(localhost)|(127\\.0\\.0\\.1)")||adrress.length()<7) {
			scale.setUpdate(-3);
		}
    	int id = 0;
    	try {
    		id = Integer.parseInt(number.getText());
    	}catch (Exception e) {
			id = 0;
		}
    	this.scale.setId(id);
    	this.scale.setName(nameScale.getText());
    	this.scale.setIp_address(addressScale.getText());
    	this.scale.setIp_address_server(addressServer.getText());
    	if(this.scale.save(MainCtrl.getDB())>0) {
    		TextBox.alertOpenDialog(AlertType.INFORMATION, "editGoodsYes");
        	close();
			ConfigNet conf;
			try {
				conf = new ConfigNet(id);
		    	conf.setLogin(login.getText());
		    	conf.setPass(pass.getText());
		    	conf.setName(nameDB.getText());
		    	conf.setPort(port.getText());
		    	conf.setHost(addressScale.getText());
				conf.save(MainCtrl.getDB());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		MainWindowCtrl.updateSidebar(this.scale,false);
    	}else{
    		TextBox.alertOpenDialog(AlertType.WARNING, "editGoodsNo");
    	}
    }
	public void loadItem() {
		number.setText(scale.getId()+"");
		nameScale.setText(scale.getName());
		addressScale.setText(scale.getIp_address());
		addressServer.setText(scale.getIp_address_server());
		ConfigNet conf = scaleInfo.getDB().getConfig();
	    login.setText(conf.getLogin());
	    pass.setText(conf.getPass());
	    nameDB.setText(conf.getName());
	    port.setText(conf.getPort());
	}
    
    @FXML
    void initialize() {
    	close.setOnAction(event->{
    		close();
    	});
    	content.setExpandedPane(scalePane);
    	save.setOnAction(event->continion());
    }
	public Scales getScale() {
		return scale;
	}
	public void setScale(Scales scale) {
		this.scale = scale;
	}
	public ScaleItemMenu getScaleInfo() {
		return scaleInfo;
	}
	public void setScaleInfo(ScaleItemMenu scaleInfo) {
		this.scaleInfo = scaleInfo;
		loadItem();
	}
    
}
