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

import org.apache.commons.lang.StringUtils;

import application.controllers.pages.ConnectSectionPLU;
import application.controllers.windows.SectionCtrl;
import application.models.net.PackingDBValue;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundSize;

public class SectionsPLU {
	private ConnectSectionPLU parentControler;
	private int code_goods = 0;
	private int id_sections = 0;
	private Goods plu = null;
	//private Section sectionServer = null;
	//private Section sectionScale = null;
	private CheckBox checkBox = new CheckBox();
	private ComboBox<String> nameSSer = new ComboBox<String>();
	private int numberSer = 0;
	private ComboBox<String> nameSSca = new ComboBox<String>();
	private int numberSca = 0;
	private String namePlU = "";
	private int codePLU = 0;
	
	public static String[] getTable() {
		String[] table = {"sections_goods","goods","sections"};
		return table;
	}
	public List<String> getFields() {
		SectionsPLU me = this;
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				String table = getTable()[0];
				if(f.getName()=="code_goods"||f.getName()=="id_sections")
				fields.add(table+"."+f.getName());
				else
					continue;
				//System.out.println(table+"."+f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public PackingDBValue[] getValues() {
		SectionsPLU me = this;
		PackingDBValue[] values = new PackingDBValue[2];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				if(f.getName()=="code_goods"||f.getName()=="id_sections") {
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
							System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
					}
				}else
					continue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	public SectionsPLU(ResultSet res,Goods plu, /*DBHandler dbScale, DBHandler dbServer,*/ ConnectSectionPLU parentControler) {
		super();
		plu.archive();
		this.parentControler = parentControler;
		//DBHandler dbScale;
		for (Field f : getClass().getDeclaredFields()) {
			try {
				if(f.getName()=="nameSSer"||f.getName()=="numberSer"||f.getName()=="nameSSca"||f.getName()=="numberSca"||f.getName()=="namePlU"||f.getName()=="codePLU") continue;
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
					case "Goods":{
						this.plu = plu;
						this.namePlU = plu.getName();
						this.codePLU = plu.getCode();
						this.code_goods = plu.getCode();
						break;
					}
					default:
						System.out.println(getTable()+": type was not found " + f.getName()+":"+f.getType());
				}
			} catch (Exception e) {
				System.out.println(getTable()+": "+e);
			}
		}
		if(parentControler!=null) {
			MySQL dbServer = parentControler.getParentControler().getDb();
	    	SectionCtrl sectionScale = new SectionCtrl(res,"s");
	    	SectionCtrl sectionServer = null;
	    	Goods pluServer = new Goods().get(plu.getCode(), 0, 0, "", dbServer);
	    	if(dbServer!=null&&pluServer!=null) {
	    		sectionServer = sectionScale.get(pluServer.getId_sections(), 0, "", dbServer);
	    	}
			/*this.sectionServer = sectionServer;
			this.sectionScale = sectionScale;*/
			if(sectionServer!=null) {
				this.nameSSer.setValue(sectionServer.getName());
				this.numberSer = sectionServer.getNumber();
			}
			if(sectionScale!=null) {
				this.nameSSca.setValue(sectionScale.getName());
				this.numberSca = sectionScale.getNumber();
				this.id_sections = sectionScale.getNumber();
			}
		}
		if(parentControler!=null)this.initialize();
	}
	public SectionsPLU() {}
	public void initialize() {
		SectionCtrl section = new SectionCtrl();
		MySQL dbScale = parentControler.getScale().getDb();
		MySQL dbServer = parentControler.getParentControler().getDb();
		this.nameSSer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			SectionCtrl sectionServer = section.get(0, 0, newSelection, dbServer);
			if(sectionServer!=null) {
				plu.setId_sections(sectionServer.getId());
				plu.save(dbServer);
				numberSer = sectionServer.getNumber();
				parentControler.getTableView().refresh();
			}
		});
		this.nameSSca.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			SectionCtrl sectionScale = section.get(0, 0, newSelection, dbScale);
			if(sectionScale!=null) {
				plu.setId_sections(sectionScale.getNumber());
				plu.save(dbScale);
				this.numberSca = sectionScale.getNumber();
				this.id_sections = sectionScale.getNumber();
				this.save(dbScale);
				parentControler.getTableView().refresh();
			}
		});
	}
	public SectionsPLU get(int code, int sId, ConnectSectionPLU parentControler) {
		MySQL dbScale = parentControler.getScale().getDb();
		MySQL dbServer = parentControler.getParentControler().getDb();
    	ResultSet resul = dbScale.getSelect(this.getSql(code, sId, dbScale, false));
    	Goods pluScale = new Goods(resul,"g");
    	SectionsPLU row = null;
    	ObservableList<String> listSectionsServer = SectionCtrl.getLName(0, dbServer);
    	ObservableList<String> listSectionsScale = SectionCtrl.getLName(0, dbScale);
    	try {
			while(resul.next()) {
				row = new SectionsPLU(resul,pluScale,parentControler);
				row.getNameSSer().setItems(listSectionsServer);
				row.getNameSSca().setItems(listSectionsScale);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	resul = null;
		return row;
	}
	public ObservableList<SectionsPLU> getList(int code, int sId, ConnectSectionPLU parentControler, boolean replay) {
		Scales scale = parentControler.getScale();
		MySQL dbServer = parentControler.getParentControler().getDb();
    	ResultSet resul = scale.getDb().getSelect(this.getSql(code, sId, scale.getDb(), replay));
    	ObservableList<SectionsPLU> row = FXCollections.observableArrayList();
    	ObservableList<String> listSectionsServer = SectionCtrl.getLName(0, dbServer);
    	ObservableList<String> listSectionsScale = SectionCtrl.getLName(0, scale.getDb());
    	try {			while(resul.next()) {
				Goods pluScale = new Goods(resul,"g");
		    	Goods pluServer = pluScale.get(pluScale.getCode(), 0, 0, "", dbServer);
		    	if(pluServer==null) {
		    		pluScale.setId_scales(scale.getId());
		    		pluScale.save(dbServer);
		    	}
		    	SectionsPLU splu = new SectionsPLU(resul,pluScale,parentControler);
		    	splu.getNameSSer().setItems(listSectionsServer);
		    	splu.getNameSSca().setItems(listSectionsScale);
				row.add(splu);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	resul = null;
		return row;
	}
	public String getSql(int code, int sId, MySQL db, boolean replay) {
		String[] table = getTable();
		String sql = "SELECT "+StringUtils.join(new Goods().getFields(table[1], "g"), ",")+","+StringUtils.join(new SectionCtrl().getFields("s"), ",")+", "+table[0]+".id_sections, "+table[0]+".code_goods FROM ";
		if(db.getDBIndex()==0) {
			sql += table[1] + " LEFT JOIN "+table[2]+" ON "+table[1]+".id_sections = "+table[2]+".id";
			if(code > 0||sId > 0) sql += " WHERE ";
			if(code > 0) sql+= table[0]+".code = "+code;
			if(code > 0&&sId > 0) sql += " AND ";
			if(sId > 0)	sql += table[0]+".id_sections = "+sId;
			if(!replay) sql += " GROUP BY "+table[1]+".name HAVING COUNT("+table[1]+".name) > 0";
	    	sql += " ORDER BY "+table[1]+".name";
		}else {
			sql += table[0];
			sql += " RIGHT JOIN "+table[1]+" ON "+table[0]+".code_goods = "+table[1]+".code";
			sql += " LEFT JOIN "+table[2]+" ON "+table[0]+".id_sections = "+table[2]+".id";
			if(code > 0||sId > 0) sql += " WHERE ";
			if(code > 0) sql+= table[0]+".code_goods = "+code;
			if(code > 0&&sId > 0) sql += " AND ";
			if(sId > 0)	sql += table[0]+".id_sections = "+sId;
			if(!replay) sql += " GROUP BY "+table[1]+".name HAVING COUNT("+table[1]+".name) > 0";
	    	sql += " ORDER BY "+table[1]+".name";
		}
		return sql;
	}
	public boolean check(SectionsPLU SPLU, MySQL db) {
    	ResultSet resul = db.getSelect(this.getSql(SPLU.getCode_goods(),0,db,false));
    	try {
			while(resul.next()) {
				//new SectionsPLU(resul,null,null);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	public boolean save(MySQL db) {
		SectionsPLU me = this;
		String[] table = getTable();
		String[] fields = me.getFields().toArray(new String[0]);
		if(db.getDBIndex()==0) {
			if(this.check(me,db)) {
				me.getPlu().setId_sections(me.getId_sections());
				me.getPlu().save(db);
			}else {
				(new SectionCtrl().get(0, 0, me.getNameSSca().getValue(), parentControler.getScale().getDb())).save(db);
				me.getPlu().setId_sections(me.getId_sections());
				me.getPlu().save(db);
			}
		}else {
			if(this.check(me,db)) {
				db.update(table[0],fields, me.getValues(), new String[]{table[0]+".code_goods = "+me.getCode_goods()});
			}else {
				db.insert(table[0], fields, me.getValues());
			}
		}
		return this.check(me,db);
	}
	public boolean delete(MySQL db) {
		SectionsPLU me = this;
		String table = getTable()[0];
		if(this.check(me,db)) {
			db.delete(table, new String[]{table+".code_goods =\""+me.getCode_goods()+"\""});
		}
		return (this.check(me,db))?false:true;
	}
	public int getCode_goods() {
		return code_goods;
	}
	public void setCode_goods(int code_goods) {
		this.code_goods = code_goods;
	}
	public int getId_sections() {
		return id_sections;
	}
	public void setId_sections(int id_sections) {
		this.id_sections = id_sections;
	}
	public Goods getPlu() {
		return plu;
	}
	public void setPlu(Goods plu) {
		this.plu = plu;
	}
	/*public Section getSectionServer() {
		return sectionServer;
	}
	public void setSectionServer(Section sectionServer) {
		this.sectionServer = sectionServer;
	}
	public Section getSectionScale() {
		return sectionScale;
	}
	public void setSectionScale(Section sectionScale) {
		this.sectionScale = sectionScale;
	}*/
	public CheckBox getCheckBox() {
		return checkBox;
	}
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}
	public int getNumberSer() {
		return numberSer;
	}
	public void setNumberSer(int numberSer) {
		this.numberSer = numberSer;
	}
	public int getNumberSca() {
		return numberSca;
	}
	public void setNumberSca(int numberSca) {
		this.numberSca = numberSca;
	}
	public String getNamePlU() {
		return namePlU;
	}
	public void setNamePlU(String namePlU) {
		this.namePlU = namePlU;
	}
	public int getCodePLU() {
		return codePLU;
	}
	public void setCodePLU(int codePLU) {
		this.codePLU = codePLU;
	}
	public ComboBox<String> getNameSSer() {
		return nameSSer;
	}
	public void setNameSSer(ComboBox<String> nameSSer) {
		this.nameSSer = nameSSer;
	}
	public ComboBox<String> getNameSSca() {
		return nameSSca;
	}
	public void setNameSSca(ComboBox<String> nameSSca) {
		this.nameSSca = nameSSca;
	}
}
