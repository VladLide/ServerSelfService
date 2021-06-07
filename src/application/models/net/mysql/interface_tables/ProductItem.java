package application.models.net.mysql.interface_tables;

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

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import application.models.net.mysql.MySQL;
import application.models.net.mysql.tables.Codes;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Sections;
import application.models.net.mysql.tables.Stocks;
import application.models.net.mysql.tables.Templates;
import application.views.languages.uk.windows.ProductInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundSize;

public class ProductItem {
	private int num = 0;
	private int number = 0;
	private String name = "";
	private String full_name = "";
	private float price = 0;
	private int type = 0;
	private String typeName = "";
	private int pre_code = 0;
	private int before_validity = 0;
	private String ingredients = "";
	private Blob data = null;
	private float min_type = 0;
	private Sections section;
	private Templates template;
	private Codes code;
	// private Stocks stock = new Stocks();

	public static String getTable() {
		return "goods";
	}

	public ProductItem() {}

	public ProductItem(int i, ResultSet res, MySQL db) {
		super();
		this.num = i;
		for (Field f : getClass().getDeclaredFields()) {
			if (f.getName() == "inst" || f.getName() == "num")
				continue;
			String type = f.getType().getTypeName().replace(".", " ");
			if (type.split(" ").length > 0) {
				String[] typ = type.split(" ");
				type = typ[typ.length - 1];
			}
			try {
				switch (type) {
				case "int": {
					if (f.getName() == "number") {
						f.set(this, res.getInt(res.findColumn("code")));
					} else
						f.set(this, res.getInt(res.findColumn(f.getName())));
				}
					break;
				case "float": {
					float val = res.getFloat(res.findColumn(f.getName()));
					if (f.getName().compareToIgnoreCase("price") == 0) {
						val = Math.round(val * 100F) / 100F;
					} else {
						val = Math.round(val * 1000F) / 1000F;
					}
					f.set(this, val);
					break;
				}
				case "double":
					f.set(this, res.getDouble(res.findColumn(f.getName())));
					break;
				case "String": {
					if (f.getName() == "typeName") {
						f.set(this, ProductInfo.unit.get(this.type));
					} else
						f.set(this, res.getString(res.findColumn(f.getName())).toUpperCase());
				}
					break;
				case "Blob":
					f.set(this, res.getBlob(res.findColumn(f.getName())));
					break;
				case "Sections": {
					try {
						f.set(this, new Sections(res.getInt(res.findColumn("sId")),
								res.getString(res.findColumn("sName"))));
					} catch (Exception e) {
						f.set(this, new Codes());
					}
					break;
				}
				case "Templates": {
					try {
						f.set(this,
								new Templates(res.getInt(res.findColumn("tId")),
										res.getString(res.findColumn("tName")).toUpperCase(),
										res.getBlob(res.findColumn("tData")),
										res.getBlob(res.findColumn("templatesImg_data")),
										res.getBlob(res.findColumn("background_data"))
								)
						);
					} catch (Exception e) {
						f.set(this, new Templates());
					}
					break;
				}
				case "Codes": {
					try {
						f.set(this, new Codes(res.getInt(res.findColumn("cId")), res.getString(res.findColumn("cname")),
								res.getString(res.findColumn("cmaska")), res.getString(res.findColumn("cmask"))));
					} catch (Exception e) {
						f.set(this, new Codes());
					}
					break;
				}
				/*case "Stocks":{
					try {
						this.stock = Stocks.get(getPre_code(), db).get(0);
					}catch (Exception e1) {
						this.stock = new Stocks();
					}
					break;
				}*/
				default:
					System.out.println("PLUInfo: type was not found " + f.getName() + " : " + f.getType());
				}
			} catch (Exception e) {
				System.out.println("PLUInfo: " + e);
			}
		}
	}

	public static String getSqlHeader(int type) {
		String sql = "*";
		switch (type) {
		case 1:
			sql = "id, id_up, level, name, description, number_s, number_po";
			break;
		case 2:
			sql = "id, id_up, level, name, description, number_s, number_po";
			break;
		case 3:
			sql = "id, id_up, level, name, description, number_s, number_po";
			break;
		default:
			break;
		}
		return sql;
	}

