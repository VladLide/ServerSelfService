package application.models.objectinfo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemTemplate {
	private int id;
	private String name;
	private String type;
	private String value;
	private String description;

	public ItemTemplate(int id, String[] value) {
		this.id = id;
		this.name = value[1];
		this.type = value[0];
		this.value = value[2];
	}

	public static ObservableList<ItemTemplate> getList(ObservableList<String[]> arr) {
		ObservableList<ItemTemplate> row = FXCollections.observableArrayList();
		AtomicInteger i = new AtomicInteger(0);
		arr.forEach(v -> row.add(new ItemTemplate(i.getAndIncrement(), v)));
		return row;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getIndex() {
		return id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
