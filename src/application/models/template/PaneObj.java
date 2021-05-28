package application.models.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;

public class PaneObj{
	private AnchorPane pane;
	private int id;
	private String name;
	private float widthContent;
	private float heightContent;
	private ObservableList<Item> Content;
	public PaneObj() {
		super();
		this.Content = FXCollections.observableArrayList();
	}
	public PaneObj(AnchorPane pane) {
		super();
		this.pane= pane;
		this.Content = FXCollections.observableArrayList();
	}
	public PaneObj(AnchorPane pane, float widthContent, float heightContent) {
		super();
		this.pane = pane;
		this.widthContent = widthContent;
		this.heightContent = heightContent;
		this.Content = FXCollections.observableArrayList();
	}
	public PaneObj(AnchorPane pane, float widthContent, float heightContent,  ObservableList<Item> content) {
		super();
		this.pane = pane;
		this.widthContent = widthContent;
		this.heightContent = heightContent;
		this.Content = content;
	}
	public AnchorPane getPane() {
		return this.pane;
	}
	public void setPane(AnchorPane pane) {
		this.pane = pane;
	}
	public float getWidthContent() {
		return this.widthContent;
	}
	public void setWidthContent(float widthContent) {
		this.widthContent = widthContent;
	}
	public float getHeightContent() {
		return this.heightContent;
	}
	public void setHeightContent(float heightContent) {
		this.heightContent = heightContent;
	}
	public ObservableList<Item> getItems() {
		return this.Content;
	}
	public Item getItem(int index) {
		return this.Content.get(index);
	}
	public void setItems(ObservableList<Item> content) {
		this.Content = content;
	}
	public void setItem(Item content) {
		this.Content.add(content);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void remove(int id) {
		this.Content.remove(id);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getWidth() {
		return this.widthContent/8;
	}
	public float getHeight() {
		return this.heightContent/8;
	}
}
