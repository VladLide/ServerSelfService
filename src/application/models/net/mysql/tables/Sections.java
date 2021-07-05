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

import application.models.net.PackingDBValue;
import application.models.net.mysql.MySQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundSize;

public class Sections {
	private int id = 0;
	private int id_up = 0;
	private int level = 0;
	private String name = "";
	private Blob img_data = null;
	private String description = "";
	private int number_s = 0;
	private int number_po = 0;

	public static String getTable() {
		String table = "sections";
		return table;
	}

	public List<String> getFields() {
		Sections me = this;
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : me.getClass().getDeclaredFields()) {
				System.out.println(table + "." + f.getName());
				fields.add(table + "." + f.getName());
			}
		} catch (Exception e) {
			System.out.println(getTable() + ": " + e.getMessage());
		}
		return fields;
	}

	public PackingDBValue[] getValues() {
		Sections me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
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

	public Sections() {}

	public Sections(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Sections(ResultSet res, boolean img) {
		super();
		for (Field f : getClass().getDeclaredFields()) {
			try {
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				if (!img && type.compareTo("Blob") == 0)
					continue;
				switch (type) {
				case "int":
					f.set(this, res.getInt(res.findColumn(f.getName())));
					break;
				case "float":
					f.set(this, res.getFloat(res.findColumn(f.getName())));
					break;
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
			} catch (Exception e) {
				System.out.println(getTable() + ": " + e.getMessage());
			}
		}
	}

	public static Sections get(int id, int id_up, int level, String name, boolean img, MySQL db) {
		ResultSet resul = db.getSelect(Sections.getSql(id, id_up, level, name, img));
		Sections res = null;
		try {
			while (resul.next()) {
				res = new Sections(resul, img);
			}
		} catch (SQLException e) {
			System.out.println(getTable() + ": " + e.getMessage());
		}
		return res;
	}

	public static ObservableList<Sections> getList(int id, int id_up, int level, String name, boolean img, MySQL db) {
		ResultSet resul = db.getSelect(Sections.getSql(id, id_up, level, name, img));
		ObservableList<Sections> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Sections(resul, img));
			}
		} catch (SQLException e) {
			System.out.println(getTable() + ": " + e.getMessage());
		}
		return row;
	}

	public static ObservableList<Object> getListObj(int id, int id_up, int level, String name, boolean img, MySQL db) {
		ResultSet resul = db.getSelect(Sections.getSql(id, id_up, level, name, img));
		ObservableList<Object> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Sections(resul, img));
			}
		} catch (SQLException e) {
			System.out.println(getTable() + ": " + e.getMessage());
		}
		return row;
	}

	public static ObservableList<String> getLName(MySQL db) {
		ResultSet resul = db.getSelect(Sections.getSql(0, -1, 0, "", false));
		ObservableList<String> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(resul.getString("name"));
			}
		} catch (SQLException e) {
			System.out.println(getTable() + ": " + e.getMessage());
		}
		return row;
	}

	public static String getSqlHeader(int type) {
		String sql = "*";
		switch (type) {
		case 1:
			sql = "id, id_up, level, name, description, number_s, number_po";
			break;

		default:
			break;
		}
		return sql;
	}

	public static String getSql(int id, int id_up, int level, String name, boolean img) {
		String table = getTable();
		String sql = "SELECT " + getSqlHeader(img ? 0 : 1) + " FROM " + table;
		if (id > 0 || id_up >= 0 || level > 0 || name.length() > 0)
			sql += " WHERE ";
		if (id > 0)
			sql += table + ".id = " + id;
		if (id > 0 && (id_up >= 0 || level > 0 || name.length() > 0))
			sql += " AND ";
		if (id_up >= 0)
			sql += table + ".id_up = " + id_up;
		if (id_up >= 0 && (level > 0 || name.length() > 0))
			sql += " AND ";
		if (level > 0)
			sql += table + ".level = " + level;
		if (level > 0 && name.length() > 0)
			sql += " AND ";
		if (name.length() > 0)
			sql += table + ".name = '" + name + "'";
		return sql;
	}

	public int save(MySQL db) {
		String table = getTable();
		Sections isNew = Sections.get(0, -1, 0, name, false, db);
		String[] fields = getFields().toArray(new String[0]);
		if (isNew == null) {
			db.insert(table, fields, getValues());
		} else {
			db.update(table, fields, getValues(), new String[] { table + ".id =" + isNew.getId() });
		}
		Sections tmp = Sections.get(0, -1, 0, name, false, db);
		id = tmp != null ? tmp.getId() : -1;
		return id;
	}

	public void updateId(int id, MySQL db) {
		String table = getTable();
		ObservableList<Sections> down = Sections.getList(0, this.id, 0, "", false, db);
		if (Sections.get(id, -1, 0, "", false, db) == null) {
			db.update(table, new String[] { (table + ".id") },
					PackingDBValue.get(new String[] { "int" }, new Object[] { id }),
					new String[] { table + ".id = " + this.id });
			Sections isNew = Sections.get(id, -1, 0, "", false, db);
			if (isNew != null) {
				this.setId(id);
				down.forEach((v) -> {
					v.setId_up(id);
					v.setLevel(isNew.getLevel() + 1, db);
					v.save(db);
				});
			}
		}
	}

	public void updateName(String name, MySQL db) {
		Sections me = this;
		String table = getTable();
		Sections isNew = Sections.get(0, -1, 0, name, false, db);
		if (isNew == null)
			db.update(table, new String[] { (table + ".name") },
					PackingDBValue.get(new String[] { "String" }, new Object[] { name }),
					new String[] { table + ".name = '" + me.getName() + "'" });
		if (Sections.get(0, -1, 0, name, false, db) != null)
			this.setName(name);
	}

	public boolean delete(boolean delDownId, MySQL db) {
		if (delDownId) {
			ObservableList<Sections> down = Sections.getList(0, this.id, 0, "", false, db);
			down.forEach((v) -> {
				v.delete(true, db);
			});
			String table = getTable();
			if (id > 0) {
				db.delete(table, new String[] { table + ".name ='" + name + "'" });
			}
		} else {
			Sections up = Sections.get(this.id_up, -1, 0, "", false, db);
			ObservableList<Sections> down = Sections.getList(0, this.id, 0, "", false, db);
			down.forEach((v) -> {
				if (up != null) {
					v.setLevel(up.getLevel() + 1, db);
					v.setId_up(up.getId());
				} else {
					v.setLevel(0, db);
					v.setId_up(0);
				}
				v.save(db);
			});
			String table = getTable();
			if (id > 0) {
				db.delete(table, new String[] { table + ".name ='" + name + "'" });
			}
		}
		return (Sections.get(0, -1, 0, name, false, db) == null) ? true : false;
	}

	@Override
	public String toString() {
		return name;
	}

	public Image getImage(AnchorPane panel) {
		InputStream is = null;
		Image image = null;
		try {
			is = (this.img_data != null) ? this.img_data.getBinaryStream() : null;
			image = new Image(is, BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, true);
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

	public Image getImage() {
		InputStream is = null;
		Image image = null;
		try {
			is = (this.img_data != null) ? this.img_data.getBinaryStream() : null;
			image = new Image(is);
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

	public int getNumber_s() {
		return number_s;
	}

	public void setNumber_s(int number_s) {
		this.number_s = number_s;
	}

	public int getNumber_po() {
		return number_po;
	}

	public void setNumber_po(int number_po) {
		this.number_po = number_po;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level, MySQL db) {
		ObservableList<Sections> down = Sections.getList(0, this.id, 0, "", false, db);
		down.forEach((v) -> {
			v.setLevel(level + 1, db);
			v.save(db);
		});
		this.level = level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getId_up() {
		return id_up;
	}

	public void setId_up(int id_up) {
		this.id_up = id_up;
	}
}