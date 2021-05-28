package application.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Scales;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Item {
    private int index;
    private Scales scale;
    private String view;
    private boolean isSelected;

    public Item(int index,Scales scale) {
        this.index = index;
        this.scale = scale;
        this.view = scale.getName();
        this.isSelected = false;
    }
    public Item(String view) {
        this(view, false);
    }
    public Item(String view, boolean isSelected) {
        this.view = view;
        this.isSelected = isSelected;
    }
	public static ObservableList<Item> get() {
    	ResultSet resul = new MySQL().getSelect(Scales.getSql(0,""));
    	ObservableList<Item> row = FXCollections.observableArrayList();
    	try {
    		int index = 0;
    		while(resul.next()) {
				Item item = new Item(index,new Scales(resul));
				if(item.scale.getId() != 0)
					row.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Scales getScale() {
		return scale;
	}
	public void setScale(Scales scale) {
		this.scale = scale;
	}
}
