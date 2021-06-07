package application;

import application.controllers.MainCtrl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.core.config.Configurator;

public class Main extends Application {
	@Override
	public void start(Stage stage) {
		MainCtrl.openMainWindow(stage);
	}

	public static void main(String[] args) {
		// configure location of log4j2 configuration file
		Configurator.initialize(null, "resources/log4j2.xml");
		launch(args);
	}
}
