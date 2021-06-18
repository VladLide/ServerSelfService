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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContentCtrl {
	private final ObservableList<ItemContent> showList = FXCollections.observableArrayList();
	private final Logger logger = LogManager.getLogger(ContentCtrl.class);
	private final EventHandler<MouseEvent> consumeMouseDragEvent = Event::consume;
	private static final int STEP = 200;
	//todo remove start and end variables
	private final int[] start = {0};
	private final int[] end = {STEP};
	private boolean addMore = true;
	private boolean beenDeleted = false;
	private boolean firstPass = true;
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
		start[0] = 0;
		end[0] = STEP;

		switch (node.getType()) {
			case "products": {
				type = ObjectType.PRODUCTS;
				db = getDbDependingOnNode(node).orElse(null);

				if (db != null) {
					showList.addAll(
							ItemContent.get(FXCollections.observableArrayList(
									Helper.getData(db, STEP, start[0], type)
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
									Helper.getData(db, STEP, start[0], type)
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
									Helper.getData(db, STEP, start[0], type)
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
									Helper.getData(db, STEP, start[0], type)
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
		if(showList.size()>0) {
			dataTable.getColumns().addAll(loadTable(ContentInfo.getInstance().columnsContent.get(node.getType())));
			dataTable.setItems(showList);
		}

		setUpScrollBar();
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
					mainWindowCtrl.openSection(
							(item != null) ? (Sections) item.getObject() : null,
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

	private void addAtTheBeginning(ScrollBar scrollBar) {
		if (showList.get(0).getNumber() - STEP >= 0) {
			showList.addAll(0, ItemContent
					.get(FXCollections.observableArrayList(
							Helper.getData(db, STEP, showList.get(0).getNumber() - STEP, type)
									.orElseThrow(type::getNullPointerException)),
							showList.get(0).getNumber() - STEP));
			double value = (1.0 / showList.size()) * STEP;
			logger.debug("Setting value to {}", value);
			scrollBar.setValue(value);
		} else {
			beenDeleted = false;
		}
	}

	private void addAtTheEnd(ScrollBar scrollBar) {
		double targetValue = scrollBar.getValue() * showList.size();

		showList.addAll(ItemContent
				.get(FXCollections.observableArrayList(
						Helper.getData(db, STEP, showList.get(showList.size() - 1).getNumber(), type)
								.orElseThrow(type::getNullPointerException)),
						showList.get(showList.size() - 1).getNumber() + 1));
		double value = targetValue / showList.size();
		logger.debug("Setting value {}", value);
		scrollBar.setValue(value);
	}

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
}

