package application.controllers.parts;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.MainCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.models.Configs;
import application.models.Utils;
import application.views.languages.uk.parts.LogInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogCtrl {
	private AnchorPane logLoad;
	private Logger logger = LogManager.getLogger(LogCtrl.class);
	private double minSize = 30;

	@FXML
	private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "part", "Log");
	@FXML
	private URL location = getClass().getResource(Utils.getView("part", "Log"));
	@FXML
	private TitledPane logTitledPane;
	@FXML
	private AnchorPane logPane;
	@FXML
	private TextArea log;

	public LogCtrl(AnchorPane anchorPane) {
		try {
			FXMLLoader loader = new FXMLLoader(location, resources);
			loader.setController(this);
			logLoad = MainCtrl.loadAnchorPane(anchorPane, loader);

			logTitledPane.setOnMouseClicked(this::mouseClickedOnLogTitledPane);
		} catch (IOException e) {
			logLoad = null;
			MainWindowCtrl.setLog(e.getMessage());
		}
	}

	public void loadLog(AnchorPane anchorPane) {
		if (logLoad != null) {
			anchorPane.getChildren().add(logLoad);
		} else {
			MainWindowCtrl.setLog(this.getClass().getName() + ": error null fxml");
		}
	}

	@FXML
	void initialize() {
		assert logPane != null : "fx:id=\"logPane\" was not injected: check your FXML file 'Log.fxml'.";
		assert log != null : "fx:id=\"log\" was not injected: check your FXML file 'Log.fxml'.";
		assert logTitledPane != null : "fx:id=\"logTitledPane\" was not injected: check your FXML file 'Log.fxml'.";
		logTitledPane.setText(LogInfo.title);
	}

	public TextArea getLogObj() {
		return log;
	}

	public String getLog() {
		return log.getText();
	}

	public void setLog(String line) {
		this.log.setText(getLog() + line + System.lineSeparator());
		logger.info(line);
	}

	public void mouseClickedOnLogTitledPane(MouseEvent mouseEvent) {
		MainWindowCtrl.updateHeightLogPane(
				logTitledPane.isExpanded() ?
					MainWindowCtrl.getHeightOfCenterSplitPane() / 2
				:
					minSize
		);
	}
}
