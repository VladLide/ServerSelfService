package application.views.languages.uk.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class TemplateInfo {
	public static TemplateInfo inst = null;
	public static ObservableList<String> templateInfo = FXCollections.observableArrayList("№", "Назва", "Ширина",
			"Висота");
	public Map<String, ObservableList<String[]>> columnsContent = new HashMap<String, ObservableList<String[]>>();
	public static ObservableList<String[]> ItemsTemplate = FXCollections.observableArrayList(
			new String[] { "namePLU", "Назва товару", "Назва товару" }, new String[] { "barcode", "Штрихкод", "" },
			new String[] { "price", "Ціна за одиницю", "000.00" }, new String[] { "weight", "Вага", "00.000" },
			new String[] { "cost", "Вартість", "00000.00" },
			new String[] { "endDate", "Термін придатності", "00.00.0000" },
			new String[] { "date", "Дата пакування", "00.00.0000" },
			new String[] { "freeText", "Вільний текст", "Вільний текст ..." },
			new String[] { "codePLU", "Код товару", "55555" },
			new String[] { "top", "Текст заголовку", "Раді Вас Вітати!!!" },
			new String[] { "bottom", "Колонтітул", "Дякуємо за покупку!!!" },
			new String[] { "ingredients", "Інгредієнти", "Інгредієнти ..." }, new String[] { "tara", "Тара", "0.000" },
			new String[] { "weightUp", "Вага бутто", "00.000" }, new String[] { "weightDown", "Вага нетто", "00.000" },
			new String[] { "numberTemplate", "Номер етикетки", "15" },
			new String[] { "timePrint", "Час друку етикетки", "00:00:00" },
			new String[] { "timePacking", "Час пакування", "00:00:00" },
			new String[] { "daySave", "Термін зберігання (в днях)", "000" },
			new String[] { "timeSave", "Термін зберігання (в годинах)", "00" },
			/* new String[] {"qrcode","QR код",""}, */ new String[] { "line", "Лінія", "" },
			new String[] { "rectangle", "Прямокутник", "" }, new String[] { "NB", "Номер кнопки товару", "1" });
	public static ObservableList<String> parametersItem = FXCollections.observableArrayList(
			"Ширина пробілу",
			"Висота штрих коду",
			"Розмір шрифту",
			"Вільна зона",
			"Жирність шрифту",
			"Текст",
			"Ширина",
			"Висота",
			"Вирівнювання",
			"З наступного рядку",
			"Міжрядковий інтервал",
			"Відступ(зверху;праворуч;внизу;зліва)",
			"Ширина межі",
			"Кут",
			"Довжина",
			"X",
			"Y");

	public TemplateInfo() {
		super();
		columnsContent.put("templates",
				FXCollections.observableArrayList(new String[] { "CheckBox", "", "checkBox" },
						new String[] { "Integer", "№", "number" }, new String[] { "Integer", "Номер", "id" },
						new String[] { "String", "Назва", "name" }, new String[] { "String", "Опис", "ingredients" }));
	}

	public static TemplateInfo getInstance() {
		if (inst == null) {
			inst = new TemplateInfo();
		}
		return inst;
	}

}
