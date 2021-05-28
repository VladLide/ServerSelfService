package application;

import application.controllers.MainCtrl;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage stage) {
		MainCtrl.openMainWindow(stage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
