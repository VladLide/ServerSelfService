package application.views.languages.uk.windows;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SectionInfo {
	public static SectionInfo inst = null;
	public static String root = "Категорії";
	public Map<String, ObservableList<String[]>> columns = new HashMap<String, ObservableList<String[]>>();

	public SectionInfo() {
		super();
		columns.put("sections", FXCollections.observableArrayList(new String[] { "Integer", "Номер", "id" },
				new String[] { "String", "Назва", "name" }));
	}

	public static SectionInfo getInstance() {
		if (inst == null) {
			inst = new SectionInfo();
		}
		return inst;
	}

	public static ObservableList<String[]> getColumns(String key) {
		return getInstance().columns.get(key);
	}

}
