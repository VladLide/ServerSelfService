package application.controllers.parts;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Converter;
import application.Helper;
import application.Marquee;
import application.controllers.MainCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.controllers.windows.ScaleCtrl;
import application.enums.ObjectType;
import application.enums.ScaleStatus;
import application.models.Configs;
import application.models.Utils;
import application.models.net.database.mysql.MySQL;
import application.models.net.database.mysql.interface_tables.ScaleItemMenu;
import application.models.net.database.mysql.tables.Codes;
import application.models.net.database.mysql.tables.Goods;
import application.models.net.database.mysql.tables.Scales;
import application.models.net.database.mysql.tables.Sections;
import application.models.net.database.mysql.tables.Templates;
import application.models.objectinfo.NodeTree;
import application.views.languages.uk.parts.SidebarInfo;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class SidebarCtrl {
	private ScaleCtrl scaleWindow = null;
	private AnchorPane sidebar;
	private NodeTree tempEdit = null;
	private static SidebarCtrl sidebarCtrl;
	private final Logger logger = LogManager.getLogger(SidebarCtrl.class);
	private Marquee marquee;
	private Thread labelThread;
	private TreeItem<NodeTree> selectedItem;
	private ScaleItemMenu sim = null;

	@FXML
	private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"), "part", "Sidebar");
	@FXML
	private URL location = getClass().getResource(Utils.getView("part", "Sidebar"));
	@FXML
	private TreeTableView<?> filter;
	@FXML
	private AnchorPane menuPane;
	@FXML
	private TreeView<NodeTree> menu;
	@FXML
	private AnchorPane filterPane;
	@FXML
	private AnchorPane status;
	@FXML
	private Label label;
	@FXML
	private MenuItem createCM;
	@FXML
	private MenuItem refreshCM;
	@FXML
	private MenuItem editCM;
	@FXML
	private MenuItem deleteCM;
	@FXML
	private ContextMenu sidebarContextMenu;
	@FXML
	private TextField searchBar;
	@FXML
	private Pane searchBarPane;

	public SidebarCtrl(AnchorPane anchorPane) {
		try {
			FXMLLoader loader = new FXMLLoader(location, resources);
			loader.setController(this);
			sidebar = MainCtrl.loadAnchorPane(anchorPane, loader);

			sidebarCtrl = this;
		} catch (IOException e) {
			sidebar = null;
			MainWindowCtrl.setLog(e.getMessage());
		}
	}

	public void loadSidebar(AnchorPane anchorPane) {
		if (sidebar != null) {
			anchorPane.getChildren().add(sidebar);
		} else {
			MainWindowCtrl.setLog(this.getClass().getName() + ": error null fxml");
		}
	}

	public void loadMenu(ObservableList<ScaleItemMenu> scales) {
		if (menu.getRoot() != null)
			menu.getRoot().getChildren().clear();

		TreeItem<NodeTree> root = new TreeItem<>(new NodeTree(SidebarInfo.root, "root", 0, "root"));
		root.setExpanded(true);
		SidebarInfo.menu.forEach(mainNode -> {
			TreeItem<NodeTree> main = new TreeItem<>(
					new NodeTree(mainNode[1], mainNode[0], 1, mainNode[0], root.getValue()));
			main.setExpanded(true);
			switch (mainNode[0]) {
			case "scales": {
				if (!scales.isEmpty()) {
					scales.forEach(scale -> {
						TreeItem<NodeTree> scaleNode = new TreeItem<>(
								new NodeTree(scale.getName() + "-" + scale.getId(), "ScaleInfo", 2, scale,
										main.getValue()),
								scale.getImg());
						SidebarInfo.menuScale.forEach(value -> {
							TreeItem<NodeTree> node = new TreeItem<>(
									new NodeTree(value[1], value[0], 3, value[0], scaleNode.getValue()));
							scaleNode.getChildren().add(node);
						});
						main.getChildren().add(scaleNode);
					});
				}
				break;
			}
			case "editors": {
				SidebarInfo.menuEditors.forEach(value -> {
					TreeItem<NodeTree> node = new TreeItem<>(
							new NodeTree(value[1], value[0], 2, value[0], main.getValue()));
					main.getChildren().add(node);
				});
				break;
			}
			default:
				throw new IllegalArgumentException("Wrong menu item");
			}
			root.getChildren().add(main);
		});
		menu.setRoot(root);
		menu.setShowRoot(false);

		setStatusToDefaultValue(scales);
	}

	public void addItemMenu(ScaleItemMenu scale) {
		TreeItem<NodeTree> main = menu.getRoot().getChildren().get(0);
		TreeItem<NodeTree> scaleNode = new TreeItem<>(
				new NodeTree(scale.getName() + "-" + scale.getId(), "ScaleInfo", 2, scale), scale.getImg());
		SidebarInfo.menuScale.forEach((value) -> {
			TreeItem<NodeTree> node = new TreeItem<NodeTree>(new NodeTree(value[1], "String", 3, value[0]));
			scaleNode.getChildren().add(node);
		});
		main.getChildren().add(scaleNode);
		menu.refresh();
	}

	public void updateItemMenu(Scales scale) {
		tempEdit.setName(scale.getName() + "-" + scale.getId());
		((ScaleItemMenu) tempEdit.getObject()).setScale(scale);
		menu.refresh();
	}

	public void openItemTree(NodeTree node) {
		switch (node.getLevel()) {
		case 1: {
			// openTableScales();
			break;
		}
		case 2: {
			// openTableInfoScale();
			// openTableRedactorDataServer
			MainWindowCtrl.getContentCtrl().showTableRedactorData(node);
			break;
		}
		case 3: {
			MainWindowCtrl.getContentCtrl().showTableRedactorData(node);
			// openTableRedactorDataScales();
			break;
		}
		default:
			break;
		}
	}

	@FXML
	void initialize() {
		assert filter != null : "fx:id=\"filter\" was not injected: check your FXML file 'Sidebar.fxml'.";
		assert menuPane != null : "fx:id=\"menuPane\" was not injected: check your FXML file 'Sidebar.fxml'.";
		assert menu != null : "fx:id=\"menu\" was not injected: check your FXML file 'Sidebar.fxml'.";
		assert filterPane != null : "fx:id=\"filterPane\" was not injected: check your FXML file 'Sidebar.fxml'.";
		assert status != null : "fx:id=\"status\" was not injected: check your FXML file 'Sidebar.fxml'.";
		menu.setOnMouseReleased(event -> {
			TreeItem<NodeTree> node = menu.getSelectionModel().getSelectedItem();
			if (node != null) {
				selectedItem = node;
				if (node.getValue().getObject().getClass().equals(ScaleItemMenu.class)) {
					setStatusToScaleStatus(node);
				} else if (node.getValue().getObject().equals(SidebarInfo.menu.get(0)[0])) {
					setStatusToDefaultValue(MainCtrl.getScales());
				} else if (node.getValue().getLevel() == 3) {
					TreeItem<NodeTree> parent = node.getParent();
					setStatusToScaleStatus(parent);
				}

				openItemTree(node.getValue());
				node = node.getParent();

				StringBuilder builder = new StringBuilder();
				builder.append(node.getValue().getName().toUpperCase());

				while (node.getValue().getLevel() != 0) {
					builder.insert(0, " > ").insert(0, node.getValue().getName().toUpperCase());

					node = node.getParent();
				}
				MainWindowCtrl.setURL(builder.toString());

			}
		});
		menu.setOnContextMenuRequested(event -> {
			NodeTree node = menu.getSelectionModel().getSelectedItem().getValue();
			if (node.getType().compareToIgnoreCase("ScaleInfo") != 0) {
				createCM.setDisable(node.getType().compareToIgnoreCase("scales") != 0);
				editCM.setDisable(true);
				deleteCM.setDisable(true);
				/*
				 * if(node.getType().compareToIgnoreCase("scales")!=0) {
				 * sidebarContextMenu.hide(); }
				 */
			} else {
				createCM.setDisable(false);
				editCM.setDisable(false);
				deleteCM.setDisable(false);
			}
		});
		createCM.setOnAction(event -> {
			scaleWindow = new ScaleCtrl(new Scales());
			scaleWindow.showStage();
			refresh();
		});
		refreshCM.setOnAction(event -> {
			refresh();
		});
		editCM.setOnAction(event -> {
			tempEdit = menu.getSelectionModel().getSelectedItem().getValue();
			ScaleItemMenu scaleInfo = (ScaleItemMenu) tempEdit.getObject();
			scaleInfo.setSaveToConnect(false);
			scaleWindow = new ScaleCtrl(scaleInfo.getScale());
			scaleWindow.setScaleInfo(scaleInfo);
			scaleWindow.showStage();
			refresh();
		});
		deleteCM.setOnAction(event -> {
			TreeItem<NodeTree> item = menu.getSelectionModel().getSelectedItem();
			// item.getChildren().clear();
			ScaleItemMenu scaleInfo = (ScaleItemMenu) item.getValue().getObject();
			scaleInfo.setSaveToConnect(false);
			if (scaleInfo.getScale().getUpdate() >= 0) {
				try {
					Connection dbConnection = scaleInfo.getDB().getDBConnection();
					if (dbConnection != null) {
						dbConnection.close();
					}
				} catch (SQLException e) {
					logger.error("Delete scale >> DB close", e);
				}
			}
			MainCtrl.getDB().delete("goods_load", "id_scales = " + scaleInfo.getScale().getId());
			scaleInfo.getScale().delete(MainCtrl.getDB());
			item.getParent().getChildren().remove(item);
			menu.refresh();
		});

		searchBar.prefWidthProperty().bind(searchBarPane.widthProperty());
		searchBar.setOnKeyPressed(this::search);
	}

	private void search(KeyEvent event) {
		String inputText = getTextFromSearchBar(event);
		MySQL db = MainWindowCtrl.getContentCtrl().getDbInSelectNode();
		ObjectType type;
		List<Object> searchResult = new ArrayList<>();
		if (db != null && ((type = MainWindowCtrl.getContentCtrl().getType()) != null)) {
			boolean isNumeric = Helper.isNumeric(inputText);
			String tableName = type.getTableName();
			String shortName = String.valueOf(tableName.charAt(0));
			String columnCode = "%column%";
			String orderByColumn = type.getOrderByColumn();
			// to finish request string you need to replace %column% with column
			String requestButNotFinished = "select * from %table% as %short% where %short%.%column% like '%like%%' order by %order%"
					.replace("%table%", type.getTableName()).replace("%short%", shortName).replace("%like%", inputText)
					.replace("%order%", orderByColumn);
			ResultSet select;
			String columnName = isNumeric ? "id" : "name";

			try {
				switch (type) {
				case PRODUCTS: {
					List<Goods> goods;
					columnName = isNumeric ? "code" : "name";
					String request = requestButNotFinished.replace(columnCode, columnName);

					select = db.getSelect(request);
					goods = Converter.fromResultSetToGoodsList(select);

					if (goods.isEmpty() && isNumeric) {
						columnName = "pre_code";
						request = requestButNotFinished.replace("%column%", columnName);
						select = db.getSelect(request);
						goods = Converter.fromResultSetToGoodsList(select);
					}

					searchResult = new ArrayList<>(goods);
					break;
				}
				case SECTIONS: {
					List<Sections> sections;
					String request = requestButNotFinished.replace(columnCode, columnName);

					select = db.getSelect(request);
					sections = Converter.fromResultSetToSectionsList(select);
					searchResult = new ArrayList<>(sections);
					break;
				}
				case TEMPLATES: {
					List<Templates> templates;
					String request = requestButNotFinished.replace(columnCode, columnName);

					select = db.getSelect(request);
					templates = Converter.fromResultSetToTemplatesList(select);
					searchResult = new ArrayList<>(templates);
					break;
				}
				case TEMPLATES_CODES: {
					List<Codes> codes;
					String request = requestButNotFinished.replace(columnCode, columnName);

					select = db.getSelect(request);
					codes = Converter.fromResultSetToCodesList(select);
					searchResult = new ArrayList<>(codes);
					break;
				}
				default:
					throw new IllegalArgumentException("Wrong type of object");
				}
			} catch (SQLException | IllegalArgumentException e) {
				logger.error(e.getMessage(), e);
			}

			MainWindowCtrl.getContentCtrl().setShowList(searchResult);
		}
	}

	private String getTextFromSearchBar(KeyEvent event) {
		String fromSearchBar = searchBar.getText();

		if (KeyCode.BACK_SPACE.equals(event.getCode()) && fromSearchBar.length() > 0) {
			return fromSearchBar.substring(0, fromSearchBar.length() - 1);
		} else if (KeyCode.BACK_SPACE.equals(event.getCode())) {
			return "";
		} else if (!KeyCode.ENTER.equals(event.getCode())) {
			return fromSearchBar + event.getText();
		} else {
			return fromSearchBar;
		}
	}

	public TreeView<NodeTree> getMenu() {
		return menu;
	}

	public void setStatus(String string) {
		if (labelThread != null)
			labelThread.interrupt();
		labelThread = new Thread(() -> {
			try {
				Platform.runLater(() -> {
					label.setText(string);
					if (marquee != null)
						marquee.stop();
				});
				label.setVisible(true);
				Thread.sleep(5000);
				label.setVisible(false);

				Platform.runLater(() -> {
					marquee = new Marquee(string);
					marquee.setColor("black");
					marquee.setStyle("-fx-font: 12 arial;");
					marquee.setBoundsFrom(status);
					marquee.moveDownBy(-7);
					marquee.setScrollDuration(18);

					status.getChildren().add(marquee);
					marquee.run();
				});

			} catch (InterruptedException e) {
				Platform.runLater(() -> {
					if (marquee != null)
						marquee.stop();
				});
			}
		});
		labelThread.start();

	}

	public void refresh() {
		loadMenu(MainCtrl.refreshScales());
		menu.refresh();
	}

	public static SidebarCtrl getInstance() {
		return sidebarCtrl;
	}

	/**
	 * Method will count number of scales overall, number of online scales and
	 * number of offline scales and output it using setStatus in such way
	 * "numberOfScales scale(s), numberOfOnlineScales online, numberOfOfflineScales
	 * offline"
	 *
	 * @param scales list which contains scales
	 */
	private void setStatusToDefaultValue(ObservableList<ScaleItemMenu> scales) {
		// update status line
		int numberOfScales = scales.size();
		long numberOfOnlineScales = scales.stream()
				.filter(scale -> ScaleStatus.PRODUCTS_AT_DATABASE_ARE_UP_TO_DATE.equals(scale.getStatus())).count();
		long numberOfOfflineScales = scales.stream()
				.filter(scale -> ScaleStatus.NO_CONNECTION.equals(scale.getStatus())).count();
		setStatus(String.format("%d scale(s), %d online, %d offline", numberOfScales, numberOfOnlineScales,
				numberOfOfflineScales));
	}

	/**
	 * Method will data from node and output it in such way "scaleName-scaleId -
	 * scaleStatus"
	 *
	 * @param node treeItem with node which contains ScaleItemMenu object
	 */
	private void setStatusToScaleStatus(TreeItem<NodeTree> node) {
		ScaleItemMenu scale = (ScaleItemMenu) node.getValue().getObject();
		sim = scale;
		setStatus(String.format("%s-%d - %s", scale.getName(), scale.getId(), scale.getStatus().getMessage()));
	}

	public static TreeItem<NodeTree> getSelectedItem() {
		return getInstance().selectedItem;
	}
	public static ScaleItemMenu getSIM() {
		return getInstance().sim;
	}
}
