package application.controllers.parts;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.MainCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.models.Configs;
import application.models.PackageSend;
import application.models.SendObjectInScale;
import application.models.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

public class FooterCtrl {
	private AnchorPane footer;
	private SendObjectInScale send;

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"),"part","Footer");
    @FXML
    private URL location = getClass().getResource(Utils.getView("part", "Footer"));
    @FXML
    private Label process;
    @FXML
    private ProgressBar progress;
    @FXML
    private Button stop;
    
	public FooterCtrl(AnchorPane anchorPane) {
		try {
            FXMLLoader loader = new FXMLLoader(location,resources);
            loader.setController(this);
            footer = MainCtrl.loadAnchorPane(anchorPane, loader);
        } catch (IOException e) {
        	footer = null;
            MainWindowCtrl.setLog(e.getMessage());
        }
	}
	public void loadFooter(AnchorPane anchorPane) {
		if(footer!=null) {
			anchorPane.getChildren().add(footer);
		}else {
			MainWindowCtrl.setLog(this.getClass().getName()+": error null fxml");
		}
	}

    public void startTask(PackageSend pack) {
        if (send != null && send.isRunning()) {
        	send.cancel();
        }

        send = new SendObjectInScale(pack);
        Thread thread = new Thread(send);
        thread.setDaemon(true);
        thread.start();

        progress.progressProperty().bind(send.progressProperty());
        process.textProperty().bind(send.messageProperty());
        MainWindowCtrl.getContentCtrl().getSend().disableProperty().bind(send.runningProperty());
        stop.disableProperty().bind(send.runningProperty().not());
    }

    public void stop() {
    	send.runningProperty().not();
    }
    public void cancelTask(ActionEvent event) {
        if (send != null) send.cancel();
    }
    
    @FXML
    void initialize() {
    	stop.setOnAction(event->{
    		cancelTask(event);
    	});
    }
}
