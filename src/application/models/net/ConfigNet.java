package application.models.net;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.controllers.MainCtrl;
import application.models.Configs;
import application.models.Utils;
import application.models.net.mysql.MySQL;


public class ConfigNet {
	protected Integer id_scales = 0;
	protected String host = "localhost";
	protected String port = "3306";
	protected String login = "root";
	protected String pass = "12345";
	protected String name = "db_scale";
	private String table = "scales_confignet";

	public ConfigNet(Integer id) throws SQLException {
		super();
		if(id!=null) {
			id_scales = id;
			ResultSet res = (id>0)?MainCtrl.getDB().getSelect(getSql(id)):null;
			if(res!=null)res.first();
			for (Field f : getClass().getDeclaredFields()) {
				try {
					if(f.getName()=="table") continue;
					f.set(this,(id>0)?res.getObject(res.findColumn(f.getName())):Configs.getItemObj(f.getName()));
				} catch (Exception e) {
					System.out.println(table+": "+e.getMessage());
				}
			}
			if(res!=null)res.close();
		}
	}
	
	public ResultSet get(int id) {
    	ResultSet resul = MainCtrl.getDB().getSelect(getSql(id));
    	try {
			while(resul.next()) {
				return resul;
			}
		} catch (SQLException e) {
			System.out.println(table+": "+e.getMessage());
		}
		return null;
	}
	public String getSql(int id) {
		String sql = "SELECT * FROM "+table ;
		if(id > 0) sql += " WHERE ";
		if(id > 0) sql += table+".id_scales = "+id;
		return sql;
	}
	public List<String> getFields() {
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : this.getClass().getDeclaredFields()) {
				if(f.getName()=="table"||f.getName()=="config"||f.getName()=="db") continue;
				//System.out.println(table+"."+f.getName());
				fields.add(table+"."+f.getName());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		PackingDBValue[] values = new PackingDBValue[this.getClass().getDeclaredFields().length-1];
		int i = 0;
		for (Field f : this.getClass().getDeclaredFields()) {
			try {
				if(f.getName()=="table") continue;
				switch(Utils.getType(f)) {
					case "Integer":
						values[i++] = new PackingDBValue(f.getName(),"I",(Object)f.get(this));
					break;
					case "int":
						values[i++] = new PackingDBValue(f.getName(),"I",(Object)f.get(this));
					break;
					case "float":
						values[i++] = new PackingDBValue(f.getName(),"F",(Object)f.get(this));
					break;
					case "double": 
						values[i++] = new PackingDBValue(f.getName(),"D",(Object)f.get(this));
					break;
					case "String":
						values[i++] = new PackingDBValue(f.getName(),"S",(Object)f.get(this));
					break;
					default:
						System.out.println(table+" - value: type was not found '" + f.getName()+"' : '"+f.getType()+"'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	public boolean save(MySQL db) {
		if(get(id_scales)!=null) {
			db.update(table,getFields().toArray(new String[0]), getValues(), new String[]{table+".id_scales ="+getId_scales()});
		}else {
			db.deleteAll(table);
			db.insert(table, getFields().toArray(new String[0]), getValues());
		}
		return get(id_scales)!=null;
	}

	public int getId_scales() {
		return id_scales;
	}
	public void setId_scales(int id_scales) {
		this.id_scales = id_scales;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String dbHost) {
		this.host = dbHost;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String dbPort) {
		this.port = dbPort;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String dbUser) {
		this.login = dbUser;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String dbPass) {
		this.pass = dbPass;
	}
	public String getName() {
		return name;
	}
	public void setName(String dbName) {
		this.name = dbName;
	}
}
