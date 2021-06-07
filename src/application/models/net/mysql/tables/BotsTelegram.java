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

public class BotsTelegram {
	private int id = 0;
	private String name = "";
	private String address = "";
	private String api_token = "";
	private int included = 1;

	public static String getTable() {
		String table = "bots_telegram";
		return table;
	}

	public List<String> getFields() {
		BotsTelegram me = this;// init();
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		// this.id = res.findColumn("id");
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				// sb.append(f.getName());
				if (f.getName() == "id")
					continue;
				System.out.println(table + "." + f.getName());
				fields.add(table + "." + f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}

	public PackingDBValue[] getValues() {
		BotsTelegram me = this;// init();
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length - 1];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				if (f.getName() == "id")
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
				default:
					System.out.println(getTable() + ":" + " type was not found " + f.getName() + "-" + f.getType());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	public BotsTelegram(ResultSet res, MySQL db) {
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
				default:
					System.out.println(getTable() + ": type was not found " + f.getName() + ":" + f.getType());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(getTable() + ":" + e);
			}
		}
	}

	public BotsTelegram() {
	}

	public static BotsTelegram get(int id, String name, String address, String api_token, int included, MySQL db) {
		BotsTelegram res = null;
		ResultSet resul = db.getSelect(BotsTelegram.getSql(id, name, address, api_token, included));
		try {
			while (resul.next()) {
				res = new BotsTelegram(resul, db);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static ObservableList<BotsTelegram> getList(int id, String name, String phone, String chat_id, int included,
			MySQL db) {
		ResultSet resul = db.getSelect(BotsTelegram.getSql(id, name, phone, chat_id, included));
		ObservableList<BotsTelegram> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new BotsTelegram(resul, db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static ObservableList<BotsTelegram> get(String name, MySQL db) {
		ResultSet resul = db.getSelect(BotsTelegram.getSql(name));
		ObservableList<BotsTelegram> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new BotsTelegram(resul, db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static String getSql(int id, String name, String address, String api_token, int included) {
		String table = getTable();
		String sql = "SELECT * FROM " + table;
		if (id > 0 || name.length() > 0 || address.length() > 0 || api_token.length() > 0 || included > -1)
			sql += " WHERE ";
		if (id > 0)
			sql += table + ".id = " + id;
		if (id > 0 && (name.length() > 0 || address.length() > 0 || api_token.length() > 0 || included > -1))
			sql += " AND ";
		if (name.length() > 0)
			sql += table + ".name = '" + name + "'";
		if (name.length() > 0 && (address.length() > 0 || api_token.length() > 0 || included > -1))
			sql += " AND ";
		if (address.length() > 0)
			sql += table + ".address = '" + address + "'";
		if (address.length() > 0 && (api_token.length() > 0 || included > -1))
			sql += " AND ";
		if (api_token.length() > 0)
			sql += table + ".api_token = '" + api_token + "'";
		if (api_token.length() > 0 && (included > -1))
			sql += " AND ";
		if (included > -1)
			sql += table + ".included = " + included;
		return sql;
	}

	public static String getSql(String name) {
		String table = getTable();
		String sql = "SELECT * FROM " + table;
		if (name.length() > 0)
			sql += " WHERE ";
		if (name.length() > 0)
			sql += table + ".name LIKE '%" + name + "%'";
		return sql;
	}

	public int save(MySQL db) {
		BotsTelegram me = this;// init();
		String table = getTable();
		BotsTelegram isNew = BotsTelegram.get(0, me.getName(), "", "", -1, db);
		if (isNew == null) {
			db.insert(table, me.getFields().toArray(new String[0]), me.getValues());
		} else {
			db.update(table, me.getFields().toArray(new String[0]), me.getValues(),
					new String[] { table + ".name = '" + me.getName() + "'" });
		}
		me.setId(BotsTelegram.get(0, me.getName(), "", "", -1, db).getId());
		return me.getId();
	}

	public void updateName(String name, MySQL db) {
		BotsTelegram me = this;
		String table = getTable();
		BotsTelegram isNew = BotsTelegram.get(0, name, "", "", -1, db);
		if (isNew == null)
			db.update(table, new String[] { (table + ".name") },
					PackingDBValue.get(new String[] { "String" }, new Object[] { name }),
					new String[] { table + ".name = '" + me.getName() + "'" });
		if (BotsTelegram.get(0, name, "", "", -1, db) != null)
			this.setName(name);
	}

	public boolean delete(MySQL db) {
		BotsTelegram me = this;// init();
		String table = getTable();
		if (me.getId() > 0) {
			db.delete(table, me.getId());
			me.setId(0);
		}
		return (BotsTelegram.get(0, me.getName(), "", "", -1, db) == null) ? true : false;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getApi_token() {
		return api_token;
	}

	public void setApi_token(String api_token) {
		this.api_token = api_token;
	}

	public int getIncluded() {
		return included;
	}

	public void setIncluded(int included) {
		this.included = included;
	}

}
