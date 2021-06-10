package application.models.net.mysql.tables;

import java.sql.ResultSet;
import java.sql.SQLException;

import application.models.net.PackingDBValue;
import application.models.net.UtilsNet;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Command {
	private int id = 1;
	private String name = "";
	private String description = "";

	public static String getTable() {
		String table = "type_table";
		return table;
	}

	public Command() {}

	public Command(int id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Command(ResultSet res) {
		super();
		UtilsNet.setValue(res, this, getClass().getDeclaredFields());
	}

	public static Command get(int id, String name, MySQL db) {
		ObservableList<Command> result = Command.getList(id, name, db);
		return result.isEmpty() ? null : result.get(0);
	}

	public static ObservableList<Command> getList(int id, String name, MySQL db) {
		ResultSet resul = db.getSelect(Command.getSql(id, name));
		ObservableList<Command> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new Command(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static ObservableList<Object> getListObj(int id, String name, MySQL db) {
		ResultSet resul = db.getSelect(Command.getSql(id, name));
		ObservableList<Object> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new Command(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static String getSql(int id, String name) {
		String table = getTable();
		String sql = "SELECT * FROM " + table;
		if (id > 0 || !name.isEmpty())
			sql += " WHERE ";
		if (id > 0)
			sql += table + ".id = " + id;
		if (id > 0 && !name.isEmpty())
			sql += " AND ";
		if (!name.isEmpty())
			sql += table + ".name = '" + name + "'";
		sql += " ORDER BY " + table + ".id";
		return sql;
	}

	public int save(MySQL db) {
		String table = getTable();
		Command isNew = Command.get(id, "", db);
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
		Command isNew = Command.get(id, "", db);
		if (isNew == null)
			db.update(table, new String[] {(table + ".id")},
					PackingDBValue.get(new String[] {"int"}, new Object[] {id}),
					new String[] { table + ".id = " + getId() });
		if (Command.get(id, "", db) != null)
			this.setId(id);
	}

	public boolean delete(MySQL db) {
		String table = getTable();
		if (Command.get(id, "", db) != null) {
			db.delete(table, new String[] {table + ".id ='" + id + "'"});
		}
		return Command.get(id, "", db) == null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
