package application.models.net.mysql.tables;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.models.PackingDBValue;
import application.models.net.mssql.SQLServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UsersAccess {
	private int id_users = 0;
	private int id_access = 0;
	
	public static String getTable() {
		return "users_access";
	}
	public List<String> getFields() {
		UsersAccess me = this;
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				String table = getTable();
				if(f.getName()=="id") continue;
				System.out.println(table+"."+f.getName());
				fields.add(table+"."+f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		UsersAccess me = this;
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
					default:
						System.out.println(getTable()+": type was not found " + f.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	public UsersAccess(int uId, int aId) {
		super();
		this.id_users = uId;
		this.id_access = aId;
	}
	public UsersAccess(ResultSet res) {
		super();
		for (Field f : getClass().getDeclaredFields()) {
			try {
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
				System.out.println(getTable()+": "+e);
			}
		}
	}
	public static UsersAccess get(int uId, int aId, SQLServer db) {
    	ResultSet resul = db.getSelect(UsersAccess.getSql(uId,aId));
    	UsersAccess row = null;
    	try {
			while(resul.next()) {
				row = new UsersAccess(resul);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<UsersAccess> getList(int uId, int aId, SQLServer db) {
    	ResultSet resul = db.getSelect(UsersAccess.getSql(uId,aId));
    	ObservableList<UsersAccess> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new UsersAccess(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static String getSql(int id_users, int id_access) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(id_users > 0 || id_access > 0) sql+= " WHERE ";
		if(id_users > 0) sql+= table+".id_users = "+id_users;
		if(id_users > 0 && id_access > 0) sql+= " AND ";
		if(id_access > 0) sql+= table+".id_access = "+id_access;
		return sql;
	}
	public static void save(int id_users, int id_access, SQLServer db) {
		String table = getTable();
		UsersAccess isNew = UsersAccess.get(id_users, id_access,db);
		String[] fields = new UsersAccess().getFields().toArray(new String[0]);
		if(isNew==null) {
			db.insert(table, fields, new UsersAccess(id_users, id_access).getValues());
		}else {
			db.update(table,fields, new UsersAccess(id_users, id_access).getValues(), new String[]{table+".id_users ="+isNew.getId_users()});
		}
	}
	public static boolean delete(int id_users, int id_access, SQLServer db) {
		String table = getTable();
		if(id_users>0) {
			db.delete(table, new String[]{table+".id_users ="+id_users+" AND "+table+".id_access ="+id_access});
		}
		UsersAccess isNew = UsersAccess.get(id_users, id_access,db);
		return (isNew==null)?true:false;
	}
	public UsersAccess() {}
	
	public int getId_users() {
		return id_users;
	}
	public void setId_users(int id_users) {
		this.id_users = id_users;
	}
	public int getId_access() {
		return id_access;
	}
	public void setId_access(int id_access) {
		this.id_access = id_access;
	}
}
