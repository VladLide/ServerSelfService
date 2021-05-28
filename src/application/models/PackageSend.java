package application.models;

import application.models.net.mysql.interface_tables.ScaleItemMenu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PackageSend {
	private ObservableList<ScaleItemMenu> connectSend = FXCollections.observableArrayList();
	private String type = "";
	private ObservableList<Object> items = FXCollections.observableArrayList();

	public PackageSend() {}

	public ObservableList<ScaleItemMenu> getConnectSend() {
		return connectSend;
	}
	public void setConnectSend(ObservableList<ScaleItemMenu> connectSend) {
		this.connectSend = connectSend;
	}
	public ObservableList<Object> getItems() {
		return items;
	}
	public void setItems(ObservableList<Object> items) {
		this.items = items;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
