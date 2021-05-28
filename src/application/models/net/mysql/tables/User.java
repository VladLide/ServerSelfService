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

public class User {
	private int id;
	private String login;
	private String pass;
	private int id_access_levels;
	//private static User inst;
	private MySQL db = new MySQL();
	public static String getTable() {
		return "users";
	}
	public List<String> getFields() {
		User me = this;
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				if(f.getName()=="inst"||f.getName()=="db") continue;
				System.out.println(table+"."+f.getName());
				fields.add(table+"."+f.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		User me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length-1];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				if(f.getName()=="inst"||f.getName()=="db") continue;
				String type = f.getType().getTypeName().replace(".", " ");
				if(type.split(" ").length>0) {
					String[] typ = type.split(" ");
					type = typ[typ.length-1];
				}
				switch(type) {
				case "int":
					values[i++] = new PackingDBValue(f.getName(),"I",(Object)f.get(me));
				break;
				case "float":
					values[i++] = new PackingDBValue(f.getName(),"F",(Object)f.get(me));
				break;
				case "double": 
					values[i++] = new PackingDBValue(f.getName(),"D",(Object)f.get(me));
				break;
				case "String":
					values[i++] = new PackingDBValue(f.getName(),"S",(Object)f.get(me));
				break;
				default:
					System.out.println(getTable()+": type was not found " + f.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	public User(ResultSet res, MySQL db) {
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
					case "DBHandler":{
						if(db!=null)
							this.db = db;
						else {
							this.db = new MySQL();
							this.db.getDBConnection();
						}
						break;
					}
					default:
						System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(getTable()+": "+e);
			}
		}
	}
	public User() {
		db.getDBConnection();
	}
	public User(MySQL db) {
		if(db!=null)
			this.db = db;
		else {
			this.db = new MySQL();
			this.db.getDBConnection();
		}
	}
	/*public static User init() {
		if(inst==null) {
			inst = new User();
		}
		return inst;
	}*/
	public static User get(int rId, MySQL db) {
		User res = new User(db);
		if(rId>0) {
	    	ResultSet resul = db.getSelect(User.getSql(rId));
	    	try {
				while(resul.next()) {
					res = new User(resul,db);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	public static User get(String login, MySQL db) {
		User res = new User(db);
		if(login.length()>0) {
	    	ResultSet resul = db.getSelect(User.getSql(login));
	    	try {
				while(resul.next()) {
					res = new User(resul,db);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	public static ObservableList<String> getLogins(MySQL db) {
    	ResultSet resul = db.getSelect(User.getSql(0));
    	ObservableList<String> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new User(resul,db).getLogin());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<User> get(MySQL db) {
    	ResultSet resul = db.getSelect(User.getSql(0));
    	ObservableList<User> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new User(resul,db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static String getSql(int rId) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(rId > 0) sql+= " where "+table+".id = "+rId;
		return sql;
	}
	public static String getSql(String login) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(login.length() > 0) sql+= " where "+table+".login = \""+login+"\"";
		return sql;
	}
	/*public static String getSqlByScale(int rId) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(rId > 0) sql += " WHERE "+table+".id = "+rId;
		return sql;
	}*/
	public boolean check() {
    	ResultSet resul = db.getSelect(User.getSql(this.getId()));
    	try {
			while(resul.next()) {
				new User(resul,db);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	public int save() {
		User me = this;
		String table = getTable();
		if(me.check()) {
			db.update(table,me.getFields().toArray(new String[0]), me.getValues(), new String[]{table+".id ="+me.getId()});
		}else {
			db.insert(table, me.getFields().toArray(new String[0]), me.getValues());
			me.setId(User.get(me.getLogin(),db).getId());
		}
		return me.getId();
	}
	public boolean delete() {
		User me = this;
		String table = getTable();
		if(me.check()) {
			db.delete(table, me.getId());
		}
		return (me.check())?false:true;
	}
	public static User getAuthorization(String login, String pass, MySQL db) {
		User me = new User(db);
		String table = getTable();
		String sql = "SELECT "+String.join(",",me.getFields())+" FROM "+table;
		sql+= " where "+table+".login = '"+login+"' AND "+table+".pass = '"+pass+"'";
		ResultSet resul = db.getSelect(sql);
		ObservableList<User> row = FXCollections.observableArrayList();
		try {
			while(resul.next()) {
				row.add(new User(resul,db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(row.size()==1)
			return row.get(0);
		else return null;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public int getId_access_levels() {
		return id_access_levels;
	}
	public void setId_access_levels(int id_access_levels) {
		this.id_access_levels = id_access_levels;
	}

}
