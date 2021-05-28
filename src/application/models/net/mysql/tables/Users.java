package application.models.net.mysql.tables;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import application.models.PackingDBValue;
import application.models.PasswordGenerator;
import application.models.net.mssql.SQLServer;
import application.models.tables.Info2Col;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Users {
	private int num = 0;
	private int id = 0;
	private String code = "";
	private String login = "";
	private String pass = "";
	private String first_name = "";
	private String name = "";
	private String patronymic = "";
	private String accessName = "";
	/*private Map<Integer, Access> access = new HashMap<Integer, Access>();
	private ObservableList<Move> move = FXCollections.observableArrayList();*/
	
	public static String getTable() {
		return "users";
	}
	public List<String> getFields() {
		Users me = this;
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				if(f.getName()=="num"||f.getName()=="id"||f.getName()=="accessName"||f.getName()=="access"||f.getName()=="move") continue;
				System.out.println(table+"."+f.getName());
				fields.add(table+"."+f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		Users me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length-5];
		try {
			int i = 0;
			for (Field f : me.getClass().getDeclaredFields()) {
				if(f.getName()=="num"||f.getName()=="id"||f.getName()=="accessName"||f.getName()=="access"||f.getName()=="move") continue;
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
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	public Users(int i, ResultSet res) {
		super();
		this.num = i;
		for (Field f : getClass().getDeclaredFields()) {
			try {
				if(f.getName()=="num"||f.getName()=="accessName"||f.getName()=="access"||f.getName()=="move") continue;
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
	public Users(SQLServer db) {
		super();
		int i = 0;
		String passCode = "";
		while(i<1000000) {
			passCode = DigestUtils.md5Hex(PasswordGenerator.generateRandomPassword(15));
			if(Users.get(0,passCode,"","","",db)==null) i = 1000000000;
			i++;
		}
		this.code = passCode;
	}
	public ObservableList<Info2Col> getInfo() {
		ObservableList<Info2Col> row = FXCollections.observableArrayList();
		for (Field f : getClass().getDeclaredFields()) {
			try {
				String type = f.getType().getTypeName().replace(".", " ");
				if(type.split(" ").length>0) {
					String[] typ = type.split(" ");
					type = typ[typ.length-1];
				}
				switch(f.getName()) {
					case "code":
						row.add(new Info2Col("Код входу",StringUtils.repeat("*", code.length()),0));
					break;
					case "login":
						row.add(new Info2Col("Логін",this.login,1));
					break;
					case "pass": 
						row.add(new Info2Col("Пароль",StringUtils.repeat("*", this.pass.length()/2),2));
					break;
					case "first_name":
						row.add(new Info2Col("Призвище",this.first_name,3));
					break;
					case "name":
						row.add(new Info2Col("Ім'я",this.name,4));
					break;
					case "patronymic":
						row.add(new Info2Col("По батькові",this.patronymic,5));
					break;
					default:{}
				}
			} catch (Exception e) {
				System.out.println(getTable()+": "+e);
			}
		}
		return row;
	}
	public void setInfo(Info2Col item) {
		switch(item.getType()) {
			case 0:{}
			break;
			case 1:
				this.login = item.getValue();
			break;
			case 2: 
				this.pass = DigestUtils.md5Hex(item.getValue());
			break;
			case 3:
				this.first_name = item.getValue();
			break;
			case 4:
				this.name = item.getValue();
			break;
			case 5:
				this.patronymic = item.getValue();
			break;
			default:{}
		}
	}
	public static Users get(int id, String code, String login, String name, String pass, SQLServer db) {
		Users res = null;
    	ResultSet resul = db.getSelect(Users.getSql(id,code,login,name,pass));
    	try {
    		int num = 1;
			while(resul.next()) {
				res = new Users(num,resul);
				/*res.setAccess(Access.getMap(0, res.getId(), "", db));
				res.setMove(Move.getList(0, res.getId(), 0, "", "", db));
				res.setAccessName(res.getAccess());*/
				num++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static ObservableList<String> getLogins(SQLServer db) {
    	ResultSet resul = db.getSelect(Users.getSql(0,"","","",""));
    	ObservableList<String> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new Users(0,resul).getLogin());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<Users> getList(int id, String code, String login, String name, SQLServer db) {
    	ResultSet resul = db.getSelect(Users.getSql(id,code,login,name,""));
    	ObservableList<Users> row = FXCollections.observableArrayList();
    	try {
    		int num = 1;
			while(resul.next()) {
				Users item = new Users(num,resul);
				/*item.setAccess(Access.getMap(0, item.getId(), "", db));
				item.setAccessName(item.getAccess());*/
				row.add(item);
				num++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<Users> getList(String login, String name, SQLServer db) {
    	ResultSet resul = db.getSelect(Users.getSql(login,name));
    	ObservableList<Users> row = FXCollections.observableArrayList();
    	try {
    		int num = 1;
			while(resul.next()) {
				Users item = new Users(num,resul);
				/*item.setAccess(Access.getMap(0, item.getId(), "", db));
				item.setAccessName(item.getAccess());*/
				row.add(item);
				num++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static String getSql(String login, String name) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(login.length() > 0 || name.length() > 0) sql+= " WHERE ";
		if(login.length() > 0) sql+= table+".login LIKE '%"+login+"%'";
		if(login.length() > 0&&(name.length() > 0)) sql += " AND ";
		if(name.length() > 0) sql+= table+".name LIKE '%"+name+"%'";
		return sql;
	}
	public static String getSql(int id, String code, String login, String name, String pass) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if(id > 0 || code.length() > 0 || login.length() > 0 || name.length() > 0 || pass.length() > 0) sql+= " WHERE ";
		if(id > 0) sql+= table+".id = "+id;
		if(id > 0&&(code.length() > 0||login.length() > 0||name.length() > 0 || pass.length() > 0)) sql += " AND ";
		if(code.length() > 0) sql+= table+".code = '"+code+"'";
		if(code.length() > 0&&(login.length() > 0||name.length() > 0 || pass.length() > 0)) sql += " AND ";
		if(login.length() > 0) sql+= table+".login = '"+login+"'";
		if(login.length() > 0&&(name.length() > 0 || pass.length() > 0)) sql += " AND ";
		if(name.length() > 0) sql+= table+".name = '"+name+"'";
		if(name.length() > 0 && pass.length() > 0) sql += " AND ";
		if(pass.length() > 0) sql+= table+".pass = '"+pass+"'";
		return sql;
	}
	public int save(SQLServer db) {
		Users me = this;//init();
		String table = getTable();
		Users isNew = Users.get(0,me.getCode(),"","","",db);
		if(isNew==null) {
			db.insert(table, me.getFields().toArray(new String[0]), me.getValues());
		}else {
			db.update(table,me.getFields().toArray(new String[0]), me.getValues(), new String[]{table+".id ='"+me.getId()+"'"});
		}
		Users it = Users.get(0,me.getCode(),"","","",db);
		if(it!=null)me.setId(it.getId());
		return me.getId();
	}
	public void updateCode(String newCode,SQLServer db) {
		String code = newCode+"";
		Users me = this;
		String table = getTable();
		Users isNew = Users.get(0,me.getCode(),"","","",db);
		if(isNew!=null)db.update(table,new String[]{(table+".code")}, PackingDBValue.get(new String[] {"String"}, new Object[] {code}), new String[]{table+".code = '"+me.getCode()+"'"});
		if(Users.get(0,code,"","","",db)!=null)this.setCode(code);
	}
	public boolean delete(SQLServer db) {
		Users me = this;//init();
		String table = getTable();
		if(me.getId()>0) {
			db.delete(table, me.getId());
			me.setId(0);
		}
		return (Users.get(me.getId(),"","","","",db)==null)?true:false;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPatronymic() {
		return patronymic;
	}
	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}
	/*public Map<Integer, Access> getAccess() {
		return access;
	}
	public Access getItemAccess(int key) {
		return access.get(key);
	}
	public void setAccess(Map<Integer, Access> access) {
		this.access = access;
	}
	public void setItemAccess(int key, Access value) {
		this.access.put(key, value);
	}*/
	public String getAccessName() {
		return accessName;
	}
	public void setAccessName(String accessName) {
		this.accessName = accessName;
	}
	/*public void setAccessName(Map<Integer, Access> access) {
		access.forEach((k,v)->{
			this.accessName+=v.getName()+System.getProperty("line.separator");
		});
	}*/
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	/*public ObservableList<Move> getMove() {
		return move;
	}
	public void setMove(ObservableList<Move> move) {
		this.move = move;
	}
	public Move getItemMove(int key) {
		return move.get(key);
	}
	public void setItemMove(Move value) {
		this.move.add(value);
	}*/
	public String getFullName() {
		return first_name+" "+name+" "+patronymic;
	}

}
