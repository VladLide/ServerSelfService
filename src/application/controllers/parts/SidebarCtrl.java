package application.controllers.parts;

import application.Marquee;
import application.ScaleStatus;
import application.controllers.MainCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.controllers.windows.ScaleCtrl;
import application.models.Configs;
import application.models.Utils;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Scales;
import application.models.objectinfo.NodeTree;
import application.views.languages.uk.parts.SidebarInfo;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SidebarCtrl {
	private ScaleCtrl scaleWindow = null;
	private AnchorPane sidebar;
	private NodeTree tempEdit = null;
	private static SidebarCtrl sidebarCtrl;
	private final Logger logger = LogManager.getLogger(SidebarCtrl.class);
	private Marquee marquee;
	private Thread labelThread;

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
					new NodeTree(
							mainNode[1],
							mainNode[0],
							1,
							mainNode[0],
							root.getValue()));
			main.setExpanded(true);
			switch (mainNode[0]) {
				case "scales": {
					if (!scales.isEmpty()) {
						scales.forEach(scale -> {
							TreeItem<NodeTree> scaleNode = new TreeItem<>(
									new NodeTree(
											scale.getName() + "-" + scale.getId(),
											"ScaleInfo",
											2,
											scale,
											main.getValue()),
									scale.getImg()
							);
							SidebarInfo.menuScale.forEach(value -> {
								TreeItem<NodeTree> node = new TreeItem<>(
										new NodeTree(
												value[1],
												value[0],
												3,
												value[0],
												scaleNode.getValue()));
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
								new NodeTree(
										value[1],
										value[0],
										2,
										value[0],
										main.getValue()));
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
				new NodeTree(
						scale.getName() + "-" + scale.getId(),
						"ScaleInfo",
						2,
						scale),
				scale.getImg());
		SidebarInfo.menuScale.forEach(value -> {
			TreeItem<NodeTree> node = new TreeItem<>(new NodeTree(value[1], "String", 3, value[0]));
			scaleNode.getChildren().add(node);
		});
		main.getChildren().add(scaleNode);
		menu.refresh();
	}

	public void updateItemMenu(Scales scale) {
		tempEdit.setName(scale.getName() + "-" + scale.getId());
		((ScaleItemMenu) tempEdit.getObject()).setScaleServer(scale);
		menu.refresh();
	}

	public void openItemTree(NodeTree node) {
		switch (node.getLevel()) {
			case 1: {
				break;
			}
			case 2:
			case 3: {
				MainWindowCtrl.getContentCtrl().showTableRedactorData(node);
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
				if (node.getValue().getObject().getClass().equals(ScaleItemMenu.class)) {
					setStatusToScaleStatus(node);
				} else if (node.getValue().getObject()
						.equals(SidebarInfo.menu.get(0)[0])) {
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
					builder.insert(0, " > ")
							.insert(0, node.getValue().getName().toUpperCase());

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
			} else {
				createCM.setDisable(false);
				editCM.setDisable(false);
				deleteCM.setDisable(false);
			}
		});
		createCM.setOnAction(event -> {
			scaleWindow = new ScaleCtrl(new Scales());
			scaleWindow.showStage();
		});
		refreshCM.setOnAction(event -> {
			loadMenu(MainCtrl.refreshScales());
			menu.refresh();
		});
		editCM.setOnAction(event -> {
			tempEdit = menu.getSelectionModel().getSelectedItem().getValue();
			ScaleItemMenu scaleInfo = (ScaleItemMenu) tempEdit.getObject();
			scaleInfo.setSaveToConnect(false);
			scaleWindow = new ScaleCtrl(scaleInfo.getScaleServer());
			scaleWindow.setScaleInfo(scaleInfo);
			scaleWindow.showStage();
		});
		deleteCM.setOnAction(event -> {
			TreeItem<NodeTree> item = menu.getSelectionModel().getSelectedItem();
			ScaleItemMenu scaleInfo = (ScaleItemMenu) item.getValue().getObject();
			scaleInfo.setSaveToConnect(false);
			if (scaleInfo.getScaleServer().getUpdate() >= 0) {
				try {
					scaleInfo.getDB().getDBConnection().close();
				} catch (SQLException e) {
					logger.error("Delete scale >> DB close", e);
				}
			}
			MainCtrl.getDB().delete("goods_load", "id_scales = " + scaleInfo.getScaleServer().getId());
			scaleInfo.getScaleServer().delete(MainCtrl.getDB());
			item.getParent().getChildren().remove(item);
			menu.refresh();
		});
	}

	public TreeView<NodeTree> getMenu() {
		return menu;
	}

	public void setStatus(String string) {
		if (labelThread != null) labelThread.interrupt();
		labelThread = new Thread(() -> {
			try {
				Platform.runLater(() -> {
					label.setText(string);
					if (marquee != null) marquee.stop();
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

	public static SidebarCtrl getInstance() {
		return sidebarCtrl;
	}

	/**
	 * Method will count number of scales overall, number of online scales and number of
	 * offline scales and output it using setStatus in such way
	 * "numberOfScales scale(s), numberOfOnlineScales online, numberOfOfflineScales offline"
	 *
	 * @param scales list which contains scales
	 */
	private void setStatusToDefaultValue(ObservableList<ScaleItemMenu> scales) {
		//update status line
		int numberOfScales = scales.size();
		long numberOfOnlineScales = scales
				.stream()
				.filter(scale ->
						ScaleStatus
								.PRODUCTS_AT_DATABASE_ARE_UP_TO_DATE
								.equals(scale.getStatus()))
				.count();
		long numberOfOfflineScales = scales
				.stream()
				.filter(scale ->
						ScaleStatus
								.NO_CONNECTION
								.equals(scale.getStatus()))
				.count();
		setStatus(String.format("%d scale(s), %d online, %d offline",
				numberOfScales,
				numberOfOnlineScales,
				numberOfOfflineScales));
	}

	/**
	 * Method will data from node and output it in such way
	 * "scaleName-scaleId - scaleStatus"
	 *
	 * @param node treeItem with node which contains ScaleItemMenu object
	 */
	private void setStatusToScaleStatus(TreeItem<NodeTree> node) {
		ScaleItemMenu scale = (ScaleItemMenu) node.getValue().getObject();
		setStatus(String.format(
				"%s-%d - %s",
				scale.getName(),
				scale.getId(),
				scale.getStatus().getMessage()
		));
	}
}
