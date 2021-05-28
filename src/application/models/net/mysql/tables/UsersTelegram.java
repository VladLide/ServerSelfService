package application.models.net.mysql.tables;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.models.PackingDBValue;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UsersTelegram {
	private int id = 0;
	private String name = "";
	private String last = "";
	private String phone = "";
	private String chat_id = "";
	
	public static String getTable() {
		String table = "users_telegram";
		return table;
	}
	public List<String> getFields() {
		UsersTelegram me = this;//init();
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				System.out.println(table+"."+f.getName());
				fields.add(table+"."+f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		UsersTelegram me = this;//init();
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				String type = f.getType().getTypeName().replace(".", " ");
				if(type.split(" ").length>0) {
					String[] typ = type.split(" ");
					type = typ[typ.length-1];
				}
				switch(type) {
					case "int":
						values[i++] = new PackingDBValue((Object)f.get(me),"I");
					break;
					case "float":
						values[i++] = new PackingDBValue((Object)f.get(me),"F");
					break;
					case "double": 
						values[i++] = new PackingDBValue((Object)f.get(me),"D");
					break;
					case "String":
						values[i++] = new PackingDBValue((Object)f.get(me),"S");
					break;
					case "Blob":
						values[i++] = new PackingDBValue((Object)f.get(me),"B");
					break;
					default:
						System.out.println("Section: type was not found " + f.getName()+"-"+f.getType());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	public UsersTelegram(ResultSet res, MySQL db) {
		super();
		for (Field f : getClass().getDeclaredFields()) {
			try {
				if(f.getName()=="inst") continue;
				String type = f.getType().getTypeName().replace(".", " ");
				if(type.split(" ").length>0) {
					String[] typ = type.split(" ");
					type = typ[typ.length-1];
				}
				switch(type) {
					case "int":
						f.set(this,res.getInt(res.findColumn(f.getName())));
					break;
					case "float":
						f.set(this,res.getFloat(res.findColumn(f.getName())));
					break;
					case "double": 
						f.set(this,res.getDouble(res.findColumn(f.getName())));
					break;
					case "String":
						f.set(this,res.getString(res.findColumn(f.getName())));
					break;
					default:
						System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(getTable()+":"+e);
			}
		}
	}
	public UsersTelegram() {}
	public static UsersTelegram get(int id, String name, String phone, String chat_id, MySQL db) {
		UsersTelegram res = null;
    	ResultSet resul = db.getSelect(UsersTelegram.getSql(id, name, phone, chat_id));
    	try {
			while(resul.next()) {
				res = new UsersTelegram(resul,db);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static ObservableList<UsersTelegram> getList(int id, String name, String phone, String chat_id, MySQL db) {
    	ResultSet resul = db.getSelect(UsersTelegram.getSql(id, name, phone, chat_id));
    	ObservableList<UsersTelegram> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new UsersTelegram(resul,db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<UsersTelegram> get(String name, String phone, MySQL db) {
    	ResultSet resul = db.getSelect(UsersTelegram.getSql(name, phone));
    	ObservableList<UsersTelegram> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new UsersTelegram(resul,db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static String getSql(int id, String name, String phone, String chat_id) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(id > 0 || name.length() > 0 || phone.length() > 0 || chat_id.length() > 0) sql+= " WHERE ";
		if(id > 0) sql+= table+".id = "+id;
		if(id > 0&&(name.length() > 0||phone.length() > 0||chat_id.length() > 0 )) sql += " AND ";
		if(name.length() > 0) sql+= table+".name = '"+name+"'";
		if(name.length() > 0&&(phone.length() > 0||chat_id.length() > 0)) sql += " AND ";
		if(phone.length() > 0) sql+= table+".phone = '"+phone+"'";
		if(phone.length() > 0&&(chat_id.length() > 0)) sql += " AND ";
		if(chat_id.length() > 0) sql+= table+".chat_id = '"+chat_id+"'";
		return sql;
	}
	public static String getSql(String name, String phone) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(name.length() > 0 || phone.length() > 0) sql+= " WHERE ";
		if(name.length() > 0) sql+= table+".name LIKE '%"+name+"%'";
		if(name.length() > 0&&(phone.length() > 0)) sql += " OR ";
		if(phone.length() > 0) sql+= table+".phone LIKE '%"+phone+"%'";
		return sql;
	}
	public int save(MySQL db) {
		UsersTelegram me = this;//init();
		String table = getTable();
		if(me.getChat_id().length()<1)me.setChat_id(me.getId()+"");
		UsersTelegram isNew = UsersTelegram.get(0,"","",me.getChat_id(),db);
		if(isNew==null) {
			db.insert(table, me.getFields().toArray(new String[0]), me.getValues());
		}else {
			db.update(table,me.getFields().toArray(new String[0]), me.getValues(), new String[]{table+".chat_id ="+me.getChat_id()});
		}
		me.setId(UsersTelegram.get(0,"","",me.getChat_id(),db).getId());
		return me.getId();
	}
	public void updateChat_id(String chat_id,MySQL db) {
		UsersTelegram me = this;
		String table = getTable();
		UsersTelegram isNew = UsersTelegram.get(0,"","",chat_id,db);
		if(isNew==null)db.update(table,new String[]{(table+".chat_id")}, PackingDBValue.get(new String[] {"String"}, new Object[] {chat_id}), new String[]{table+".chat_id = '"+me.getChat_id()+"'"});
		if(UsersTelegram.get(0,"","",chat_id,db)!=null)this.setName(name);
	}
	public boolean delete(MySQL db) {
		UsersTelegram me = this;//init();
		String table = getTable();
		if(me.getId()>0) {
			db.delete(table, me.getId());
			me.setId(0);
		}
		return (UsersTelegram.get(0,"","",me.getChat_id(),db)==null)?true:false;
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
	public String getLast() {
		return last;
	}
	public void setLast(String last) {
		this.last = last;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getChat_id() {
		return chat_id;
	}
	public void setChat_id(String chat_id) {
		this.chat_id = chat_id;
	}

}
