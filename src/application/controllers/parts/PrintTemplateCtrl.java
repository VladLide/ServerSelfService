package application.controllers.parts;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.models.Utils;
import application.models.tables.PLUInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PrintTemplateCtrl {
    public Stage thisStage;

    @FXML 
    private ResourceBundle resources;

    @FXML 
    private URL location = getClass().getResource(Utils.getView("forms", "Template"));

    @FXML
    private AnchorPane template; 

    public PrintTemplateCtrl() {
        this.thisStage = new Stage();
        try {
        	FXMLLoader loader = new FXMLLoader(location);
            loader.setController(this);
            this.thisStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showStage() {
    	this.thisStage.showAndWait();
    }
    public void closeStage() {
    	this.thisStage.close();
    }
    
    @FXML 
    void initialize() {
    }

	public AnchorPane getTemplate() {
		return template;
	}

	public void setTemplate(AnchorPane template) {
		this.template = template;
	}
    
}