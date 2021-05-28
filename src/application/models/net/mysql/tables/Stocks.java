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

public class Stocks {
	private int code_goods = 0;
	private float new_price = 0;
	private boolean visible = false;
	
	public static String[] getTable() {
		String[] table = {"stocks"};
		return table;
	}
	public List<String> getFields(int i) {
		List<String> table = new ArrayList<String>();
		table = this.getFields();
		return table;
	}
	public List<String> getFields() {
		Stocks me = this;
		String table = getTable()[0];
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				//if(f.getName()=="inst"||f.getName()=="id"||f.getName()=="db") continue;
				System.out.println(table+"."+f.getName());
				fields.add(table+"."+f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		Stocks me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length-1];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				//if(f.getName()=="inst"||f.getName()=="id"||f.getName()=="db") continue;
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
					case "boolean":
						values[i++] = new PackingDBValue((Object)f.get(me),"BL");
					break;
					default:
						System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	public Stocks(ResultSet res) {
		super();
		try {
			for (Field f : getClass().getDeclaredFields()) {
				//if(f.getName()=="inst") continue;
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
					case "boolean":
						f.set(this,res.getBoolean(res.findColumn(f.getName())));
					break;
					default:
						System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(getTable()+": "+e);
		}
	}
	public Stocks() {}
	public static ObservableList<Stocks> get(int pre_code, MySQL db) {
    	ResultSet resul = db.getSelect(Stocks.getSql(pre_code));
    	ObservableList<Stocks> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new Stocks(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static String getSql(int pre_code) {
		String table = getTable()[0];
		String sql = "SELECT * FROM "+table;
		if(pre_code > 0) sql+= " where "+table+".code_goods = "+pre_code;
		return sql;
	}
	public boolean save(MySQL db) {
		Stocks me = this;
		if(me.getCode_goods()!=0) {
			String[] table = getTable();
			Stocks isNew = Stocks.get(me.getCode_goods(),db).get(0);
			if(isNew==null){
				db.insert(table[0], me.getFields().toArray(new String[0]), me.getValues());
			}else {
				db.update(table[0], me.getFields().toArray(new String[0]), me.getValues(), new String[]{table[0]+".code_goods ="+me.getCode_goods()});
			}
			;
		}
		return Stocks.get(me.getCode_goods(),db).get(0)!=null;
	}
	public void updateCode(int code,MySQL db) {
		Stocks me = this;
		String table = getTable()[0];
		Stocks isNew = Stocks.get(code,db).get(0);
		if(isNew==null)db.update(table,new String[]{(table+".code_goods")}, PackingDBValue.get(new String[] {"int"}, new Object[] {code}), new String[]{table+".code_goods = "+me.getCode_goods()});
		if(Stocks.get(code,db)!=null)this.setCode_goods(code);
	}
	public int delete(MySQL db) {
		int result = 0;
		Stocks me = this;
		String table = getTable()[0];
		if(me.getCode_goods()>0) {
			result = db.delete(table, new String[]{table+".code_goods = "+me.getCode_goods()});
		}
		
		return result;
	}
	public int getCode_goods() {
		return code_goods;
	}
	public void setCode_goods(int code_goods) {
		this.code_goods = code_goods;
	}
	public float getNew_price() {
		return new_price;
	}
	public void setNew_price(float new_price) {
		this.new_price = new_price;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
