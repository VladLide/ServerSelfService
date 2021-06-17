package application.models.objectinfo;

import application.models.net.mysql.interface_tables.ScaleItemMenu;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Templates;
import application.views.languages.uk.windows.ProductInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemContent {
	private int number = 0;
	private CheckBox checkBox = null;
	private int type = 0;
	private Object object = null;
	private int id = 0;
	private int code = 0;
	private String name = "";
	private String full_name = "";
	private String name_s = "";
	private String name_t = "";
	private String name_b = "";
	private float price = 0;
	private int before_validity = 0;
	private String ingredients = "";
	private LocalDateTime date = null;

	public ItemContent() {
		checkBox = new CheckBox();
	}

	public static ObservableList<ItemContent> get(ObservableList<Object> array) {
		return get(array,1);
	}

	public static ObservableList<ItemContent> get(ObservableList<Object> array, int initialIndex) {
		AtomicInteger i = new AtomicInteger(initialIndex);
		ObservableList<ItemContent> result = FXCollections.observableArrayList();
		array.forEach(value -> {
			ItemContent item = new ItemContent();
			item.setObject(value);
			item.setNumber(i.getAndIncrement());
			switch (item.getTypeOdject()) {
				case "Sections": {
					Sections val = (Sections) item.getObject();
					item.setId(val.getId());
					item.setName(val.getName());
					item.setName_s(val.getNumber_s() + "");
					item.setName_t(val.getNumber_po() + "");
					item.setIngredients(val.getDescription());
					break;
				}
				case "Templates": {
					Templates val = (Templates) item.getObject();
					item.setId(val.getId());
					item.setName(val.getName());
					item.setIngredients(val.getDescription());
					break;
				}
				case "Codes": {
					Codes val = (Codes) item.getObject();
					item.setId(val.getId());
					item.setName(val.getName());
					item.setIngredients(val.getMask());
					break;
				}
				case "Goods": {
					Goods val = (Goods) item.getObject();
					item.setId(val.getNumber());
					item.setCode(val.getPre_code());
					item.setName(val.getName());
					item.setPrice(val.getPrice());
					item.setType(val.getType());
					break;
				}
			}
			result.add(item);
		});
		return result;
	}

	public static ObservableList<ItemContent> getCheckSend(ObservableList<ScaleItemMenu> arr) {
		ObservableList<ItemContent> result = FXCollections.observableArrayList();
		AtomicInteger i = new AtomicInteger(1);
		arr.forEach((v) -> {
			ItemContent item = new ItemContent();
			item.setObject(v);
			item.setNumber(i.getAndIncrement());
			item.setId(v.getId());
			item.setName(v.getName());
			result.add(item);
		});
		return result;
	}

	public String getTypeOdject() {
		String[] type = object.getClass().getTypeName().replace(".", " ").split(" ");
		return type[type.length - 1];
	}

	public boolean getCompareOdject(String type) {
		String[] typeObj = object.getClass().getTypeName().replace(".", " ").split(" ");
		return typeObj[typeObj.length - 1].compareToIgnoreCase(type) == 0;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isSelected() {
		return checkBox.isSelected();
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(Boolean select) {
		this.checkBox.setSelected(select);
	}

	public String getType() {
		return ProductInfo.unit.get(type);
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getName_s() {
		return name_s;
	}

	public void setName_s(String name_s) {
		this.name_s = name_s;
	}

	public String getName_t() {
		return name_t;
	}

	public void setName_t(String name_t) {
		this.name_t = name_t;
	}

	public String getName_b() {
		return name_b;
	}

	public void setName_b(String name_b) {
		this.name_b = name_b;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getBefore_validity() {
		return before_validity;
	}

	public void setBefore_validity(int before_validity) {
		this.before_validity = before_validity;
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
