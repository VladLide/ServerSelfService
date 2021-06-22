package application.controllers.parts;

import application.Helper;
import application.controllers.MainCtrl;
import application.controllers.windows.ChooseCtrl;
import application.controllers.windows.ChooseSendCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.enums.*;
import application.models.Configs;
import application.models.PackageSend;
import application.models.Utils;
import application.models.net.mysql.MySQL;
import application.models.net.mysql.SqlQueryBuilder;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Templates;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContentCtrl {
	private final ObservableList<ItemContent> showList = FXCollections.observableArrayList();
	private final Logger logger = LogManager.getLogger(ContentCtrl.class);
	private final EventHandler<MouseEvent> consumeMouseDragEvent = Event::consume;
	private static final int commonObjectsBetweenPages = 5;
	private static final int STEP = 200;
	private int databaseTableSize;
	private boolean addMore = true;
	private boolean beenDeleted = false;
	private boolean firstPass = true;
	private boolean tableHasMore = true;
	private AnchorPane content;
	private NodeTree node = null;
	private PackageSend pack = null;
	private MySQL db;
	private ObjectType type;

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

		switch (node.getType()) {
			case "products": {
				type = ObjectType.PRODUCTS;
				db = getDbDependingOnNode(node).orElse(null);

				if (db != null) {
					showList.addAll(
							ItemContent.get(FXCollections.observableArrayList(
									Helper.getData(db, STEP, 0, type)
											.orElseThrow(type::getNullPointerException)
									)));
				}
				break;
			}
			case "sections": {
				type = ObjectType.SECTIONS;
				db = getDbDependingOnNode(node).orElse(null);

				if (db != null) {
					showList.addAll(
							ItemContent.get(FXCollections.observableArrayList(
									Helper.getData(db, STEP, 0, type)
											.orElseThrow(type::getNullPointerException)
							)));
				}
				break;
			}
			case "templates": {
				type = ObjectType.TEMPLATES;
				db = getDbDependingOnNode(node).orElse(null);

				if (db != null) {
					showList.addAll(
							ItemContent.get(FXCollections.observableArrayList(
									Helper.getData(db, STEP, 0, type)
											.orElseThrow(type::getNullPointerException)
							)));
				}

				break;
			}
			case "templateCodes": {
				type = ObjectType.TEMPLATES_CODES;
				db = getDbDependingOnNode(node).orElse(null);
				if (db != null) {
					showList.addAll(
							ItemContent.get(FXCollections.observableArrayList(
									Helper.getData(db, STEP, 0, type)
											.orElseThrow(type::getNullPointerException)
							)));
				}

				break;
			}
			case "settings": {
				break;
			}
			default:
				break;
		}

		dataTable.getColumns().addAll(loadTable(ContentInfo.getInstance().columnsContent.get(node.getType())));
		//if we have any data from database load it to the table
		if(!showList.isEmpty()) {
			dataTable.setItems(showList);
		}

		setUpScrollBar();
		getNumberOfItemsInTable();
	}

	public void operationsData(ItemContent item, boolean del) {
		MainWindowCtrl mainWindowCtrl = MainWindowCtrl.getInstance();
		String source = "";
		ScaleItemMenu sim = null;

		if (node.getLevel() == 2) {
			source = "Server (localhost)";
		} else {
			sim = (ScaleItemMenu) node.getUpObject().getObject();
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
				} else {
					mainWindowCtrl.openPlu(
							(item != null) ? (Goods) item.getObject() : null,
							source,
							db,
							ipAddress,
							placeType
					);
				}
				break;
			}
			case "sections": {
				if (item != null && del) {
					Sections sections = (Sections) item.getObject();
					sections.delete(false, db);

					// logging context menu deletion of section
					MainWindowCtrl.setLog(
							Helper.formatOutput(
									Operation.DELETE,
									placeType,
									ipAddress,
									SectionType.SECTION,
									sections.getName(),
									LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
									OperationStatus.SUCCESS
							)
					);
				} else {
					Sections sectionsWithImage;
					if (item == null) {
						sectionsWithImage = null;
					} else {
						sectionsWithImage = Sections.get(item.getId(), 0, -1, "", true, db);
					}
					mainWindowCtrl.openSection(
							sectionsWithImage,
							source,
							db,
							ipAddress,
							placeType
					);
				}
				break;
			}
			case "templates": {
				if (item != null && del) {
					Templates templates = (Templates) item.getObject();
					templates.delete(db);

					// logging context menu deletion of template
					MainWindowCtrl.setLog(
							Helper.formatOutput(
									Operation.DELETE,
									placeType,
									ipAddress,
									SectionType.TEMPLATE,
									templates.getName(),
									LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
									OperationStatus.SUCCESS
							)
					);
				} else {
					mainWindowCtrl.openTemplate(
							(item != null) ? (Templates) item.getObject() : null,
							db,
							ipAddress,
							placeType);
				}
				break;
			}
			case "templateCodes": {
				if (item != null && del) {
					Codes codes = (Codes) item.getObject();
					codes.delete(db);

					// logging context menu deletion of templateCodes
					MainWindowCtrl.setLog(
							Helper.formatOutput(
									Operation.DELETE,
									placeType,
									ipAddress,
									SectionType.CODE,
									codes.getName(),
									LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
									OperationStatus.SUCCESS
							)
					);
				} else {
					mainWindowCtrl.openCode(
							(item != null) ? (Codes) item.getObject() : null,
							source,
							db,
							ipAddress,
							placeType);
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
		delete.setOnAction(event -> dataTable.getItems()
				.stream()
				.filter(itemContent -> itemContent.getCheckBox().isSelected())
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
						item.save(MainCtrl.getDB());
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
						item.save(MainCtrl.getDB());
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
						item.save(MainCtrl.getDB());
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

	/**
	 * Set up scrollbar functions
	 * On scrollbar reaching end current items will be deleted and new added to the table, position of block
	 * on scroll bar will be set to 0
	 * If any items were deleted we can load them if scrollbar block reach 0.05 of scrollbar length and it
	 * moved further than 0.1, before loading old items new one will be deleted
	 */
	private void setUpScrollBar() {
		int numberOfItemsToContain = STEP;
		ScrollBar scrollBar = Helper.getDataTableScrollBar(dataTable)
				.orElseThrow(() -> new NullPointerException("Was not able to obtain ScrollBar from TableView"));

		scrollBar.setOnMouseReleased(event -> {
			addMore = true;
			scrollBar.removeEventFilter(MouseEvent.MOUSE_DRAGGED, consumeMouseDragEvent);
		});

		//will work only if scroll using mouse wheel or touchpad but not by clicking on scrollbar and dragging it
		dataTable.addEventFilter(ScrollEvent.ANY, event -> addMore = true);

		scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
			double position = scrollBar.getValue();

			/*
			if block is at the and addMore and table has more -> add more items
			else if block is at the beginning and addMore and we have scrolled to the end before and we have scrolled further
				than 10% -> delete current items and add items which were before them
			else if block is further than 10% and addMore and we have scrolled to the end before -> we can now add
				item that were before
			 */
			if (position == scrollBar.getMax() && addMore && tableHasMore) {
				//stop mouse from moving scrollbar block until mouse release it
				scrollBar.addEventFilter(MouseEvent.MOUSE_DRAGGED, consumeMouseDragEvent);

				addAtTheEnd(scrollBar);

				if (showList.size() > numberOfItemsToContain) {
					firstPass = true;
					beenDeleted = true;
					showList.remove(0, showList.size() - numberOfItemsToContain);
					scrollBar.setValue(0);
				}

				addMore = false;
			} else if (position <= 0.05 && addMore && beenDeleted && !firstPass) {
				scrollBar.addEventFilter(MouseEvent.MOUSE_DRAGGED, consumeMouseDragEvent);

				addAtTheBeginning(scrollBar);

				if (showList.size() > numberOfItemsToContain) {
					showList.remove(numberOfItemsToContain, showList.size());
					scrollBar.setValue(0.99);//if set to 1.0 it will trigger adding items at the end
				}

			} else if (position >= 0.1 && addMore && beenDeleted) {
				firstPass = false;
			}
		});
	}

	/**
	 * Will add items which were on before we added new
	 * or if showList.get(0).getNumber() - STEP + commonObjectsBetweenPages < 0 don't add any
	 * @param scrollBar object at which we will set new block position
	 */
	private void addAtTheBeginning(ScrollBar scrollBar) {
		if (showList.get(0).getNumber() - STEP + commonObjectsBetweenPages >= 0) {
			int limit = STEP;
			int offset = showList.get(0).getNumber() - STEP + commonObjectsBetweenPages;
			int initialIndex = showList.get(0).getNumber() - STEP + commonObjectsBetweenPages;
			showList.addAll(0, ItemContent
					.get(FXCollections.observableArrayList(
							Helper.getData(db, limit, offset, type).orElseThrow(type::getNullPointerException)),
							initialIndex));
			double value = (1.0 / showList.size()) * STEP;
			logger.debug("Setting value to {}", value);
			scrollBar.setValue(value);
		} else {
			beenDeleted = false;
		}
	}

	/**
	 * Will add new items
	 * or if there are no items to add don't add any
	 * @param scrollBar object at which we will set new block position
	 */
	private void addAtTheEnd(ScrollBar scrollBar) {
		if (showList.get(showList.size() - 1).getNumber() + STEP <= databaseTableSize
				|| databaseTableSize - showList.get(showList.size() - 1).getNumber() < STEP) {
			double targetValue = scrollBar.getValue() * showList.size();
			int limit = STEP;
			int offset = showList.get(showList.size() - 1).getNumber() - commonObjectsBetweenPages;
			int initialIndex = showList.get(showList.size() - 1).getNumber() + 1 - commonObjectsBetweenPages;

			showList.addAll(ItemContent
					.get(FXCollections.observableArrayList(
							Helper.getData(db, limit, offset, type).orElseThrow(type::getNullPointerException)),
							initialIndex));
			//we are setting value, but later after deletion items we reset it to another, so this part at this time
			// doesn't do anything but, if we load more than delete we will need this
			double value = targetValue / showList.size();
			scrollBar.setValue(value);
		} else {
			tableHasMore = false;
		}
	}

	/**
	 * Get database, if node level is 2 we will get db from MainCtrl,
	 * else convert node to the scaleItemMenu and get its db
	 * @param node is item depending on which we will get db
	 * @return db or if not is not on level 2 and were not able to get it from scaleItemMenu return null
	 */
	private Optional<MySQL> getDbDependingOnNode(NodeTree node) {
		if (node.getLevel() == 2 && MainCtrl.getDB().isDBConnection()) {
			return Optional.of(MainCtrl.getDB());
		} else {
			ScaleItemMenu scale = (ScaleItemMenu) node.getUpObject().getObject();
			if (scale.getScale().getUpdate() >= 0 && scale.getDB().isDBConnection()) {
				return Optional.of(scale.getDB());
			}
		}

		return Optional.empty();
	}

	/**
	 * Will get number of item is table so later we won't try to load more item than we have in table
	 */
	private void getNumberOfItemsInTable() {
		if (db != null) {
			SqlQueryBuilder builder = new SqlQueryBuilder(db);
			tableHasMore = true;
			try {
				//select count(*) from tableName;
				ResultSet set = builder.select("").count("*").from(type.getTableName()).execute();
				//get number of items from result set
				while (set.next()) {
					databaseTableSize = set.getInt(1);
				}
				logger.info("Table size {}", databaseTableSize);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}

