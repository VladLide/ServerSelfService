package application.models.net.database.mysql.tables;

import application.models.net.PackingDBValue;
import application.models.net.database.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
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
import java.util.Map;

public class Goods {
	private int id = 0;
	private int code = 0;
	private int id_scales = 0;
	private int id_sections = 0;
	private int id_templates = 0;
	private int id_barcodes = 0;
	private String name = "";
	private String full_name = "";
	private float price = 0;
	private int type = 0;
	private int pre_code = 0;
	private int before_validity = 0;
	private String ingredients = "";
	private float min_type = 0;
	private Blob data = null;
	private float tara = 0;

	public static String getTable() {
		return "goods";
	}

	public List<String> getFields() {
		Goods me = this;
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
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
		Goods me = this;
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
					System.out.println(getTable() + ": type was not found " + f.getName() + ":" + f.getType());
				}
			} catch (Exception e) {
				System.out.println(getTable() + ": " + e.getMessage());
			}
		}
		return values;
	}

	public Goods(ResultSet res, int[] id) {
		super();
		if (this.getId_barcodes() == 0)
			this.setId_barcodes(id[0]);
		if (this.getId_templates() == 0)
			this.setId_templates(id[1]);
		try {
			for (Field f : getClass().getDeclaredFields()) {
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
				case "String":
					f.set(this, res.getString(res.findColumn(f.getName())));
					break;
				case "Blob":
					f.set(this, res.getBlob(res.findColumn(f.getName())));
					break;
				default:
					System.out.println(getTable() + ": type was not found " + f.getName() + ":" + f.getType());
				}
			}
		} catch (Exception e) {
			System.out.println(getTable() + ": " + e.getMessage());
		}
	}

	public Goods(Map<String, String> value, int[] id) {
		super();
		if (this.getId_barcodes() == 0)
			this.setId_barcodes(id[0]);
		if (this.getId_templates() == 0)
			this.setId_templates(id[1]);
		for (Field f : getClass().getDeclaredFields()) {
			try {
				String s = value.get(f.getName());
				if (s != null) {
					if (f.getName() == "inst")
						continue;
					if (f.getName() == "id") {
						f.set(this, 0);
						continue;
					}
					String type = f.getType().getTypeName().replace(".", " ");
					if (type.split(" ").length > 0) {
						String[] typ = type.split(" ");
						type = typ[typ.length - 1];
					}
					switch (type) {
					case "int":
						try {
							if (f.getName().compareToIgnoreCase("type") == 0)
								f.set(this, Integer.parseInt(value.get("type")));
							else
								f.set(this, Integer.parseInt(value.get(f.getName())));
						} catch (Exception e) {
							if (f.getName().compareToIgnoreCase("type") == 0) {
								String t = value.get("type").replace(",", "").replace(" ", "").replace(".", "");
								switch (t) {
								case "кг": {
									f.set(this, 0);
									break;
								}
								case "шт": {
									f.set(this, 1);
									break;
								}
								}
							} else
								f.set(this, 0);
						}
						break;
					case "float": {
						Float n = 0F;
						try {
							n = Float.parseFloat(value.get(f.getName()).replace(",", "."));
							if (f.getName() == "price")
								n = Math.round(n * 100F) / 100F;
							else
								n = Math.round(n * 1000F) / 1000F;
						} catch (Exception e) {
							n = 0F;
						}
						f.set(this, n);
						break;
					}
					case "double": {
						try {
							f.set(this, Double.parseDouble(value.get(f.getName()).replace(",", ".")));
						} catch (Exception e) {
							f.set(this, 0);
						}
						break;
					}
					case "String": {
						if (f.getName() == "ingredients")
							f.set(this, value.get(f.getName()));
						else if (f.getName().compareToIgnoreCase("type") == 0)
							try {
								int j = Integer.parseInt(value.get(f.getName()));
								if (j == 0)
									f.set(this, "кг.");
								else
									f.set(this, "шт.");
							} catch (Exception e) {
								f.set(this, value.get(f.getName()).replace(",", "."));
							}
						else
							f.set(this, value.get(f.getName()).replace(",", "."));
						break;
					}
					case "Blob": {
						File file = new File(value.get(f.getName()));
						this.setData(file);
						break;
					}
					default:
						System.out.println(getTable() + ": type was not found " + f.getName() + ":" + f.getType());
					}
				}
			} catch (Exception e) {
				this.id = -1;
				System.out.println(getTable() + ": " + e);
			}
		}
	}

	public Goods() {}

	public void set(String name, Object value, String type) {
		try {
			switch (type) {
			case "int":
				this.getClass().getField(name).set(this, (int) value);
				break;
			case "float": {
				float val = (float) value;
				if (name.compareToIgnoreCase("price") == 0) {
					val = Math.round(val * 100F) / 100F;
				} else {
					val = Math.round(val * 1000F) / 1000F;
				}
				this.getClass().getField(name).set(this, val);
				break;
			}
			case "double":
				this.getClass().getField(name).set(this, (float) value);
				break;
			case "String":
				this.getClass().getField(name).set(this, (float) value);
				break;
			case "Blob":
				this.getClass().getField(name).set(this, (float) value);
				break;
			default:
				System.out.println(getTable() + ": type was not found " + name + ":" + type);
			}
		} catch (Exception e) {
			System.out.println(getTable() + ": " + e.getMessage());
		}
	}

	public static Goods get(int number, int pre_code, String name, int sId, int tId, MySQL db) {
		ResultSet resul = db.getSelect(Goods.getSql(number, pre_code, name, sId, tId));
		Goods row = null;
		try {
			while (resul.next()) {
				row = new Goods(resul, new int[] { 0, 0 });
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return row;
	}

	public static ObservableList<Goods> getList(int number, int pre_code, String name, int sId, int tId, MySQL db) {
		ResultSet resul = db.getSelect(Goods.getSql(number, pre_code, name, sId, tId));
		ObservableList<Goods> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Goods(resul, new int[] { 0, 0 }));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return row;
	}

	public static ObservableList<Object> getListObj(int number, int pre_code, String name, int sId, int tId, MySQL db) {
		ResultSet resul = db.getSelect(Goods.getSql(number, pre_code, name, sId, tId));
		ObservableList<Object> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Goods(resul, new int[] { 0, 0 }));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return row;
	}

	public static ObservableList<Goods> getGoodsLoad(int idScale, MySQL db) {
		ResultSet resul = db.getSelect(Goods.getSql(idScale, "distribute"));
		ObservableList<Goods> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Goods(resul, new int[] { 0, 0 }));
			}
		} catch (NullPointerException e) {
			System.out.println("Goods: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static int getCountGoodsLoad(int idScale, MySQL db) {
		ResultSet resul = db.getSelect(Goods.getSqlCount(idScale, "distribute"));
		if (resul != null) {
			try {
				while (resul.next()) {
					return resul.getInt(1);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return -1;
	}

	public static int getCountGoods(int idScale, MySQL db) {
		ResultSet resul = db.getSelect(Goods.getSqlCount(idScale, getTable()));
		try {
			while (resul.next()) {
				return resul.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}

	public static String getSqlCount(int idScale, String table) {
		String sql = "SELECT COUNT(*) FROM " + table;
		if (idScale > 0)
			sql += " WHERE ";
		if (idScale > 0)
			sql += table + ".id_scales = " + idScale;
		return sql;
	}

	public static String getSql(int idScale, String table) {
		String sql = "SELECT * FROM " + table;
		if (idScale > 0)
			sql += " WHERE ";
		if (idScale > 0)
			sql += table + ".id_scales = " + idScale;
		sql += " ORDER BY " + table + ".code";
		return sql;
	}

	public static String getSql(int number, int pre_code, String name, int sId, int tId) {
		String table = getTable();
		String sql = "SELECT * FROM " + table;
		if (number > 0 || pre_code > 0 || sId > 0 || tId > 0 || name.length() > 0)
			sql += " WHERE ";
		if (number > 0)
			sql += table + ".code = \"" + number + "\" ";
		if (number > 0 && (pre_code > 0 || sId > 0 || tId > 0 || name.length() > 0))
			sql += " AND ";
		if (pre_code > 0)
			sql += table + ".pre_code = \"" + pre_code + "\" ";
		if (pre_code > 0 && (sId > 0 || tId > 0 || name.length() > 0))
			sql += " AND ";
		if (sId > 0)
			sql += table + ".id_sections = " + sId;
		if (sId > 0 && (tId > 0 || name.length() > 0))
			sql += " AND ";
		if (tId > 0)
			sql += table + ".id_templates = " + tId;
		if (tId > 0 && name.length() > 0)
			sql += " AND ";
		if (name.length() > 0)
			sql += table + ".name = \"" + name + "\"";
		sql += " ORDER BY " + table + ".code";
		return sql;
	}

	public static int save(Map<String, String> value, MySQL db) {
		Goods me = new Goods(value, new int[] { 0, 0 });
		return me.save(db);
	}

	public int save(MySQL db) {
		if (id != -1 && db != null) {
			String table = getTable();
			Goods isNew = Goods.get(0, pre_code, "", 0, 0, db);
			if (isNew == null) {
				db.insert(table, getFields().toArray(new String[0]), getValues());
			} else {
				db.update(table, getFields().toArray(new String[0]), getValues(),
						new String[] { table + ".pre_code =" + pre_code });
			}
			Goods tmp = Goods.get(0, pre_code, "", 0, 0, db);
			id = (tmp != null) ? tmp.getId() : -1;
		}
		return id;
	}

	public void updatePre_code(int pre_code, MySQL db) {
		String table = getTable();
		Goods isNew = Goods.get(0, pre_code, "", 0, 0, db);
		if (isNew == null)
			db.update(table, new String[] { (table + ".pre_code") },
					PackingDBValue.get(new String[] { "int" }, new Object[] { pre_code }),
					new String[] { table + ".pre_code = " + this.pre_code });
		if (Goods.get(0, pre_code, "", 0, 0, db) != null)
			this.setPre_code(pre_code);
	}

	public int delete(MySQL db) {
		int result = 0;
		Goods me = this;
		String table = getTable();
		if (me.getId() > 0) {
			result = db.delete(table, new String[] { table + ".pre_code = " + me.getPre_code() });
		}
		return result;
	}

	public static boolean delete(int pre_code, MySQL db) {
		String table = getTable();
		db.delete(table, new String[] { table + ".pre_code = " + pre_code });
		return Goods.get(0, pre_code, "", 0, 0, db) == null;
	}

	public Image getImage(AnchorPane panel) {
		InputStream is = null;
		Image image = null;
		try {
			if (this.data != null) {
				is = this.data.getBinaryStream();
				image = new Image(is, panel.getWidth(), panel.getHeight(), true, true);
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
			this.data = null;
			System.out.println(getTable() + ": image not found " + e.getMessage());
		} catch (IOException e) {
			this.data = null;
			System.out.println(getTable() + ": image not found " + e.getMessage());
		} catch (SerialException e) {
			this.data = null;
			System.out.println(getTable() + ": serial " + e.getMessage());
		} catch (SQLException e) {
			this.data = null;
			System.out.println(getTable() + ": sql error " + e.getMessage());
		}
	}

	public Blob getData() {
		return data;
	}

	public void setDataBlob(Blob data) {
		this.data = data;
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

	public void setType(String type) {
		this.type = Goods.type(type);
	}

	public static int type(String value) {
		String t = value.replace(",", "").replace(" ", "").replace(".", "");
		switch (t) {
		case "кг":
			return 0;
		case "шт":
			return 1;
		default:
			return 0;
		}
	}

	public int getNumber() {
		return code;
	}

	public void setNumber(int number) {
		this.code = number;
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

	public int getId_sections() {
		return id_sections;
	}

	public void setId_sections(int id_section) {
		this.id_sections = id_section;
	}

	public int getId_templates() {
		return id_templates;
	}

	public void setId_templates(int id_templates) {
		this.id_templates = id_templates;
	}

	public int getId_barcodes() {
		return id_barcodes;
	}

	public void setId_barcodes(int id_barcode) {
		this.id_barcodes = id_barcode;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public float getMin_type() {
		return min_type;
	}

	public void setMin_type(float min_type) {
		this.min_type = min_type;
	}

	public int getId_scales() {
		return id_scales;
	}

	public void setId_scales(int id_scale) {
		this.id_scales = id_scale;
	}

	public float getTara() {
		return tara;
	}

	public void setTara(float tara) {
		this.tara = tara;
	}

	@Override
	public String toString() {
		return "Goods{" +
				"id=" + id +
				", code=" + code +
				", id_scales=" + id_scales +
				", id_sections=" + id_sections +
				", id_templates=" + id_templates +
				", id_barcodes=" + id_barcodes +
				", name='" + name + '\'' +
				", full_name='" + full_name + '\'' +
				", price=" + price +
				", type=" + type +
				", pre_code=" + pre_code +
				", before_validity=" + before_validity +
				", ingredients='" + ingredients + '\'' +
				", min_type=" + min_type +
				", data=" + data +
				", tara=" + tara +
				'}';
	}
}
