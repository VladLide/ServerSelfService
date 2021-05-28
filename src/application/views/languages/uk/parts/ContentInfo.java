package application.views.languages.uk.parts;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContentInfo {
	public static ContentInfo inst = null;
	public Map<String, ObservableList<String[]>> columnsContent = new HashMap<String, ObservableList<String[]>>();
	public static ObservableList<String[]> menu = FXCollections.observableArrayList(new String[] {"scales","Ваги"}, new String[] {"editors","Редактори даних"});
	public static ObservableList<String[]> menuScale = FXCollections.observableArrayList(new String[] {"products","Товари"}, new String[] {"stocks","Знижки"}, new String[] {"sections","Категорії"},
														new String[] {"templates","Шаблони етикетки"}, new String[] {"templateCodes","Шаблони коду"}, new String[] {"telegrams","Повідомлення"}, 
														new String[] {"users","Користувачі"}, new String[] {"settings","Налаштування"});
	public static ObservableList<String[]> menuEditors = FXCollections.observableArrayList(new String[] {"products","Товари"}, new String[] {"stocks","Знижки"}, new String[] {"sections","Категорії"},
														new String[] {"templates","Шаблони етикетки"}, new String[] {"templateCodes","Шаблони кодів"});
	
	public ContentInfo() {
		super();
		columnsContent.put("products", FXCollections.observableArrayList(new String[] {"CheckBox","","checkBox"}, new String[] {"Integer","№","number"}, new String[] {"Integer","Код товару","code"}, 
				new String[] {"Integer","Номер кнопки","id"}, new String[] {"String","Назва","name"},new String[] {"String","Тип","full_name"},new String[] {"String","Ціна","price"}));
		columnsContent.put("sections", FXCollections.observableArrayList(new String[] {"CheckBox","","checkBox"}, new String[] {"Integer","№","number"}, new String[] {"Integer","Номер","id"}, new String[] {"String","Назва","name"},
				new String[] {"String","З","name_s"}, new String[] {"String","До","name_t"}, new String[] {"String","Опис","ingredients"}));
		columnsContent.put("templates", FXCollections.observableArrayList(new String[] {"CheckBox","","checkBox"}, new String[] {"Integer","№","number"}, new String[] {"Integer","Номер","id"}, new String[] {"String","Назва","name"},
				new String[] {"String","Опис","ingredients"}));
		columnsContent.put("templateCodes", FXCollections.observableArrayList(new String[] {"CheckBox","","checkBox"}, new String[] {"Integer","№","number"}, new String[] {"Integer","Номер","id"}, new String[] {"String","Назва","name"},
				new String[] {"String","Шаблон","ingredients"}));
		columnsContent.put("send", FXCollections.observableArrayList(new String[] {"CheckBox","","checkBox"}, new String[] {"Integer","Номер","id"}, new String[] {"String","Назва","name"}));
	}
	public static ContentInfo getInstance() {
		if(inst==null) {
			inst = new ContentInfo();
		}
		return inst;
	}
	public static ObservableList<String[]> getColumnsContent(String key) {
		return getInstance().columnsContent.get(key);
	}
	
}
