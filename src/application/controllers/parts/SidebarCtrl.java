package application.controllers.parts;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.controllers.MainCtrl;
import application.controllers.windows.MainWindowCtrl;
import application.controllers.windows.ScaleCtrl;
import application.models.Configs;
import application.models.Utils;
import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Scales;
import application.models.objectinfo.NodeTree;
import application.views.languages.uk.parts.SidebarInfo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

public class SidebarCtrl {
	private ScaleCtrl scaleWindow = null;
	private AnchorPane sidebar;
	private NodeTree tempEdit = null;

    @FXML
    private ResourceBundle resources = Utils.getResource(Configs.getItemStr("language"),"part","Sidebar");
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
    private Label status;
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
            FXMLLoader loader = new FXMLLoader(location,resources);
            loader.setController(this);
            sidebar = MainCtrl.loadAnchorPane(anchorPane, loader);
        } catch (IOException e) {
        	sidebar = null;
        	MainWindowCtrl.setLog(e.getMessage());
        }
	}
	public void loadSidebar(AnchorPane anchorPane) {
		if(sidebar!=null) {
			anchorPane.getChildren().add(sidebar);
		}else {
			MainWindowCtrl.setLog(this.getClass().getName()+": error null fxml");
		}
	}

	public void loadMenu(ObservableList<ScaleItemMenu> scales) {
		if(menu.getRoot() != null)
			menu.getRoot().getChildren().clear();

		TreeItem<NodeTree> root = new TreeItem<>(new NodeTree(SidebarInfo.root,"root",0,"root"));
		root.setExpanded(true);
		SidebarInfo.menu.forEach(mainNode -> {
			TreeItem<NodeTree> main = new TreeItem<>(new NodeTree(mainNode[1],mainNode[0],1,mainNode[0],root.getValue()));
			main.setExpanded(true);
			switch(mainNode[0]) {
				case "scales":{
					if(!scales.isEmpty()) {
						scales.forEach(scale->{
							TreeItem<NodeTree> scaleNode = new TreeItem<NodeTree>(
									new NodeTree(
											scale.getName() + "-" + scale.getId(),
											"ScaleInfo",
											2,
											scale,
											main.getValue()),
									scale.getImg()
							);
							//scaleNode.setExpanded(true);
							SidebarInfo.menuScale.forEach((value)->{
								TreeItem<NodeTree> node = new TreeItem<NodeTree>(
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
				case "editors":{
					SidebarInfo.menuEditors.forEach((value)->{
						TreeItem<NodeTree> node = new TreeItem<NodeTree> (new NodeTree(value[1],value[0],2,value[0],main.getValue()));
						main.getChildren().add(node);
					});
					break;
				}
			}
			root.getChildren().add(main);
		});
		menu.setRoot(root);
		menu.setShowRoot(false);
		//menu.setContextMenu(arg0);
	}
	public void addItemMenu(ScaleItemMenu scale) {
		TreeItem<NodeTree> main = menu.getRoot().getChildren().get(0);
		TreeItem<NodeTree> scaleNode = new TreeItem<NodeTree>(
				new NodeTree(
						scale.getName()+"-"+scale.getId(),
						"ScaleInfo",
						2,
						scale),
				scale.getImg());
		SidebarInfo.menuScale.forEach((value)->{
			TreeItem<NodeTree> node = new TreeItem<NodeTree> (new NodeTree(value[1],"String",3,value[0]));
			scaleNode.getChildren().add(node);
		});
		main.getChildren().add(scaleNode);
		menu.refresh();
	}
	public void updateItemMenu(Scales scale) {
		tempEdit.setName(scale.getName()+"-"+scale.getId());
		((ScaleItemMenu)tempEdit.getObject()).setScaleServer(scale);
		menu.refresh();
	}
	public void openItemTree(NodeTree node) {
		switch (node.getLevel()) {
		case 1:{
			//openTableScales();
			break;
		}
		case 2:{
			//openTableInfoScale();
			//openTableRedactorDataServer
			MainWindowCtrl.getContentCtrl().showTableRedactorData(node);
			break;
		}
		case 3:{
			MainWindowCtrl.getContentCtrl().showTableRedactorData(node);
			//openTableRedactorDataScales();
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
        menu.setOnMouseReleased(event->{
        	TreeItem<NodeTree> node = menu.getSelectionModel().getSelectedItem();
        	if(node!=null) {
        		openItemTree(node.getValue());
        		String path = node.getValue().getName().toUpperCase();
	        	node = node.getParent();
	        	while(node.getValue().getLevel() != 0){
	        		path = node.getValue().getName().toUpperCase() + " > " + path;
	                node = node.getParent();
	            }
	        	MainWindowCtrl.setURL(path);
        	}
        });
        menu.setOnContextMenuRequested(event->{
        	NodeTree node = menu.getSelectionModel().getSelectedItem().getValue();
        	if(node.getType().compareToIgnoreCase("ScaleInfo")!=0) {
        		createCM.setDisable(node.getType().compareToIgnoreCase("scales")!=0);
    			editCM.setDisable(true);
    			deleteCM.setDisable(true);
        		/*if(node.getType().compareToIgnoreCase("scales")!=0) {
        			sidebarContextMenu.hide();
        		}*/
        	}else {
    			createCM.setDisable(false);
    			editCM.setDisable(false);
    			deleteCM.setDisable(false);
        	}
        });
        createCM.setOnAction(event->{
        	scaleWindow = new ScaleCtrl(new Scales());
        	scaleWindow.showStage();
        });
        refreshCM.setOnAction(event->{
        	loadMenu(MainCtrl.refreshScales());
        	menu.refresh();
        });
        editCM.setOnAction(event->{
        	tempEdit = menu.getSelectionModel().getSelectedItem().getValue();
        	ScaleItemMenu scaleInfo = (ScaleItemMenu)tempEdit.getObject();
        	scaleInfo.setSaveToConnect(false);
        	scaleWindow = new ScaleCtrl(scaleInfo.getScaleServer());
        	scaleWindow.setScaleInfo(scaleInfo);
        	scaleWindow.showStage();
        });
        deleteCM.setOnAction(event->{
        	TreeItem<NodeTree> item = menu.getSelectionModel().getSelectedItem();
        	//item.getChildren().clear();
        	ScaleItemMenu scaleInfo = (ScaleItemMenu)item.getValue().getObject();
        	scaleInfo.setSaveToConnect(false);
        	if(scaleInfo.getScaleServer().getUpdate()>=0){
        		try {
					scaleInfo.getDB().getDBConnection().close();
				} catch (SQLException e) {
					System.out.println("Delete scale >> DB close: "+e.getMessage());
				}
        	}
			MainCtrl.getDB().delete("goods_load", "id_scales = "+scaleInfo.getScaleServer().getId());
        	scaleInfo.getScaleServer().delete(MainCtrl.getDB());
        	item.getParent().getChildren().remove(item);
        	menu.refresh();
        });
    }
	public TreeView<NodeTree> getMenu() {
		return menu;
	}
}