	public static String getSql(int sId, String name, int number, int pre_code, int union, int limit, boolean like) {
		String table = getTable();
		String[] unionList = {" LEFT ", " INNER ", " RIGHT "};
		String sql = "SELECT *, "
				+ "sections.id as sId, "
				+ "sections.id as sId, "
				+ "sections.name as sName, "
				+ "templates.id as tId, "
				+ "templates.name as tName, "
				+ "templates.data as tData, "
				+ "templates.img_data as tImg_data, "
				+ "barcodes.id as cId, "
				+ "barcodes.name as cname, "
				+ "barcodes.mask as cmask, "
				+ "CONCAT(barcodes.prefix,'|', barcodes.code,'|', barcodes.unit,'|',barcodes.prefix_val) as cmaska "
				+ "FROM " + table
				+ " LEFT JOIN stocks ON stocks.code_goods = " + table + ".pre_code "
				+ unionList[union] + " JOIN sections ON sections.id = " + table + ".id_sections "
				+ unionList[union] + " JOIN templates ON templates.id = " + table + ".id_templates "
				+ unionList[union] + " JOIN barcodes ON barcodes.id = " + table + ".id_barcodes ";
		if (sId > 0 || name.length() > 0 || number > 0 || pre_code > 0)
			sql += " WHERE ";
		if (number > 0)
			sql += table + ".code = " + ((like) ? ("LIKE \"" + number + "%\"") : ("\"" + number + "\" "));
		if (number > 0 && (sId > 0 || name.length() > 0))
			sql += " AND ";
		if (sId > 0)
			sql += table + ".id_scales = " + sId;
		if (sId > 0 && name.length() > 0)
			sql += " AND ";
		if (name.length() > 0)
			sql += table + ".name = " + ((like) ? ("LIKE \"%" + name + "%\"") : ("\"" + name + "\""));
		sql += " ORDER BY " + table + ".code ";
		if (limit > 0)
			sql += " LIMIT " + limit + " ";
		return sql;
	}

	public static ProductItem get(int sId, String name, int number, int pre_code, int union, int limit, boolean like,
			MySQL db) {
		ResultSet resul = db.getSelect(ProductItem.getSql(sId, name, number, pre_code, union, limit, like));
		ProductItem res = null;
		try {
			while (resul.next()) {
				res = new ProductItem(1, resul, db);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static ObservableList<ProductItem> getList(int sId, String name, int number, int pre_code, int union,
			int limit, boolean like, MySQL db) {
		ResultSet resul = db.getSelect(ProductItem.getSql(sId, name, number, pre_code, union, limit, like));
		ObservableList<ProductItem> row = FXCollections.observableArrayList();
		try {
			int num = 1;
			while (resul.next()) {
				row.add(new ProductItem(num, resul, db));
				num++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static ObservableList<Object> getListObj(int sId, String name, int number, int pre_code, int union,
			int limit, boolean like, MySQL db) {
		ResultSet resul = db.getSelect(ProductItem.getSql(sId, name, number, pre_code, union, limit, like));
		ObservableList<Object> row = FXCollections.observableArrayList();
		try {
			int num = 1;
			while (resul.next()) {
				row.add(new ProductItem(num, resul, db));
				num++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public int save(int idScale, int idSection, int idBarcode, MySQL db) {
		Goods plu = new Goods();
		ProductItem me = this;
		for (Field f : me.getClass().getDeclaredFields()) {
			if (f.getName() == "inst" || f.getName() == "sectionsName" || f.getName() == "template"
					|| f.getName() == "barcode")
				continue;
			try {
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				plu.set(f.getName(), f.get(this), type);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		plu.set("id_section", idSection, "int");
		plu.set("id_templates", this.getTemplate().getId(), "int");
		plu.set("id_barcode", idBarcode, "int");
		plu.set("is_min_weight", 0, "int");
		return plu.save(db);
	}

	public Image getImage(AnchorPane panel) {
		InputStream is = null;
		Image image = null;
		try {
			if (this.data != null) {
				is = this.data.getBinaryStream();
				image = new Image(is, BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true,
						true);
			}
		} catch (SQLException e) {
			System.out.println("Image: no image - " + e);
		} finally {
			if (is != null) {
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
			if (this.data != null) {
				is = this.data.getBinaryStream();
				image = new Image(is);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
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
			this.data = new SerialBlob(fileContent);
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

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	public Templates getTemplate() {
		return (template != null) ? template : new Templates();
	}

	public void setTemplate(Templates template) {
		this.template = template;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int code) {
		this.number = code;
	}

	public int getPre_code() {
		return pre_code;
	}

	public void setPre_code(int pre_code) {
		this.pre_code = pre_code;
	}

	public int getBefore_validity() {
		return before_validity;
	}

	public void setBefore_validity(int before_validity) {
		this.before_validity = before_validity;
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public Sections getSection() {
		return section;
	}

	public void setSection(Sections sections) {
		this.section = sections;
	}

	public Blob getImages() {
		return this.data;
	}

	public void setImages(Blob img) {
		this.data = img;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public Codes getCode() {
		return code;
	}

	public void setCode(Codes barcode) {
		this.code = barcode;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public float getMin_type() {
		return min_type;
	}

	public void setMin_type(float min_type) {
		this.min_type = min_type;
	}

	/*public Stocks getStock() {
		return stock;
	}
	public void setStock(Stocks stock) {
		this.stock = stock;
	}*/

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
