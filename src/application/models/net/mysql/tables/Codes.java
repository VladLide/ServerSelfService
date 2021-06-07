package application.models.net.mysql.tables;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.models.net.PackingDBValue;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Codes {
	private Integer id = 0;
	private String name = "2/5/5";
	private Integer prefix = 2;
	private Integer code = 5;
	private Integer unit = 5;
	private String prefix_val = "22";
	private String mask = "PPCCCCCUUUUU";

	public Codes(String maska, String prefix_val, String mask) {
		super();
		String[] el = maska.split("/");
		this.prefix = Integer.parseInt(el[0]);
		this.code = Integer.parseInt(el[1]);
		this.unit = Integer.parseInt(el[2]);
		this.prefix_val = prefix_val;
		this.mask = mask;
	}

	public Codes(Integer id, String name, String maska, String prefix_val, String mask) {
		super();
		String[] el = maska.split("/");
		this.id = id;
		this.name = name;
		this.prefix = Integer.parseInt(el[0]);
		this.code = Integer.parseInt(el[1]);
		this.unit = Integer.parseInt(el[2]);
		this.prefix_val = prefix_val;
		this.mask = mask;
	}

	public Codes(String name, String maska, String prefix_val, String mask) {
		super();
		String[] el = maska.split("/");
		if (el.length > 0) {
			this.name = maska;
			this.prefix = Integer.parseInt(el[0]);
			this.code = Integer.parseInt(el[1]);
			this.unit = Integer.parseInt(el[2]);
		} else {
			this.name = "";
			this.prefix = 0;
			this.code = 0;
			this.unit = 0;
		}
		this.prefix_val = prefix_val;
		this.mask = mask;
	}

	public static String getTable() {
		String table = "barcodes";
		return table;
	}

	public List<String> getFields() {
		Codes me = this;
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				System.out.println(table + "." + f.getName());
				fields.add(table + "." + f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}

	public PackingDBValue[] getValues() {
		Codes me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				switch (type) {
				case "Integer":
					values[i++] = new PackingDBValue(f.getName(), "I", (Object) f.get(me));
					break;
				case "float":
					values[i++] = new PackingDBValue(f.getName(), "F", (Object) f.get(me));
					break;
				case "double":
					values[i++] = new PackingDBValue(f.getName(), "D", (Object) f.get(me));
					break;
				case "String":
					values[i++] = new PackingDBValue(f.getName(), "S", (Object) f.get(me));
					break;
				case "Blob":
					values[i++] = new PackingDBValue(f.getName(), "B", (Object) f.get(me));
					break;
				default:
					System.out.println(getTable() + ": type was not found " + f.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	public Codes() {}

	public Codes(Integer id, String name, String maska, String mask) {
		super();
		String[] el = maska.split("|");
		this.id = id;
		this.name = name;
		this.prefix = Integer.parseInt(el[0]);
		this.code = Integer.parseInt(el[1]);
		this.unit = Integer.parseInt(el[2]);
		this.prefix_val = el[3];
		this.mask = mask;
	}

	public Codes(ResultSet res) {
		super();
		for (Field f : getClass().getDeclaredFields()) {
			try {
				if (f.getName() == "inst")
					continue;
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				switch (type) {
				case "Integer":
					f.set(this, res.getInt(res.findColumn(f.getName())));
					break;
				case "float":
					f.set(this, res.getFloat(res.findColumn(f.getName())));
					break;
				case "double":
					f.set(this, res.getDouble(res.findColumn(f.getName())));
					break;
				case "String":
					f.set(this, res.getString(res.findColumn(f.getName())));
					break;
				default:
					System.out.println(getTable() + ": type was not found " + f.getName() + ":" + f.getType());
				}
			} catch (Exception e) {
				System.out.println(getTable() + ": " + e.getMessage());
			}
		}
		if (this.name.length() == 0)
			this.name = this.prefix + "/" + this.code + "/" + this.unit;
	}

	public static Codes get(int rId, String name, MySQL db) {
		ResultSet resul = db.getSelect(Codes.getSql(rId, name));
		Codes row = null;
		try {
			while (resul.next()) {
				row = new Codes(resul);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static ObservableList<Codes> getList(int rId, String name, MySQL db) {
		ResultSet resul = db.getSelect(Codes.getSql(rId, name));
		ObservableList<Codes> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Codes(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static ObservableList<Object> getListObj(int rId, String name, MySQL db) {
		ResultSet resul = db.getSelect(Codes.getSql(rId, name));
		ObservableList<Object> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Codes(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static ObservableList<String> getListName(MySQL db) {
		ResultSet resul = db.getSelect(Codes.getSql(0, ""));
		ObservableList<String> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				Codes barcode = new Codes(resul);
				if (barcode.name.length() > 0)
					row.add(barcode.name);
				else
					row.add(barcode.prefix + "/" + barcode.code + "/" + barcode.unit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static String getSql(int rId, String name) {
		String table = getTable();
		String sql = "SELECT * FROM " + table;
		if (rId > 0 || name.length() > 0)
			sql += " WHERE ";
		if (rId > 0)
			sql += table + ".id = " + rId;
		if (rId > 0 && name.length() > 0)
			sql += " AND ";
		if (name.length() > 0)
			sql += table + ".name = '" + name + "'";
		sql += " ORDER BY " + table + ".id";
		return sql;
	}

	public int save(MySQL db) {
		String table = getTable();
		Codes isNew = Codes.get(0, name, db);
		String[] fields = getFields().toArray(new String[0]);
		if (isNew == null) {
			db.insert(table, fields, getValues());
			setId(get(0, name, db).getId());
		} else {
			db.update(table, fields, getValues(), new String[] { table + ".id =" + isNew.getId() });
		}
		return id;
	}

	public void updateId(int id, MySQL db) {
		String table = getTable();
		Codes isNew = Codes.get(id, "", db);
		if (isNew == null)
			db.update(table, new String[] { (table + ".id") },
					PackingDBValue.get(new String[] { "int" }, new Object[] { id }),
					new String[] { table + ".id = " + getId() });
		if (Codes.get(id, "", db) != null)
			this.setId(id);
	}

	public void updateName(String name, MySQL db) {
		String table = getTable();
		Codes isNew = Codes.get(0, name, db);
		if (isNew == null)
			db.update(table, new String[] { (table + ".name") },
					PackingDBValue.get(new String[] { "String" }, new Object[] { name }),
					new String[] { table + ".name = '" + getName() + "'" });
		if (Codes.get(0, name, db) != null)
			this.setName(name);
	}

	public boolean delete(MySQL db) {
		String table = getTable();
		if (Codes.get(id, "", db) != null) {
			db.delete(table, new String[] { table + ".id ='" + id + "'" });
		}
		return Codes.get(id, "", db) == null;
	}

	public void setAll(String name, int prefix, int code, int unit, String pr_val) {
		this.name = name;
		this.prefix = prefix;
		this.code = code;
		this.unit = unit;
		this.prefix_val = pr_val;
	}

	@Override
	public String toString() {
		return name;
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

	public int getPrefix() {
		return prefix;
	}

	public void setPrefix(int prefix) {
		this.prefix = prefix;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public String getPrefix_val() {
		return prefix_val;
	}

	public void setPrefix_val(String prefix_val) {
		this.prefix_val = prefix_val;
	}

	public String getMaska() {
		return this.prefix + "/" + this.code + "/" + this.unit;
	}

	public String getMask() {
		return (mask != null) ? mask : "";
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

}
