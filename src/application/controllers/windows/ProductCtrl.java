package application.controllers.windows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import application.*;
import application.models.Configs;
import application.models.TextBox;
import application.models.Utils;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Templates;
import application.views.languages.uk.windows.ProductInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductCtrl {
	private Stage stage;
	private Goods item = null;
	private boolean newItem = true;
	private File file = null;
	private MySQL db = null;
	private String ipAddress;
	private PlaceType placeType;

	@FXML
	private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "window", "Product");
	@FXML
	private URL location = getClass().getResource(Utils.getView("window", "Product"));
	@FXML
	private AnchorPane img;
	@FXML
	private AnchorPane imgTemplate;
	@FXML
	private TableView<Goods> dataTable;
	@FXML
	private ComboBox<Templates> template;
	@FXML
	private ComboBox<Codes> code;
	@FXML
	private ComboBox<Sections> section;
	@FXML
	private ComboBox<String> unit;
	@FXML
	private Label source;
	@FXML
	private TextArea ingredients;
	@FXML
	private TextField pre_code;
	@FXML
	private TextField number;
	@FXML
	private TextField price;
	@FXML
	private TextField name;
	@FXML
	private TextField expirationDate;
	@FXML
	private Button addImg;
	@FXML
	private Button delImg;
	@FXML
	private Button save;
	@FXML
	private Button clear;
	@FXML
	private Button delete;

	public ProductCtrl(MySQL db) {
		this.db = db;
		this.stage = new Stage();
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.initOwner(MainWindowCtrl.getMainStage());
		try {
			FXMLLoader loader = new FXMLLoader(location, resources);
			loader.setController(this);
			this.stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void show() {
		load();
		this.stage.showAndWait();
	}

	public void close() {
		this.stage.close();
	}

	public ObservableList<TableColumn<Goods, ?>> loadDataTable(ObservableList<String[]> colInfo) {
		ObservableList<TableColumn<Goods, ?>> col = FXCollections.observableArrayList();
		colInfo.forEach(v -> {
			switch (v[0]) {
			case "Integer": {
				TableColumn<Goods, Integer> item = new TableColumn<Goods, Integer>(v[1]);
				item.setCellValueFactory(new PropertyValueFactory<Goods, Integer>(v[2]));
				col.add(item);
				break;
			}
			case "String": {
				TableColumn<Goods, String> item = new TableColumn<Goods, String>(v[1]);
				item.setCellValueFactory(new PropertyValueFactory<Goods, String>(v[2]));
				col.add(item);
				break;
			}
			}
		});
		return col;
	}

	private void loadImage(AnchorPane imgpanel, String str) {
		imgpanel.getChildren().clear();
		if (str.compareToIgnoreCase("img") == 0) {
			try {
				imgpanel.setBackground(new Background(new BackgroundImage(this.item.getImage(imgpanel),
						BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
						new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true,
								false, true, false))));
			} catch (Exception e) {
				imgpanel.setBackground(null);
				System.out.println("ButtonWithImage: no image - " + e);
			}
		} else {
			try {
				imgpanel.setBackground(new Background(
						new BackgroundImage(template.getSelectionModel().getSelectedItem().getImage(imgpanel, 0),
								BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
								new BackgroundSize(BackgroundSize.DEFAULT.getWidth(),
										BackgroundSize.DEFAULT.getHeight(), true, false, true, false))));
			} catch (Exception e) {
				imgpanel.setBackground(null);
				System.out.println("ButtonWithImage: no image - " + e);
			}
		}
	}

	private void continion() {
		try {
			if (section.getSelectionModel().getSelectedItem() == null
					|| template.getSelectionModel().getSelectedItem() == null
					|| code.getSelectionModel().getSelectedItem() == null
					|| unit.getSelectionModel().getSelectedItem() == null
					|| pre_code.getText().length() <= 0) {
				TextBox.alertOpenDialog(AlertType.ERROR, "editGoodsNo");
			} else {
				Goods plu = new Goods();
				int preCode = Integer.parseInt(pre_code.getText());
				plu.setId_sections(section.getSelectionModel().getSelectedItem().getId());
				plu.setId_templates(template.getSelectionModel().getSelectedItem().getId());
				plu.setId_barcodes(code.getSelectionModel().getSelectedItem().getId());
				plu.setName(name.getText());
				plu.setFull_name(name.getText());
				plu.setPrice(Float.parseFloat(price.getText().replace(",", ".")));
				plu.setType(unit.getSelectionModel().getSelectedItem());
				plu.setBefore_validity(
						(expirationDate.getText().length() > 0) ? Integer.parseInt(expirationDate.getText()) : 0);
				plu.setIngredients(ingredients.getText());
				plu.setMin_type((float) 0.04);
				try {
					plu.setNumber(Integer.parseInt(number.getText()));
				} catch (NumberFormatException e) {
					if (item != null) {
						plu.setNumber(item.getNumber());
					}
				}

				if (this.file != null)
					plu.setData(this.file);
				if (newItem) {
					plu.setPre_code(preCode);
					if (this.file == null)
						plu.setDataBlob(null);
					if (plu.save(db) > 0) {
						TextBox.alertOpenDialog(AlertType.INFORMATION, "addGoodsYes");
						load();
						MainWindowCtrl.setLog(
								Helper.formatOutput(
										Operation.CREATE,
										placeType,
										ipAddress,
										SectionType.PRODUCT,
										plu.getName(),
										LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
										OperationStatus.SUCCESS
								)
						);
					} else {
						TextBox.alertOpenDialog(AlertType.WARNING, "addGoodsNot");
						MainWindowCtrl.setLog(
								Helper.formatOutput(
										Operation.CREATE,
										placeType,
										ipAddress,
										SectionType.PRODUCT,
										plu.getName(),
										LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
										OperationStatus.FAILURE
								)
						);
					}
				} else {
					// todo item.getPre_code can throw nullPointer exception
					plu.setPre_code(this.item.getPre_code());
					plu.setDataBlob(this.item.getData());
					if (this.item.getPre_code() != preCode)
						plu.updatePre_code(preCode, db);
					if (plu.save(db) > 0) {
						TextBox.alertOpenDialog(AlertType.INFORMATION, "editGoodsYes");
						MainWindowCtrl.setLog(
								Helper.formatOutput(
										Operation.UPDATE,
										placeType,
										ipAddress,
										SectionType.PRODUCT,
										plu.getName(),
										LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
										OperationStatus.SUCCESS
								)
						);
					} else {
						TextBox.alertOpenDialog(AlertType.WARNING, "editGoodsNo");
						MainWindowCtrl.setLog(
								Helper.formatOutput(
										Operation.UPDATE,
										placeType,
										ipAddress,
										SectionType.PRODUCT,
										plu.getName(),
										LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
										OperationStatus.FAILURE
								)
						);
					}
				}
			}
		} catch (Exception e) {
			TextBox.alertOpenDialog(AlertType.ERROR, "saveGoodsNo", e.getMessage());
		}
	}

	public void clearLoad() {
		name.setText("");
		number.setText("");
		pre_code.setText("");
		section.setValue(null);
		ingredients.setText("");
		price.setText("");
		unit.setValue(null);
		expirationDate.setText("");
		template.setValue(null);
		code.setValue(null);
		img.setBackground(null);
		imgTemplate.setBackground(null);
		this.item = new Goods();
		this.file = null;
		save.setDisable(true);
		newItem = true;
	}

	public void load() {
		clearLoad();
		dataTable.getColumns().addAll(loadDataTable(ProductInfo.dataTableColums));
		dataTable.setItems(Goods.getList(0, 0, "", 0, 0, db));
		template.setItems(Templates.getList(0, "", true, db));
		code.setItems(Codes.getList(0, "", db));
		section.setItems(Sections.getList(0, 0, -1, "", false, db));

		name.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		number.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		pre_code.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		section.setOnAction(event -> {
			save.setDisable(false);
		});
		ingredients.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		price.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		unit.setOnAction(event -> {
			save.setDisable(false);
		});
		expirationDate.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		code.setOnAction(event -> {
			save.setDisable(false);
		});
	}

	@FXML
	void initialize() {
		clear.setOnAction(event -> {
			clearLoad();
		});
		delete.setOnAction(event -> {
			Goods item = dataTable.getSelectionModel().getSelectedItem();
			// todo fix optional.get() without checking if present
			if (TextBox.alertOpenDialog(AlertType.CONFIRMATION, "deleteBarcode?", item.getName())
					.get() == ButtonType.OK) {
				if (item != null) {
					if (Goods.delete(item.getPre_code(), db)) {
						TextBox.alertOpenDialog(AlertType.INFORMATION, "deleteBarcodeYes");
						MainWindowCtrl.setLog(
								Helper.formatOutput(
										Operation.DELETE,
										placeType,
										ipAddress,
										SectionType.PRODUCT,
										item.getName(),
										LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
										OperationStatus.SUCCESS
								)
						);
						item = null;
						clearLoad();
					} else {
						TextBox.alertOpenDialog(AlertType.WARNING, "deleteBarcodeNo");

						MainWindowCtrl.setLog(
								Helper.formatOutput(
										Operation.DELETE,
										placeType,
										ipAddress,
										SectionType.PRODUCT,
										item.getName(),
										LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
										OperationStatus.FAILURE
								)
						);
					}
				}
			}
		});
		save.setOnAction(event -> {
			try {
				if (section.getSelectionModel().getSelectedItem() == null
						|| template.getSelectionModel().getSelectedItem() == null
						|| code.getSelectionModel().getSelectedItem() == null
						|| unit.getSelectionModel().getSelectedItem() == null
						|| pre_code.getText().length() <= 0) {
					TextBox.alertOpenDialog(AlertType.ERROR, "editGoodsNo");
				} else {
					continion();
				}
			} catch (Exception e) {
				TextBox.alertOpenDialog(AlertType.WARNING, "saveGoodsNo");
			}
		});
		addImg.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select Image");
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpeg", "*.jpg"),
					new FileChooser.ExtensionFilter("PNG", "*.png"));
			file = fileChooser.showOpenDialog(stage);
			if (file == null) {
				TextBox.alertOpenDialog(AlertType.WARNING, "chooseImageNo");
			} else {
				item.setData(file);
				this.loadImage(img, "img");
				save.setDisable(false);
			}
		});
		delImg.setOnAction(event -> {
			img.setBackground(null);
			item.setDataBlob(null);
			save.setDisable(false);
		});
		template.setOnAction(event -> {
			Templates selection = template.getSelectionModel().getSelectedItem();
			if (selection != null) {
				item.setId_templates(selection.getId());
				save.setDisable(false);
			}
			loadImage(imgTemplate, "tpl");
		});
		dataTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						setItem(newSelection);
						newItem = false;
					}
				});
	}

	public void setItem(Goods product) {
		item = (product != null) ? product : new Goods();
		unit.setItems(ProductInfo.unit);
		if (product != null) {
			// this.image = this.item.getImages();
			name.setText(item.getName());
			section.setValue((item.getId_sections() > 0) ? Sections.get(item.getId_sections(), 0, -1, "", false, db) : null);
			number.setText(item.getNumber() + "");
			pre_code.setText(item.getPre_code() + "");
			ingredients.setText(item.getIngredients());
			price.setText(Float.toString(item.getPrice()));
			unit.setValue(ProductInfo.unit.get(item.getType()));
			expirationDate.setText(Integer.toString(item.getBefore_validity()));
			template.setValue((item.getId_templates() > 0) ? Templates.get(item.getId_templates(), "", false, db) : null);
			code.setValue(Codes.get(item.getId_barcodes(), "", db));
			loadImage(img, "img");
			loadImage(imgTemplate, "tpl");
		}
		save.setDisable(true);
		if (item != null)
			dataTable.getSelectionModel().select(item);
	}

	public Goods getItem() {
		return item;
	}

	public void setSource(String source) {
		this.source.setText(source);
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setPlaceType(PlaceType placeType) {
		this.placeType = placeType;
	}
}
