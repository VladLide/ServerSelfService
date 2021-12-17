package application.views.languages.uk.windows;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChooseInfo {
	public static ChooseInfo inst = null;
	public static String titleWindow = "Зв'язати";

	public ChooseInfo() {
		super();
	}

	public static ChooseInfo getInstance() {
		if (inst == null) {
			inst = new ChooseInfo();
		}
		return inst;
	}

}
