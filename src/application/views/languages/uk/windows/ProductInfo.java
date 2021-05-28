package application.views.languages.uk.windows;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductInfo {
	public static ProductInfo inst = null;
	public static ObservableList<String[]> dataTableColums = FXCollections.observableArrayList(new String[] {"Integer","№","number"}, new String[] {"Integer","Код","pre_code"}, new String[] {"Integer","Код","code"}, 
			new String[] {"String","Назва","name"}, new String[] {"Float","Ціна","price"}, new String[] {"String","Тип","type"});
	public static ObservableList<String> unit = FXCollections.observableArrayList("ШТ.", "КГ.");
	
	public ProductInfo() {
		super();
	}
	
	public static ProductInfo getInstance() {
		if(inst==null) {
			inst = new ProductInfo();
		}
		return inst;
	}
	
}
