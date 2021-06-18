package application.controllers.windows;

import application.Helper;
import application.enums.*;
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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProductCtrl {
	private boolean newItem = true;
	private boolean addMore = true;
	private boolean firstPass = true;
	private boolean beenDeleted = false;
	private int offset = 0;
	private Goods item = new Goods();
	private File file = null;
	private String ipAddress;
	private PlaceType placeType;
	private Thread notAccurateSearchThread;
	private final MySQL db;
	private final ObservableList<Goods> showList = FXCollections.observableArrayList();
	private final EventHandler<MouseEvent> consumeMouseDragEvent = Event::consume;
	private final Stage stage;
	private final Logger logger = LogManager.getLogger(ProductCtrl.class);
	private static final int STEP = 200;
	private static final ObjectType type = ObjectType.PRODUCTS;

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
	@FXML
	private TextField searchBar;

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
			logger.error(e.getMessage(), e);
		}
	}

	public void show() {
		this.stage.show();
		setUpScrollBar();
	}

	public void close() {
		this.stage.close();
	}

	public ObservableList<TableColumn<Goods, ?>> loadDataTable(ObservableList<String[]> colInfo) {
		ObservableList<TableColumn<Goods, ?>> col = FXCollections.observableArrayList();
		colInfo.forEach(v -> {
			TableColumn<Goods, ?> tableColumn = new TableColumn<>(v[1]);
			tableColumn.setCellValueFactory(new PropertyValueFactory<>(v[2]));
			col.add(tableColumn);
		});
		return col;
	}

	private void loadImage(AnchorPane imgPanel, String str) {
		if ("img".equalsIgnoreCase(str)) {
			try {
				Image image = this.item.getImage(imgPanel);
				imgPanel.setBackground(new Background(
						new BackgroundImage(image,
								BackgroundRepeat.NO_REPEAT,
								BackgroundRepeat.NO_REPEAT,
								BackgroundPosition.CENTER,
								new BackgroundSize(
										BackgroundSize.DEFAULT.getWidth(),
										BackgroundSize.DEFAULT.getHeight(),
										true,
										false,
										true,
										false))));
			} catch (Exception e) {
				imgPanel.setBackground(null);
				logger.warn("Background image is null");
			}
		} else {
			try {
				Image image = template.getSelectionModel().getSelectedItem().getImage(imgPanel, 0);
				logger.debug("Template image{}", image);
				imgPanel.setBackground(new Background(
						new BackgroundImage(
								image,
								BackgroundRepeat.NO_REPEAT,
								BackgroundRepeat.NO_REPEAT,
								BackgroundPosition.CENTER,
								new BackgroundSize(
										BackgroundSize.DEFAULT.getWidth(),
										BackgroundSize.DEFAULT.getHeight(),
										true,
										false,
										true,
										false))));
			} catch (Exception e) {
				imgPanel.setBackground(null);
				logger.warn("Background image is null");
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
				int number = getNumberForGoods();
				if (number != -1) {
					plu.setNumber(number);
				}

				if (this.file != null)
					plu.setData(this.file);
				if (newItem) {
					plu.setPre_code(preCode);
					if (this.file == null)
						plu.setDataBlob(null);
					if (plu.save(db) > 0) {
						TextBox.alertOpenDialog(AlertType.INFORMATION, "addGoodsYes");
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
					Goods goods = Optional
							.ofNullable(this.item)
							.orElseThrow(() -> new NullPointerException("Item is null"));
					plu.setPre_code(goods.getPre_code());
					plu.setDataBlob(goods.getData());
					if (goods.getPre_code() != preCode)
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
				update();
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
		showList.addAll(FXCollections.observableArrayList(
				Helper.getGoods(db, STEP, 0, type)
						.orElseThrow(type::getNullPointerException)));
		dataTable.setItems(showList);

		template.setItems(Templates.getList(0, "", true, db));
		code.setItems(Codes.getList(0, "", db));
		section.setItems(Sections.getList(0, 0, -1, "", false, db));

		name.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		number.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		pre_code.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		section.setOnAction(event -> save.setDisable(false));
		ingredients.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		price.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		unit.setOnAction(event -> save.setDisable(false));
		expirationDate.textProperty().addListener((obs, oldText, newText) -> save.setDisable(false));
		code.setOnAction(event -> save.setDisable(false));

	}

	public void update() {
		clearLoad();
		showList.addAll(FXCollections.observableArrayList(
				Helper.getGoods(db, STEP, 0, type)
						.orElseThrow(type::getNullPointerException)));
		dataTable.setItems(showList);
	}

	private void setUpScrollBar() {
		int numberOfItemsToContain = STEP;
		ScrollBar scrollBar = Helper.getDataTableScrollBar(dataTable)
				.orElseThrow(() -> new NullPointerException("Was not able to obtain ScrollBar from TableView"));

		scrollBar.setOnMouseReleased(event -> {
			addMore = true;
			scrollBar.removeEventFilter(MouseEvent.MOUSE_DRAGGED, consumeMouseDragEvent);
		});

		dataTable.addEventFilter(ScrollEvent.ANY, event -> addMore = true);
		scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
			double position = scrollBar.getValue();

			if (position == scrollBar.getMax() && addMore) {
				logger.info("Adding at the end");
				scrollBar.addEventFilter(MouseEvent.MOUSE_DRAGGED, consumeMouseDragEvent);

				addAtTheEnd(scrollBar);

				if (showList.size() > numberOfItemsToContain) {
					logger.info("Removing first {} items", numberOfItemsToContain);
					firstPass = true;
					beenDeleted = true;
					showList.remove(0, showList.size() - numberOfItemsToContain);
					scrollBar.setValue(0);
				}

				addMore = false;
			} else if (position <= 0.05 && addMore && beenDeleted && !firstPass) {
				logger.info("Adding at the beginning");
				scrollBar.addEventFilter(MouseEvent.MOUSE_DRAGGED, consumeMouseDragEvent);

				addAtTheBeginning(scrollBar);

				if (showList.size() > numberOfItemsToContain) {
					logger.info("Removing items and leaving only first {}", numberOfItemsToContain);
					logger.info("Size before deletion {}", showList.size());
					showList.remove(numberOfItemsToContain, showList.size());
					logger.info("Size after deletion {}", showList.size());
					scrollBar.setValue(0.99);
				}

			} else if (position >= 0.1 && addMore && beenDeleted) {
				firstPass = false;
			}
		});
	}

	int tmp = 5;

	private void addAtTheBeginning(ScrollBar scrollBar) {
		//todo fix if statement because first element in array can have number bigger than step and
		// we'll try to get more and there won't be any more and we'll get an error
		if (showList.get(0).getNumber() - STEP >= 0) {
			offset = offset - STEP + tmp;
			showList.addAll(0,
					Helper.getGoods(db, STEP, offset, type)
							.orElseThrow(type::getNullPointerException));
			double value = (1.0 / showList.size()) * STEP;
			logger.debug("Setting value to {}", value);
			scrollBar.setValue(value);
		} else {
			beenDeleted = false;
		}
	}

	private void addAtTheEnd(ScrollBar scrollBar) {
		double targetValue = scrollBar.getValue() * showList.size();

		offset = offset + STEP - tmp;
		showList.addAll(
				Helper.getGoods(db, STEP, offset, type)
						.orElseThrow(type::getNullPointerException));
		double value = targetValue / showList.size();
		logger.debug("Setting value {}", value);
		scrollBar.setValue(value);
	}

	@FXML
	void initialize() {
		load();
		clear.setOnAction(event -> clearLoad());
		delete.setOnAction(event -> deleteGoods());
		save.setOnAction(event -> saveGoods());
		addImg.setDisable(false);
		addImg.setOnAction(event -> addImage());
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
		dataTable.getSelectionModel()
				.selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						setItem(newSelection);
						newItem = false;
					}
				});
		searchBar.setOnKeyPressed(this::search);
	}

	/**
	 * Will search for goods with number or pre_code or name which we get from searchBar
	 *
	 * @param event some key event which happened inside of search bar
	 */
	private void search(KeyEvent event) {
		//kill previous non accurate search if it is alive
		if (notAccurateSearchThread != null && notAccurateSearchThread.isAlive()) {
			notAccurateSearchThread.interrupt();
		}

		String text;
		String fromSearchBar = searchBar.getText();

		if (KeyCode.BACK_SPACE.equals(event.getCode()) && fromSearchBar.length() > 0) {
			text = fromSearchBar.substring(0, fromSearchBar.length() - 1);
		} else if (KeyCode.BACK_SPACE.equals(event.getCode())) {
			text = "";
		} else if (!KeyCode.ENTER.equals(event.getCode())) {
			text = fromSearchBar + event.getText();
		} else {
			text = fromSearchBar;
		}

		ObservableList<Goods> items = Goods.getList(0, 0, "", 0, 0, db);
		if (!text.trim().isEmpty()) {
			List<Goods> collect;
			if (isNumeric(text)) {
				int integer = Integer.parseInt(text);
				collect = items
						.stream()
						.filter(goods -> goods.getNumber() == integer || goods.getPre_code() == integer)
						.collect(Collectors.toList());
			} else {
				collect = items
						.stream()
						.filter(goods -> text.equalsIgnoreCase(goods.getName()))
						.collect(Collectors.toList());
			}
			items = FXCollections.observableArrayList(collect);
		}

		dataTable.setItems(items);

		notAccurateSearchThread = notAccurateSearch(text);
		notAccurateSearchThread.start();
	}

	/**
	 * Will create Thread object which will start not accurate search, it means we are searching
	 * for all products which number or pre_code or name starts with given text
	 *
	 * @param text string which contains input from searchBar
	 * @return thread with code to start non accurate search
	 */
	private Thread notAccurateSearch(String text) {
		return new Thread(() -> {
			ObservableList<Goods> items = Goods.getList(0, 0, "", 0, 0, db);
			items = FXCollections.observableArrayList(
					items.stream()
							.filter(goods -> String.valueOf(goods.getNumber()).startsWith(text)
									|| String.valueOf(goods.getPre_code()).startsWith(text)
									|| goods.getName().toLowerCase(Locale.ROOT).startsWith(text.toLowerCase(Locale.ROOT)))
							.collect(Collectors.toList())
			);
			dataTable.setItems(items);
		});
	}

	private void deleteGoods() {
		Goods goods = dataTable.getSelectionModel().getSelectedItem();
		ButtonType buttonType = TextBox
				.alertOpenDialog(AlertType.CONFIRMATION, "deleteBarcode?", goods.getName())
				.orElseThrow(() -> new NullPointerException("ButtonType is null"));
		boolean deleteGoods = ButtonType.OK.equals(buttonType);
		boolean goodsIsDeleted = false;

		if (deleteGoods) {
			goodsIsDeleted = Goods.delete(goods.getPre_code(), db);
		}

		if (deleteGoods && goodsIsDeleted) {
			TextBox.alertOpenDialog(AlertType.INFORMATION, "deleteBarcodeYes");
			MainWindowCtrl.setLog(
					Helper.formatOutput(
							Operation.DELETE,
							placeType,
							ipAddress,
							SectionType.PRODUCT,
							goods.getName(),
							LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
							OperationStatus.SUCCESS
					)
			);
			clearLoad();
		} else if (deleteGoods) {
			TextBox.alertOpenDialog(AlertType.WARNING, "deleteBarcodeNo");

			MainWindowCtrl.setLog(
					Helper.formatOutput(
							Operation.DELETE,
							placeType,
							ipAddress,
							SectionType.PRODUCT,
							goods.getName(),
							LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
							OperationStatus.FAILURE
					)
			);
		}
	}

	public void setItem(Goods product) {
		item = (product != null) ? product : new Goods();
		unit.setItems(ProductInfo.unit);
		if (product != null) {
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
		if (!newItem)
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

	private void saveGoods() {
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
	}

	private void addImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Image");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpeg", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));
		file = fileChooser.showOpenDialog(stage);
		if (file == null) {
			TextBox.alertOpenDialog(AlertType.WARNING, "chooseImageNo");
		} else {
			item.setData(file);
			loadImage(img, "img");
			save.setDisable(false);
		}
	}

	private boolean isNumeric(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private int getNumberForGoods() {
		if (isNumeric(number.getText())) {
			return Integer.parseInt(number.getText());
		} else {
			if (item != null) {
				return item.getNumber();
			} else {
				return -1;
			}
		}
	}
}
