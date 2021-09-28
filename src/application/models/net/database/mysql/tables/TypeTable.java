package application.models.net.database.mysql.tables;

import java.sql.ResultSet;
import java.sql.SQLException;

import application.models.net.PackingDBValue;
import application.models.net.UtilsNet;
import application.models.net.database.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TypeTable {
	private int id = 1;
	private String type = "";
	private String name = "";

	public static String getTable() {
		String table = "type_table";
		return table;
	}

	public TypeTable() {}

	public TypeTable(int id, String type, String name) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public TypeTable(ResultSet res) {
		super();
		UtilsNet.setValue(res, this, getClass().getDeclaredFields());
	}

	public static TypeTable get(int id, String type, String name, MySQL db) {
		ObservableList<TypeTable> result = TypeTable.getList(id, type, name, db);
		return result.isEmpty() ? null : result.get(0);
	}

	public static ObservableList<TypeTable> getList(int id, String type, String name, MySQL db) {
		ResultSet resul = db.getSelect(TypeTable.getSql(id, type, name));
		ObservableList<TypeTable> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new TypeTable(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static ObservableList<Object> getListObj(int id, String type, String name, MySQL db) {
		ResultSet resul = db.getSelect(TypeTable.getSql(id, type, name));
		ObservableList<Object> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new TypeTable(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static String getSql(int id, String type, String name) {
		String table = getTable();
		String sql = "SELECT * FROM " + table;
		if (id > 0 || !type.isEmpty() || !name.isEmpty())
			sql += " WHERE ";
		if (id > 0)
			sql += table + ".id = " + id;
		if (id > 0 && (!type.isEmpty() || !name.isEmpty()))
			sql += " AND ";
		if (!type.isEmpty())
			sql += table + ".type = '" + type + "'";
		if (!type.isEmpty() && !name.isEmpty())
			sql += " AND ";
		if (!name.isEmpty())
			sql += table + ".name = '" + name + "'";
		sql += " ORDER BY " + table + ".id";
		return sql;
	}

	public int save(MySQL db) {
		String table = getTable();
		TypeTable isNew = TypeTable.get(id, "", "", db);
		String[] fields = UtilsNet.getFields(table, getClass().getDeclaredFields()).toArray(new String[0]);
		PackingDBValue[] values = UtilsNet.getValues(getClass().getDeclaredFields(), this);
		if (isNew == null) {
			db.insert(table, fields, values);
		} else {
			db.update(table, fields, values, new String[] {table + ".id =" + isNew.getId()});
		}
		return id;
	}

	public void updateId(int id, MySQL db) {
		String table = getTable();
		TypeTable isNew = TypeTable.get(id, "", "", db);
		if (isNew == null)
			db.update(table, new String[] {(table + ".id")},
					PackingDBValue.get(new String[] {"int"}, new Object[] {id}),
					new String[] { table + ".id = " + getId() });
		if (TypeTable.get(id, "", "", db) != null)
			this.setId(id);
	}

	public boolean delete(MySQL db) {
		String table = getTable();
		if (TypeTable.get(id, "", "", db) != null) {
			db.delete(table, new String[] {table + ".id ='" + id + "'"});
		}
		return TypeTable.get(id, "", "", db) == null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
