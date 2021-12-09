package application.controllers.parts;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Helper;
import application.controllers.MainCtrl;
import application.controllers.windows.ChooseCtrl;
import application.controllers.windows.ChooseSendCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.enums.CurrentItemSendTo;
import application.enums.ObjectType;
import application.enums.Operation;
import application.enums.OperationStatus;
import application.enums.PlaceType;
import application.enums.SectionType;
import application.models.Configs;
import application.models.PackageSend;
import application.models.Utils;
import application.models.net.database.mysql.MySQL;
import application.models.net.database.mysql.SqlQueryBuilder;
import application.models.net.database.mysql.interface_tables.ScaleItemMenu;
import application.models.net.database.mysql.tables.Codes;
import application.models.net.database.mysql.tables.Goods;
import application.models.net.database.mysql.tables.Scales;
import application.models.net.database.mysql.tables.Sections;
import application.models.net.database.mysql.tables.Templates;
import application.models.objectinfo.ItemContent;
import application.models.objectinfo.NodeTree;
import application.views.languages.uk.parts.ContentInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

public class ContentCtrl {
	private final ObservableList<ItemContent> showList = FXCollections.observableArrayList();
	private final Logger logger = LogManager.getLogger(ContentCtrl.class);
	private final EventHandler<MouseEvent> consumeMouseDragEvent = Event::consume;
	private static final int commonObjectsBetweenPages = 5;
	private static final int STEP = 200;
	private int offset = 0;
	private int databaseTableSize;
	private boolean addMore = true;
	private boolean tableHasMore = true;
	private AnchorPane content;
	private NodeTree node = null;
	private PackageSend pack = null;
	private MySQL db;
	private ObjectType type;
	private ScaleItemMenu sim = null;

