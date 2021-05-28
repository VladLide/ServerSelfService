package application.views.languages.uk.windows;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CodeInfo {
	public static CodeInfo inst = null;
	public static ObservableList<String[]> ItemsTemplate = FXCollections.observableArrayList(new String[] {"prefix","Префікс","P"},new String[] {"code","Код товару","C"},new String[] {"unit","Одиниця виміру","U"});
	
	public CodeInfo() {
		super();
	}
	
	public static CodeInfo getInstance() {
		if(inst==null) {
			inst = new CodeInfo();
		}
		return inst;
	}
	
}
