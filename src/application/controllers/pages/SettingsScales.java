package application.controllers.pages;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.codec.digest.DigestUtils;

import application.controllers.MainCtrl;
import application.models.EditingCell;
import application.models.Info2Col;
import application.models.TextBox;
import application.models.Utils;
import application.models.net.mysql.tables.Scales;
import application.models.net.mysql.tables.Users;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class SettingsScales {
	private Scales scale;
	private Info2Col item = null;

	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location = getClass().getResource(Utils.getView("fxml", "SettingsScales"));
	@FXML
	private AnchorPane img;
	@FXML
	private Button clearPLU;
	@FXML
	private Button setImport;
	@FXML
	private AnchorPane tableInfo;
	@FXML
	private Button close;
	@FXML
	private TableView<Info2Col> table;
	@FXML
	private TableColumn<Info2Col, String> nameOptionsTable;
	@FXML
	private TableColumn<Info2Col, String> valueTable;
	@FXML
	private AnchorPane keyboard;

	public SettingsScales(AnchorPane Panel, Scales scale) {
		this.scale = scale;
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

	public void FXMLLoad(AnchorPane Panel, Scales scale) {
		this.scale = scale;
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

	public void load() {
		ObservableList<Info2Col> arr = FXCollections.observableArrayList();
		nameOptionsTable.setCellValueFactory(new PropertyValueFactory<Info2Col, String>(TextBox.info2col[0][1]));
		valueTable.setCellValueFactory(new PropertyValueFactory<Info2Col, String>(TextBox.info2col[1][1]));
		for (int i = 0; i < TextBox.options.length; i++) {
			switch (i) {
			case 0: {
				arr.add(new Info2Col(TextBox.options[i][0], "*****", -1));
				break;
			}
			case 1: {
				arr.add(new Info2Col(TextBox.options[i][0], "*****", -2));
				break;
			}
			default: {
				arr.add(new Info2Col(TextBox.options[i][0], scale.getConfigItem(TextBox.options[i][1]), i));
				break;
			}
			}
		}
		this.item = null;
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
				switch (item.getType()) {
				case -1: {
					/*
					 * Users us = new Users();
					 * us.setId(2);us.setLogin("admin");us.setPass(DigestUtils.md5Hex(item.getValue(
					 * )));us.setId_access_levels(1); us.save();
					 */
					break;
				}
				case -2: {
					/*
					 * Users us = new Users(scale.get);
					 * us.setId(4);us.setLogin("packing");us.setPass(DigestUtils.md5Hex(item.
					 * getValue()));us.setId_access_levels(2); us.save();
					 */
					break;
				}
				default: {
					scale.setConfigItem(TextBox.options[item.getType()][1], item.getValue());
					if (MainCtrl.getDB().dbConnection != null)
						scale.save(MainCtrl.getDB());
					scale.save(MainCtrl.getDB());
					break;
				}
				}
			}
		});
		close.setOnAction(event -> {
		});
	}
}