	@FXML
	private final ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "part", "Content");
	@FXML
	private final URL location = getClass().getResource(Utils.getView("part", "Content"));
	@FXML
	private TableView<ItemContent> dataTable;
	@FXML
	private CheckBox check;
	@FXML
	private Button create;
	@FXML
	private Button edit;
	@FXML
	private Button delete;
	@FXML
	private Button send;
	@FXML
	private ContextMenu contextMenu;
	@FXML
	private MenuItem createCM;
	@FXML
	private MenuItem editCM;
	@FXML
	private MenuItem deleteCM;
	@FXML
	private Menu connectCM;
	@FXML
	private MenuItem sectionsCM;
	@FXML
	private MenuItem templateCodesCM;
	@FXML
	private MenuItem templatesCM;
	@FXML
	private MenuItem stocksCM;

	public ContentCtrl(AnchorPane anchorPane) {
		try {
			FXMLLoader loader = new FXMLLoader(location, resources);
			loader.setController(this);
			content = MainCtrl.loadAnchorPane(anchorPane, loader);
		} catch (IOException e) {
			content = null;
			MainWindowCtrl.setLog(e.getMessage());
		}
	}

	public void loadContent(AnchorPane anchorPane) {
		if (content != null) {
			anchorPane.getChildren().add(content);
		} else {
			MainWindowCtrl.setLog(this.getClass().getName() + ": error null fxml");
		}
	}

	public void showTableRedactorData(NodeTree node) {
		this.node = node;

		dataTable.getItems().clear();
		dataTable.getColumns().clear();
		dataTable.setEditable(true);
		switch (node.getType()) {
		case "products": {
			type = ObjectType.PRODUCTS;
			db = getDbDependingOnNode(node).orElse(null);

			if (db != null) {
				showList.addAll(ItemContent.get(FXCollections.observableArrayList(
						Helper.getData(db, STEP, 0, type).orElseThrow(type::getNullPointerException))));
			}
			break;
		}
		case "sections": {
			type = ObjectType.SECTIONS;
			db = getDbDependingOnNode(node).orElse(null);

			if (db != null) {
				showList.addAll(ItemContent.get(FXCollections.observableArrayList(
						Helper.getData(db, STEP, 0, type).orElseThrow(type::getNullPointerException))));
			}
			break;
		}
		case "templates": {
			type = ObjectType.TEMPLATES;
			db = getDbDependingOnNode(node).orElse(null);

			if (db != null) {
				showList.addAll(ItemContent.get(FXCollections.observableArrayList(
						Helper.getData(db, STEP, 0, type).orElseThrow(type::getNullPointerException))));
			}

			break;
		}
		case "templateCodes": {
			type = ObjectType.TEMPLATES_CODES;
			db = getDbDependingOnNode(node).orElse(null);
			if (db != null) {
				showList.addAll(ItemContent.get(FXCollections.observableArrayList(
						Helper.getData(db, STEP, 0, type).orElseThrow(type::getNullPointerException))));
			}

			break;
		}
		case "settings": {
			type = ObjectType.SETTINGS;
			db = getDbDependingOnNode(node).orElse(null);
			if (db != null) {
				showList.addAll(ItemContent.get(FXCollections.observableArrayList(
						Helper.getData(db, STEP, 0, type).orElseThrow(type::getNullPointerException))));
			}
			break;
		}
		default:
			break;
		}

		// if we have any data from database load it to the table
		if (!showList.isEmpty()) {
			// line below must be in if cause if we just click on scale it tries to load
			// some data and crashes,
			// so load it only if we got data from database
			dataTable.getColumns().addAll(loadTable(ContentInfo.getInstance().columnsContent.get(node.getType())));
			dataTable.setItems(showList);
			if (node.getType() == "settings") {

				ObservableList<TableColumn<ItemContent, ?>> columns = dataTable.getColumns();
				TableColumn<ItemContent, String> valuesColumn = (TableColumn<ItemContent, String>) columns.get(1);
				valuesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
				valuesColumn.setOnEditCommit(new EventHandler<CellEditEvent<ItemContent, String>>() {
					@Override
					public void handle(CellEditEvent<ItemContent, String> t) {
						ItemContent item = (ItemContent) t.getTableView().getItems().get(t.getTablePosition().getRow());
						item.setValue(t.getNewValue());
						if (node.getLevel() == 2)
							MainCtrl.setSettings(item.getName(), item.getValue());
						else {
							Scales scale = Scales.get(0, "", db);
							scale.setConfigItem(item.getName(), item.getValue());
							scale.save(db);
						}
					}
				});

			}
		}

		setUpScrollBar();
		getNumberOfItemsInTable();
	}

	public void operationsData(ItemContent item, boolean del) {
		MainWindowCtrl mainWindowCtrl = MainWindowCtrl.getInstance();
		String source = "";

		if (node.getLevel() == 2) {
			source = "Server (localhost)";
		} else {

			source = sim.getName() + " - " + sim.getId() + " (" + sim.getScale().getIp_address() + ")";
		}

		String ipAddress = sim != null ? sim.getScale().getIp_address() : "localhost";
		PlaceType placeType = sim != null ? PlaceType.SCALE : PlaceType.SERVER;

		switch (node.getType()) {
		case "products": {
			if (item != null && del) {
				Goods goods = (Goods) item.getObject();
				goods.delete(db);

				// logging context menu deletion of goods
				MainWindowCtrl.setLog(Helper.formatOutput(Operation.DELETE, placeType, ipAddress, SectionType.PRODUCT,
						goods.getName(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
						OperationStatus.SUCCESS));
			} else {
				mainWindowCtrl.openPlu((item != null) ? (Goods) item.getObject() : null, source, db, ipAddress,
						placeType, this);
			}
			break;
		}
		case "sections": {
			if (item != null && del) {
				Sections sections = (Sections) item.getObject();
				sections.delete(false, db);

				// logging context menu deletion of section
				MainWindowCtrl.setLog(Helper.formatOutput(Operation.DELETE, placeType, ipAddress, SectionType.SECTION,
						sections.getName(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
						OperationStatus.SUCCESS));
			} else {
				Sections sectionsWithImage;
				if (item == null) {
					sectionsWithImage = null;
				} else {
					sectionsWithImage = Sections.get(item.getId(), 0, -1, "", true, db);
				}
				mainWindowCtrl.openSection(sectionsWithImage, source, db, ipAddress, placeType, this);
			}
			break;
		}
		case "templates": {
			if (item != null && del) {
				Templates templates = (Templates) item.getObject();
				templates.delete(db);

				// logging context menu deletion of template
				MainWindowCtrl.setLog(Helper.formatOutput(Operation.DELETE, placeType, ipAddress, SectionType.TEMPLATE,
						templates.getName(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
						OperationStatus.SUCCESS));
			} else {
				mainWindowCtrl.openTemplate((item != null) ? (Templates) item.getObject() : null, db, ipAddress,
						placeType, this);
			}
			break;
		}
		case "templateCodes": {
			if (item != null && del) {
				Codes codes = (Codes) item.getObject();
				codes.delete(db);

				// logging context menu deletion of templateCodes
				MainWindowCtrl.setLog(Helper.formatOutput(Operation.DELETE, placeType, ipAddress, SectionType.CODE,
						codes.getName(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
						OperationStatus.SUCCESS));
			} else {
				mainWindowCtrl.openCode((item != null) ? (Codes) item.getObject() : null, source, db, ipAddress,
						placeType, this);
			}
			break;
		}
		case "settings": {
			// todo fix empty settings
			break;
		}
		default:
			break;
		}
		dataTable.refresh();
	}

	public static ObservableList<TableColumn<ItemContent, ?>> loadTable(ObservableList<String[]> colInfo) {
		ObservableList<TableColumn<ItemContent, ?>> col = FXCollections.observableArrayList();
		colInfo.forEach(value -> {
			TableColumn<ItemContent, ?> item = new TableColumn<>(value[1]);
			item.setCellValueFactory(new PropertyValueFactory<>(value[2]));
			col.add(item);
		});
		return col;
	}

	private void addSend() {
		pack = new PackageSend();
		CurrentItemSendTo currentItemSendTo;

		if (node.getLevel() == 2) {
			currentItemSendTo = CurrentItemSendTo.SERVER;
		} else {
			currentItemSendTo = CurrentItemSendTo.SCALE;
			currentItemSendTo.setScaleItemMenu((ScaleItemMenu) node.getUpObject().getObject());
		}

		new ChooseSendCtrl(currentItemSendTo).show();

		if (!pack.getConnectSend().isEmpty()) {
			ObservableList<Object> arr = FXCollections.observableArrayList();
			dataTable.getItems().forEach(item -> {
				if (item.isSelected()) {
					if (pack.getType().length() < 1)
						pack.setType(item.getTypeOdject());
					arr.add(item.getObject());
				}
			});
			if (!arr.isEmpty()) {
				pack.setItems(arr);
				MainCtrl.addPacks(pack);
			}
		}
	}

	@FXML
	void initialize() {
		assert create != null : "fx:id=\"create\" was not injected: check your FXML file 'Content.fxml'.";
		assert check != null : "fx:id=\"check\" was not injected: check your FXML file 'Content.fxml'.";
		assert dataTable != null : "fx:id=\"viewTreeTable\" was not injected: check your FXML file 'Content.fxml'.";
		assert delete != null : "fx:id=\"delete\" was not injected: check your FXML file 'Content.fxml'.";

		delete.setDisable(false);

		dataTable.setOnContextMenuRequested(event -> {
			ItemContent item = dataTable.getSelectionModel().getSelectedItem();
			if (item != null) {
				editCM.setDisable(false);
				deleteCM.setDisable(false);
				connectCM.setVisible(item.getCompareOdject("Goods"));
			} else {
				editCM.setDisable(true);
				deleteCM.setDisable(true);
				connectCM.setVisible(false);
			}
		});

		send.setOnAction(event -> addSend());
		check.setOnAction(event -> dataTable.getItems().forEach(item -> item.setCheckBox(check.isSelected())));
		create.setOnAction(event -> operationsData(null, false));
		edit.setOnAction(event -> {
			ItemContent item = dataTable.getSelectionModel().getSelectedItem();
			operationsData(item, false);
		});
		delete.setOnAction(
				event -> dataTable.getItems().stream().filter(itemContent -> itemContent.getCheckBox().isSelected())
						.forEach(itemContent -> Platform.runLater(() -> {
							operationsData(itemContent, true);
							dataTable.getItems().remove(itemContent);
							dataTable.refresh();
						})));
		createCM.setOnAction(event -> operationsData(null, false));
		editCM.setOnAction(event -> {
			ItemContent item = dataTable.getSelectionModel().getSelectedItem();
			operationsData(item, false);
		});
		deleteCM.setOnAction(event -> {
			ItemContent item = dataTable.getSelectionModel().getSelectedItem();
			operationsData(item, true);
			dataTable.getItems().remove(item);
			dataTable.refresh();
		});
		sectionsCM.setOnAction(event -> {
			ChooseCtrl choose = new ChooseCtrl();
			choose.load(node, "sections");
			Sections sections = (Sections) choose.show();
			if (sections != null) {
				dataTable.getItems().forEach(value -> {
					if (value.isSelected()) {
						Goods item = (Goods) value.getObject();
						item.setId_sections(sections.getId());
						item.save(getDbInSelectNode());
					}
				});
			}
		});
		templateCodesCM.setOnAction(event -> {
			ChooseCtrl choose = new ChooseCtrl();
			choose.load(node, "templateCodes");
			Codes code = (Codes) choose.show();
			if (code != null) {
				dataTable.getItems().forEach(itemContent -> {
					if (itemContent.isSelected()) {
						Goods item = (Goods) itemContent.getObject();
						item.setId_barcodes(code.getId());
						item.save(getDbInSelectNode());
					}
				});
			}
		});
		templatesCM.setOnAction(event -> {
			ChooseCtrl choose = new ChooseCtrl();
			choose.load(node, "templates");
			Templates template = (Templates) choose.show();
			if (template != null) {
				dataTable.getItems().forEach(value -> {
					if (value.isSelected()) {
						Goods item = (Goods) value.getObject();
						item.setId_templates(template.getId());
						item.save(getDbInSelectNode());
					}
				});
			}
		});
		stocksCM.setOnAction(event -> {
		});
	}

	public Button getSend() {
		return send;
	}

	public PackageSend getPack() {
		return pack;
	}

	private void setUpScrollBar() {
		ScrollBar scrollBar = Helper.getDataTableScrollBar(dataTable)
				.orElseThrow(() -> new NullPointerException("Was not able to obtain ScrollBar from TableView"));

		scrollBar.setOnMouseReleased(event -> {
			addMore = true;
			scrollBar.removeEventFilter(MouseEvent.MOUSE_DRAGGED, consumeMouseDragEvent);
		});

		// will work only if scroll using mouse wheel or touchpad but not by clicking on
		// scrollbar and dragging it
		dataTable.addEventFilter(ScrollEvent.SCROLL_FINISHED, event -> addMore = true);

		scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.doubleValue() >= scrollBar.getMax() && addMore && tableHasMore) {
				// stop mouse from moving scrollbar block until mouse release it
				scrollBar.addEventFilter(MouseEvent.ANY, consumeMouseDragEvent);
				if (showList.get(showList.size() - 1).getNumber() + STEP <= databaseTableSize) {
					loadTableContent();
				} else if (databaseTableSize - showList.get(showList.size() - 1).getNumber() < STEP) {
					loadTableContent();
					tableHasMore = false;
				} else {
					tableHasMore = false;
				}

			}

		});
	}

	/**
	 * Get database, if node level is 2 we will get db from MainCtrl, else convert
	 * node to the scaleItemMenu and get its db
	 * 
	 * @param node is item depending on which we will get db
	 * @return db or if not is not on level 2 and were not able to get it from
	 *         scaleItemMenu return null
	 */
	private Optional<MySQL> getDbDependingOnNode(NodeTree node) {
		if (node != null) {
			if (node.getLevel() == 2 && MainCtrl.getDB().isDBConnection()) {
				return Optional.of(MainCtrl.getDB());
			} else {
				ScaleItemMenu scale = (ScaleItemMenu) node.getUpObject().getObject();
				if (scale.getScale().getUpdate() >= 0 && scale.getDB().isDBConnection()) {
					return Optional.of(scale.getDB());
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Will get number of item is table so later we won't try to load more item than
	 * we have in table
	 */
	private void getNumberOfItemsInTable() {
		if (db != null) {
			SqlQueryBuilder builder = new SqlQueryBuilder(db, type.getTableName());
			tableHasMore = true;
			try {
				// select count(*) from tableName;
				ResultSet set = builder.select("").count("*").from(type.getTableName()).execute();
				// get number of items from result set
				while (set.next()) {
					databaseTableSize = set.getInt(1);
				}
				logger.info("Table size {}", databaseTableSize);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public MySQL getDbInSelectNode() {
		return getDbDependingOnNode(node).orElse(null);
	}

	public ObjectType getType() {
		return type;
	}

	private void loadTableContent() {

		offset = showList.get(showList.size() - 1).getNumber() - commonObjectsBetweenPages;
		int initialIndex = showList.get(showList.size() - 1).getNumber() + 1 - commonObjectsBetweenPages;
		showList.addAll(ItemContent.get(
				FXCollections.observableArrayList(
						Helper.getData(db, STEP, offset, type).orElseThrow(type::getNullPointerException)),
				initialIndex));
	}

	public void updateTableContent() {
		int initialIndex = showList.get(showList.size() - 1).getNumber() + 1 - commonObjectsBetweenPages;
		showList.setAll(
				ItemContent.get(
						FXCollections.observableArrayList(
								Helper.getData(db, STEP, 0, type).orElseThrow(type::getNullPointerException)),
						initialIndex));
	}

	public void setShowList(List<Object> list) {
		showList.setAll(ItemContent.get(FXCollections.observableArrayList(list)));
	}
}
