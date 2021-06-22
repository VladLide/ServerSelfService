package application.models.net.mysql.tables;

import java.sql.ResultSet;
import java.sql.SQLException;

import application.models.net.UtilsNet;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Distribute {
	private int id_command = 1;
	private int id_type_table = 1;
	private Integer unique_item = 0;
	private int id_scales = 0;
	private int id_condition = 0;
	private int id_templates = 0;
	private int id_barcodes = 0;
	private float price = 0.0F;
	private String description = "";
	private boolean batch = false;

	public static String getTable() {
		String table = "distribute";
		return table;
	}

	public Distribute() {}

	public Distribute(int id_command, int id_type_table, Integer unique_item, int id_scales) {
		super();
		this.id_command = id_command;
		this.id_type_table = id_type_table;
		this.unique_item = unique_item;
		this.id_scales = id_scales;
	}

	public Distribute(ResultSet res) {
		super();
		UtilsNet.setValue(res, this, getClass().getDeclaredFields());
	}

	public static Distribute get(int idCommand, int idTypeTable, Integer uniqueItem, int idScales, MySQL db) {
		ObservableList<Distribute> result = Distribute.getList(idCommand, idTypeTable, uniqueItem, idScales, db);
		return result.isEmpty() ? null : result.get(0);
	}

	public static ObservableList<Distribute> getList(int idCommand, int idTypeTable, Integer uniqueItem, int idScales, MySQL db) {
		ResultSet resul = db.getSelect(Distribute.getSql(idCommand, idTypeTable, uniqueItem, idScales));
		ObservableList<Distribute> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new Distribute(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static ObservableList<Object> getListObj(int idCommand, int idTypeTable, Integer uniqueItem, int idScales, MySQL db) {
		ResultSet resul = db.getSelect(Distribute.getSql(idCommand, idTypeTable, uniqueItem, idScales));
		ObservableList<Object> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new Distribute(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static String getSql(int idCommand, int idTypeTable, Integer uniqueItem, int idScales) {
		String table = getTable();
		String sql = "SELECT * FROM " + table;
		if (idCommand > 0 || idTypeTable > 0 || uniqueItem != null || idScales > 0)
			sql += " WHERE ";
		if (idCommand > 0)
			sql += table + ".id_command = " + idCommand;
		if (idCommand > 0 && (idTypeTable > 0 || uniqueItem != null || idScales > 0))
			sql += " AND ";
		if (idTypeTable > 0)
			sql += table + ".id_type_table = " + idTypeTable;
		if (idTypeTable > 0 && (uniqueItem != null || idScales > 0))
			sql += " AND ";
		if (uniqueItem != null)
			sql += table + ".unique_item = " + uniqueItem;
		if (uniqueItem != null && idScales > 0)
			sql += " AND ";
		if (idScales > 0)
			sql += table + ".id_scales = " + idScales;
		sql += " ORDER BY " + table + ".id_type_table";
		return sql;
	}

	public boolean delete(MySQL db) {
		String table = getTable();
		if (Distribute.get(id_command, id_type_table, unique_item, id_scales, db) != null) {
			db.delete(table, new String[] {table + ".id_command ='" + id_command + "'", table + ".id_type_table ='" + id_type_table + "'", table + ".unique_item ='" + unique_item + "'",
					table + ".id_scales ='" + id_scales + "'"});
		}
		return Distribute.get(id_command, id_type_table, unique_item, id_scales, db) == null;
	}

	public int getId_command() {
		return id_command;
	}

	public void setId_command(int id_command) {
		this.id_command = id_command;
	}

	public int getId_type_table() {
		return id_type_table;
	}

	public void setId_type_table(int id_type_table) {
		this.id_type_table = id_type_table;
	}

	public int getUnique_item() {
		return unique_item;
	}

	public void setUnique_item(Integer unique_item) {
		this.unique_item = unique_item;
	}

	public int getId_scales() {
		return id_scales;
	}

	public void setId_scales(int id_scales) {
		this.id_scales = id_scales;
	}

	public int getId_condition() {
		return id_condition;
	}

	public void setId_condition(int id_condition) {
		this.id_condition = id_condition;
	}

	public int getId_templates() {
		return id_templates;
	}

	public void setId_templates(int id_templates) {
		this.id_templates = id_templates;
	}

	public int getId_barcodes() {
		return id_barcodes;
	}

	public void setId_barcodes(int id_barcodes) {
		this.id_barcodes = id_barcodes;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isBatch() {
		return batch;
	}

	public void setBatch(boolean batch) {
		this.batch = batch;
	}

}
