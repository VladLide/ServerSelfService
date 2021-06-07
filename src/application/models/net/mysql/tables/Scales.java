package application.models.net.mysql.tables;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.models.TextBox;
import application.models.net.PackingDBValue;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Scales {
	private int id = 0;
	private String name = null;
	private String ip_address = null;
	private String ip_address_server = null;
	private int update = -1;
	private LocalDateTime date_update = LocalDateTime.parse("2000-01-01T00:00:00");
	private String data = null;
	private Map<String, String> config = new HashMap<String, String>();
	// private static Scales inst;

	public Scales() {
		super();
	}

	public Scales(int id, String name, String ip_adrress, String ip_adrress_server) {
		super();
		this.id = id;
		this.name = name;
		this.ip_address = ip_adrress;
		this.ip_address_server = ip_adrress_server;
	}

	public Scales(ResultSet res) {
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
				case "int":
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
				case "LocalDateTime": {
					Timestamp time = res.getTimestamp(res.findColumn(f.getName()));
					f.set(this, time.toLocalDateTime());
					break;
				}
				case "Blob":
					f.set(this, res.getBlob(res.findColumn(f.getName())));
					break;
				case "Map":
					this.readConfigFile();
					break;
				default:
					System.out.println(
							getTable() + " - create: type was not found '" + f.getName() + "' : '" + f.getType() + "'");
				}
			} catch (Exception e) {
				System.out.println(getTable() + ": " + e.getMessage());
			}
		}
	}

	public static String getTable() {
		return "scales";
	}

	public List<String> getFields() {
		Scales me = this;
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				if (f.getName() == "inst" || f.getName() == "config" || f.getName() == "db")
					continue;
				// System.out.println(table+"."+f.getName());
				fields.add(table + "." + f.getName());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}

	public PackingDBValue[] getValues() {
		Scales me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length - 1];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				if (f.getName() == "inst" || f.getName() == "config" || f.getName() == "db")
					continue;
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				switch (type) {
				case "int":
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
				case "LocalDateTime": {
					values[i++] = new PackingDBValue(f.getName(), "DT", (Object) f.get(me));
					break;
				}
				default:
					System.out.println(
							getTable() + " - value: type was not found '" + f.getName() + "' : '" + f.getType() + "'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
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
			sql += table + ".name = \"" + name + "\"";
		return sql;
	}

	public static Scales get(int rId, String name, MySQL db) {
		Scales res = null;
		ResultSet resul = db.getSelect(Scales.getSql(rId, name));
		try {
			while (resul.next()) {
				res = new Scales(resul);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static ObservableList<Scales> getList(int rId, String name, MySQL db) {
		ResultSet resul = db.getSelect(Scales.getSql(rId, name));
		ObservableList<Scales> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Scales(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static boolean check(Scales scale, MySQL db) {
		ResultSet resul = db.getSelect(Scales.getSql(scale.getId(), ""));
		try {
			while (resul.next()) {
				new Scales(resul);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public int save(MySQL db) {
		writeConfigFile();
		Scales me = this;
		String table = getTable();
		if (Scales.check(me, db)) {
			db.update(table, me.getFields().toArray(new String[0]), me.getValues(),
					new String[] { table + ".id =" + me.getId() });
		} else {
			db.deleteAll(table);
			db.insert(table, me.getFields().toArray(new String[0]), me.getValues());
			this.setId(Scales.get(0, me.getName(), db).getId());
		}
		return this.getId();
	}

	public boolean updateId(int id, MySQL db) {
		Scales me = this;
		String table = getTable();
		db.update(table, new String[] { (table + ".id") },
				PackingDBValue.get(new String[] { int.class.getTypeName() }, new Object[] { id }),
				new String[] { table + ".id = " + me.getId() });
		int oldId = me.getId();
		this.setId(id);
		if (Scales.check(me, db))
			return true;
		else {
			this.setId(oldId);
			return false;
		}
	}

	public boolean delete(MySQL db) {
		Scales me = this;
		String table = getTable();
		if (Scales.check(me, db)) {
			db.delete(table, me.getId());
		}
		return (Scales.check(me, db)) ? false : true;
	}

	public void readConfigFile() {
		String[] nextLine = {};
		if (this.data != null) {
			if (this.data.length() > 0)
				nextLine = this.data.split("\r");
		}
		if (TextBox.options.length > nextLine.length + 2) {
			for (int i = 2; i < TextBox.options.length; i++)
				this.config.put(TextBox.options[i][1], TextBox.options[i][2]);
		} else {
			for (String v : nextLine) {
				String[] key = v.split("=");
				this.config.put(key[0], key[1]);
			}
		}
	}

	public void writeConfigFile() {
		this.data = "";
		this.config.forEach((key, value) -> {
			this.data += key + "=" + value + "\r";
		});
	}

	public boolean getBoolean(String key) {
		try {
			return Integer.parseInt(this.config.get(key)) > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	public String getConfigItem(String name) {
		return config.get(name);
	}

	public void setConfigItem(String key, String value) {
		this.config.put(key, value);
	}

	public int getId() {
		return id;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public String getIp_address_server() {
		return ip_address_server;
	}

	public void setIp_address_server(String ip_address_server) {
		this.ip_address_server = ip_address_server;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUpdate() {
		return update;
	}

	public void setUpdate(int update) {
		this.update = update;
	}

	public LocalDateTime getDate_update() {
		return date_update;
	}

	public void setDate_update(LocalDateTime date_update) {
		this.date_update = date_update;
	}

	public Map<String, String> getConfigFile() {
		return config;
	}

	public int getCountSection_grid() {
		String[] grid = this.config.get("section_grid").replace("*", "-").split("-");
		return Integer.parseInt(grid[0]) * Integer.parseInt(grid[1]);
	}

	public int getCountGoods_grid() {
		String[] grid = this.config.get("plu_grid").replace("*", "-").split("-");
		return Integer.parseInt(grid[0]) * Integer.parseInt(grid[1]);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
