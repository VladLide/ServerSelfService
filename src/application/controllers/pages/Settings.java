package application.controllers.pages;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import application.models.EditingCell;
import application.models.Info2Col;
import application.models.TextBox;
import application.models.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class Settings {
	private Info2Col item = null;
	private String jsonString = null;
	private ObservableList<String> key = FXCollections.observableArrayList();
	private Map<String, String> configImport = new HashMap<String, String>();

	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location = getClass().getResource(Utils.getView("fxml", "Settings"));
	@FXML
	private AnchorPane img;
	@FXML
	private Button clear;
	@FXML
	private Button setImport;
	@FXML
	private AnchorPane tableInfo;
	@FXML
	private Button close;
	@FXML
	private Button save;
	@FXML
	private TableView<Info2Col> table;
	@FXML
	private TableColumn<Info2Col, String> nameOptionsTable;
	@FXML
	private TableColumn<Info2Col, String> valueTable;
	@FXML
	private AnchorPane keyboard;

	public Settings(AnchorPane Panel) {
		try {
			FXMLLoader loader = new FXMLLoader(location);
			loader.setController(this);
			BorderPane settings = loader.load();
			Panel.getChildren().clear();
			Panel.getChildren().add(settings);
			AnchorPane.setBottomAnchor(settings, 0.0);
			AnchorPane.setTopAnchor(settings, 0.0);
			AnchorPane.setLeftAnchor(settings, 0.0);
			AnchorPane.setRightAnchor(settings, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void FXMLLoad(AnchorPane Panel) {
		try {
			FXMLLoader loader = new FXMLLoader(location);
			loader.setController(this);
			BorderPane settings = loader.load();
			Panel.getChildren().clear();
			Panel.getChildren().add(settings);
			AnchorPane.setBottomAnchor(settings, 0.0);
			AnchorPane.setTopAnchor(settings, 0.0);
			AnchorPane.setLeftAnchor(settings, 0.0);
			AnchorPane.setRightAnchor(settings, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void continion() {
		this.writeConfigFile("config.sys");
		this.load();
	}

	public void load() {
		this.readConfigFile("config.sys");
		ObservableList<Info2Col> arr = FXCollections.observableArrayList();
		nameOptionsTable.setCellValueFactory(new PropertyValueFactory<Info2Col, String>(TextBox.info2col[0][1]));
		valueTable.setCellValueFactory(new PropertyValueFactory<Info2Col, String>(TextBox.info2col[1][1]));
		for (int i = 0; i < key.size(); i++) {
			String s = configImport.get(key.get(i));
			arr.add(new Info2Col(key.get(i), s, i));
		}
		this.item = null;
		save.setDisable(true);
		table.setItems(arr);
	}

	@FXML
	void initialize() {
		load();
		setImport.setOnAction(event -> {
			// this.parentControler.openSettingsImport();
		});
		Callback<TableColumn<Info2Col, String>, TableCell<Info2Col, String>> cellFactory = new Callback<TableColumn<Info2Col, String>, TableCell<Info2Col, String>>() {
			public TableCell call(TableColumn p) {
				EditingCell edc = new EditingCell();
				return edc;
			}
		};
		valueTable.setCellFactory(cellFactory);
		valueTable.setOnEditCommit(new EventHandler<CellEditEvent<Info2Col, String>>() {
			@Override
			public void handle(CellEditEvent<Info2Col, String> v) {
				Info2Col item = ((Info2Col) v.getTableView().getItems().get(v.getTablePosition().getRow()));
				item.setValue(v.getNewValue());
				configImport.put(key.get(item.getType()), v.getNewValue());
				save.setDisable(false);
			}
		});
		save.setOnAction(event -> continion());
		close.setOnAction(event -> {
		});
	}

	public void readConfigFile(String name) {
		this.key.clear();
		this.configImport.clear();
		try {
			jsonString = new String(Files.readAllBytes(Paths.get(Utils.getPath("sys", name))));
			String[] nextLine = {};
			if (jsonString != null) {
				if (jsonString.length() > 0)
					nextLine = jsonString.split(System.getProperty("line.separator"));
			}
			for (String v : nextLine) {
				String[] key = v.split("=");
				this.key.add(key[0]);
				if (key.length == 2)
					this.configImport.put(key[0], key[1]);
				else
					this.configImport.put(key[0], "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeConfigFile(String name) {
		jsonString = "";
		this.configImport.forEach((key, value) -> {
			jsonString += key + "=" + value + System.getProperty("line.separator");
		});
		try {
			FileWriter myWriter = new FileWriter(Utils.getPath("sys", name));
			myWriter.write(jsonString);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}