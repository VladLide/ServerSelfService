package application.controllers.pages;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.windows.SectionCtrl;
import application.models.EditingCell;
import application.models.Info2Col;
import application.models.Utils;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Scales;
import application.models.net.mysql.tables.SectionsPLU;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class ConnectSectionPLU {
    private Admin parentControler;
    private Scales scale;

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location = getClass().getResource(Utils.getView("fxml", "ConnectSectionPLU"));
    @FXML
    private TableColumn<SectionsPLU, Integer> numSServer;
    @FXML
    private TableColumn<SectionsPLU, ComboBox<String>> nameSServer;
    @FXML
    private TableColumn<SectionsPLU, SectionCtrl> sectionsScale;
    @FXML
    private TableColumn<SectionsPLU, SectionCtrl> sectionsServer;
    @FXML
    private TableColumn<SectionsPLU, ComboBox<String>> nameSScale;
    @FXML
    private TableColumn<SectionsPLU, Integer> numSScale;
    @FXML
    private TableColumn<SectionsPLU, Goods> plu;
    @FXML
    private TableView<SectionsPLU> tableView;
    @FXML
    private TableColumn<SectionsPLU, String> namePLU;
    @FXML
    private TableColumn<SectionsPLU, Integer> codePLU;
    @FXML
    private TableColumn<SectionsPLU, CheckBox> checkBoxSection;
    @FXML
    private CheckBox allCheckBox;
    @FXML
    private Button close;
    @FXML
    private Button send;

    public ConnectSectionPLU(AnchorPane Panel, Admin parentControler, Scales scale) {
        this.parentControler = parentControler;
        this.scale = scale;
        try {
            FXMLLoader loader = new FXMLLoader(location);
            loader.setController(this);
            BorderPane sectionPLU = loader.load();
            Panel.getChildren().clear();
            Panel.getChildren().add(sectionPLU);
            AnchorPane.setBottomAnchor(sectionPLU, 0.0);
            AnchorPane.setTopAnchor(sectionPLU, 0.0);
            AnchorPane.setLeftAnchor(sectionPLU, 0.0);
            AnchorPane.setRightAnchor(sectionPLU, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void FXMLLoad(AnchorPane Panel, Scales scale) {
        this.scale = scale;
        try {
            FXMLLoader loader = new FXMLLoader(location);
            loader.setController(this);
            BorderPane sectionPLU = loader.load();
            Panel.getChildren().clear();
            Panel.getChildren().add(sectionPLU);
            AnchorPane.setBottomAnchor(sectionPLU, 0.0);
            AnchorPane.setTopAnchor(sectionPLU, 0.0);
            AnchorPane.setLeftAnchor(sectionPLU, 0.0);
            AnchorPane.setRightAnchor(sectionPLU, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void load() {
    	ObservableList<SectionsPLU> arr = new SectionsPLU().getList(0, 0, this, false);
    	checkBoxSection.setCellValueFactory(new PropertyValueFactory<SectionsPLU, CheckBox>("checkBox"));
    	nameSServer.setCellValueFactory(new PropertyValueFactory<SectionsPLU, ComboBox<String>>("nameSSer"));
    	numSServer.setCellValueFactory(new PropertyValueFactory<SectionsPLU, Integer>("numberSer"));
    	nameSScale.setCellValueFactory(new PropertyValueFactory<SectionsPLU, ComboBox<String>>("nameSSca"));
    	numSScale.setCellValueFactory(new PropertyValueFactory<SectionsPLU, Integer>("numberSca"));
    	namePLU.setCellValueFactory(new PropertyValueFactory<SectionsPLU, String>("namePlU"));
    	codePLU.setCellValueFactory(new PropertyValueFactory<SectionsPLU, Integer>("codePLU"));
	    tableView.setItems(arr);
    }
    
    @FXML
    void initialize() {
    	load();
		allCheckBox.setOnAction(event->{
			if(allCheckBox.isSelected()) {
				tableView.getItems().forEach((val)->{
					val.getCheckBox().setSelected(true);
				});
				tableView.refresh();
			}else{
				tableView.getItems().forEach((val)->{
					val.getCheckBox().setSelected(false);
				});
				tableView.refresh();
			}
		});
		send.setOnAction(event->{
			tableView.getItems().forEach((val)->{
				if(val.getCheckBox().isSelected()) {
					val.setId_sections(val.getNumberSer());
					val.getNameSSca().setValue(val.getNameSSer().getValue());
					val.setNumberSca(val.getNumberSer());
					val.save(scale.getDb());
				}
			});
	    	/*tableView.getItems().clear();
			tableView.refresh();*/
		});
    	close.setOnAction(event->{
	    	tableView.getItems().clear();
			this.parentControler.show();
			this.parentControler = null;
			this.scale = null;
		});
    }
	public Scales getScale() {
		return scale;
	}
	public void setScale(Scales scale) {
		this.scale = scale;
	}
	public ResourceBundle getResources() {
		return resources;
	}
	public void setResources(ResourceBundle resources) {
		this.resources = resources;
	}
	public URL getLocation() {
		return location;
	}
	public void setLocation(URL location) {
		this.location = location;
	}
	public TableColumn<SectionsPLU, Integer> getNumSServer() {
		return numSServer;
	}
	public void setNumSServer(TableColumn<SectionsPLU, Integer> numSServer) {
		this.numSServer = numSServer;
	}
	public TableColumn<SectionsPLU, ComboBox<String>> getNameSServer() {
		return nameSServer;
	}
	public void setNameSServer(TableColumn<SectionsPLU, ComboBox<String>> nameSServer) {
		this.nameSServer = nameSServer;
	}
	public TableColumn<SectionsPLU, SectionCtrl> getSectionsScale() {
		return sectionsScale;
	}
	public void setSectionsScale(TableColumn<SectionsPLU, SectionCtrl> sectionsScale) {
		this.sectionsScale = sectionsScale;
	}
	public TableColumn<SectionsPLU, SectionCtrl> getSectionsServer() {
		return sectionsServer;
	}
	public void setSectionsServer(TableColumn<SectionsPLU, SectionCtrl> sectionsServer) {
		this.sectionsServer = sectionsServer;
	}
	public TableColumn<SectionsPLU, ComboBox<String>> getNameSScale() {
		return nameSScale;
	}
	public void setNameSScale(TableColumn<SectionsPLU, ComboBox<String>> nameSScale) {
		this.nameSScale = nameSScale;
	}
	public TableColumn<SectionsPLU, Integer> getNumSScale() {
		return numSScale;
	}
	public void setNumSScale(TableColumn<SectionsPLU, Integer> numSScale) {
		this.numSScale = numSScale;
	}
	public TableColumn<SectionsPLU, Goods> getPlu() {
		return plu;
	}
	public void setPlu(TableColumn<SectionsPLU, Goods> plu) {
		this.plu = plu;
	}
	public TableView<SectionsPLU> getTableView() {
		return tableView;
	}
	public void setTableView(TableView<SectionsPLU> tableView) {
		this.tableView = tableView;
	}
	public TableColumn<SectionsPLU, String> getNamePLU() {
		return namePLU;
	}
	public void setNamePLU(TableColumn<SectionsPLU, String> namePLU) {
		this.namePLU = namePLU;
	}
	public TableColumn<SectionsPLU, Integer> getCodePLU() {
		return codePLU;
	}
	public void setCodePLU(TableColumn<SectionsPLU, Integer> codePLU) {
		this.codePLU = codePLU;
	}
	public TableColumn<SectionsPLU, CheckBox> getCheckBoxSection() {
		return checkBoxSection;
	}
	public void setCheckBoxSection(TableColumn<SectionsPLU, CheckBox> checkBoxSection) {
		this.checkBoxSection = checkBoxSection;
	}
	public CheckBox getAllCheckBox() {
		return allCheckBox;
	}
	public void setAllCheckBox(CheckBox allCheckBox) {
		this.allCheckBox = allCheckBox;
	}
	public Button getClose() {
		return close;
	}
	public void setClose(Button close) {
		this.close = close;
	}
	public Button getSend() {
		return send;
	}
	public void setSend(Button send) {
		this.send = send;
	}
	public Admin getParentControler() {
		return parentControler;
	}
}
