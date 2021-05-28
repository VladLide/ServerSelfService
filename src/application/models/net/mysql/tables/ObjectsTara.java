package application.models.net.mysql.tables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import application.models.PackingDBValue;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundSize;

public class ObjectsTara {
	private int id = 0;
	private String name = "";
	private String description = "";
	private float tara = 0;
	private Blob img_data = null;
	
	public static String getTable() {
		String table = "objects_tara";
		return table;
	}
	public List<String> getFields() {
		ObjectsTara me = this;
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				if(f.getName()=="inst"||f.getName()=="db") continue;
				System.out.println(table+"."+f.getName());
				fields.add(table+"."+f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		ObjectsTara me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length];
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
						System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	public ObjectsTara(ResultSet res, MySQL db) {
		super();
		//inst = this;
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
					case "Blob":
						f.set(this,res.getBlob(res.findColumn(f.getName())));
					break;
					default:
						System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
				}
			} catch (Exception e) {
				System.out.println(getTable()+": "+e);
			}
		}
	}
	public ObjectsTara() {}
	public static ObjectsTara get(int sort, int id, String name, MySQL db) {
    	ResultSet resul = db.getSelect(ObjectsTara.getSql(sort,id,name));
    	ObjectsTara row = null;
    	try {
			while(resul.next()) {
				row = new ObjectsTara(resul,db);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<ObjectsTara> getList(int sort, int id, String name, MySQL db) {
		ResultSet resul = db.getSelect(ObjectsTara.getSql(sort,id,name));
    	ObservableList<ObjectsTara> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new ObjectsTara(resul,db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<Object> getListObj(int sort, int id, String name, MySQL db) {
		ResultSet resul = db.getSelect(ObjectsTara.getSql(sort,id,name));
    	ObservableList<Object> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new ObjectsTara(resul,db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static ObservableList<ObjectsTara> getInfo(int sort, int id, String name, MySQL db) {
    	ResultSet resul = db.getSelect(ObjectsTara.getSqlInfo(sort, id, name));
    	ObservableList<ObjectsTara> row = FXCollections.observableArrayList();
    	try {
			while(resul.next()) {
				row.add(new ObjectsTara(resul,db));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}
	public static String getSql(int sort, int id, String name) {
		String table = getTable();
		String sql = "SELECT * FROM "+table;
		if((id > 0)||(name.length()>0)) sql+= " WHERE ";
		if((id > 0)) sql+= table+".id = "+id;
		if((id > 0)&&(name.length()>0)) sql+= " AND ";
		if((name.length()>0)) sql+= table+".name = '"+name+"'";
		switch (sort) {
		case 0:
			sql+=" ORDER BY "+table+".id";
			break;
		case 1:
			sql+=" ORDER BY "+table+".name";
			break;
		case 2:
			sql+=" ORDER BY "+table+".tara";
			break;
		default:
			sql+=" ORDER BY "+table+".id";
			break;
		}
		return sql;
	}
	public static String getSqlInfo(int sort, int id, String name) {
		String table = getTable();
		String sql = "SELECT id, name, description, tara FROM "+table;
		if((id > 0)||(name.length()>0)) sql+= " WHERE ";
		if((id > 0)) sql+= table+".id = "+id;
		if((id > 0)&&(name.length()>0)) sql+= " AND ";
		if((name.length()>0)) sql+= table+".name = '"+name+"'";
		switch (sort) {
		case 0:
			sql+=" ORDER BY "+table+".id";
			break;
		case 1:
			sql+=" ORDER BY "+table+".name";
			break;
		case 2:
			sql+=" ORDER BY "+table+".tara";
			break;
		default:
			sql+=" ORDER BY "+table+".id";
			break;
		}
		return sql;
	}
	public int save(MySQL db) {
		String table = getTable();
		ObjectsTara isNew = ObjectsTara.get(0,id,"",db);
		String[] fields = getFields().toArray(new String[0]);
		PackingDBValue[] values = getValues();
		if(isNew==null) {
			db.insert(table, fields, values);
		}else {
			db.update(table, fields, values, new String[]{table+".id ="+isNew.getId()});
		}
		ObjectsTara tmp = ObjectsTara.get(0,id,"",db);
		id = tmp!=null?tmp.getId():-1;
		return id;
	}
	public boolean updateId(int id, MySQL db) {
		String table = getTable();
		ObjectsTara isNew = ObjectsTara.get(0,id,"",db);
		if(isNew==null)db.update(table,new String[]{(table+".id")}, PackingDBValue.get(new String[] {"int"}, new Object[] {id}), new String[]{table+".id = "+this.id});
		isNew = ObjectsTara.get(0,id,"",db);
		if(isNew!=null)this.id = id;
		return isNew!=null;
	}
	/*public boolean updateName(String name,MySQL db) {
		String table = getTable();
		ObjectsTara isNew = ObjectsTara.get(0,0,name,db);
		if(isNew==null)db.update(table,new String[]{(table+".name")}, PackingDBValue.get(new String[] {"String"}, new Object[] {name}), new String[]{table+".name = '"+this.name+"'"});
		isNew = ObjectsTara.get(0,0,name,db);
		if(isNew!=null)this.name = name;
		return isNew!=null;
	}*/
	public boolean delete(MySQL db) {
		String table = getTable();
		if(id>0) {
			db.delete(table, new String[]{table+".id ='"+id+"'"});
		}
		ObjectsTara old = ObjectsTara.get(0,id,"",db);
		return old==null;
	}

	public Image getImage(AnchorPane panel) {
		InputStream is = null;
		Image image = null;
		try {
			is = (this.img_data!=null)?this.img_data.getBinaryStream():null;
			image = new Image(is, BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return image;
	}
	public Image getImage() {
		InputStream is = null;
		Image image = null;
		try {
			is = (this.img_data!=null)?this.img_data.getBinaryStream():null;
			image = new Image(is);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return image;
	}
	public void setData(File file) {
		try {
			Path filePath = Paths.get(file.getAbsolutePath());
			byte[] fileContent = Files.readAllBytes(filePath);
			this.img_data = new SerialBlob(fileContent);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SerialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int i) {
		this.id = i;
	}
	public Blob getImg_data() {
		return img_data;
	}
	public void setImg_data(Blob img_data) {
		this.img_data = img_data;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getTara() {
		return tara;
	}
	public String getStringTara(){
		String realT = tara+"";
		while((realT.length()-1-realT.indexOf("."))<3) {
			realT += "0";
		}
		return realT;
	}
	public void setTara(float tara) {
		this.tara = tara;
	}
}