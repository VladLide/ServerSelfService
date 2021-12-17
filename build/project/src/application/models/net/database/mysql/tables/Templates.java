package application.models.net.database.mysql.tables;

import application.controllers.parts.TemplatePanelCtrl;
import application.controllers.windows.TemplateCtrl;
import application.models.TextBox;
import application.models.net.PackingDBValue;
import application.models.net.database.mysql.MySQL;
import application.models.template.*;
import application.views.languages.uk.windows.TemplateInfo;
import com.google.zxing.WriterException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Templates {
	private int id = 0;
	private String name = "";
	private Blob data = null;
	private Blob img_data = null;
	private Blob background_data = null;
	private String description = "";

	public Templates() {}

	public Templates(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public static String getTable() {
		String table = "templates";

		return table;
	}

	public List<String> getFields(boolean img) {
		String table = getTable();
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : getClass().getDeclaredFields()) {
				if(!img&&(f.getName()=="data"||f.getName()=="img_data"||f.getName()=="background_data")) continue;
				System.out.println(table + "." + f.getName());
				fields.add(table + "." + f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}

	public PackingDBValue[] getValues(boolean img) {
		Templates me = this;
		PackingDBValue[] values = new PackingDBValue[me.getClass().getDeclaredFields().length];
		int i = 0;
		for (Field f : me.getClass().getDeclaredFields()) {
			try {
				if(!img&&(f.getName()=="data"||f.getName()=="img_data"||f.getName()=="background_data"))
					continue;
				// if(f.getName()=="id") continue;
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
				e.printStackTrace();
			}
		}
		return values;
	}

	public Templates(ResultSet res) {
		super();
		for (Field f : getClass().getDeclaredFields()) {
			try {
				if (f.getName() == "inst")
					continue;
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				// if(!img&&type.compareTo("Blob")==0) continue;
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
				// TODO Auto-generated catch block
				System.out.println(getTable() + ": " + e);
			}
		}
	}

	public Templates(int id, String name, Blob data) {
		super();
		this.id = id;
		this.name = name;
		this.data = data;
	}

	public Templates(int id, String name, Blob data, Blob img_data, Blob background_data) {
		super();
		this.id = id;
		this.name = name;
		this.data = data;
		this.img_data = img_data;
		this.background_data = background_data;
	}

	public static Templates get(int rId, String name, boolean img, MySQL db) {
		Templates res = null;
		ResultSet resul = db.getSelect(Templates.getSql(rId, name, img, db));
		try {
			while (resul.next()) {
				res = new Templates(resul);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static ObservableList<Templates> getList(int rId, String name, boolean img, MySQL db) {
		ResultSet resul = db.getSelect(Templates.getSql(rId, name, img, db));
		ObservableList<Templates> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new Templates(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static ObservableList<Object> getListObj(int rId, String name, boolean img, MySQL db) {
		ResultSet resul = db.getSelect(Templates.getSql(rId, name, img, db));
		ObservableList<Object> rows = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				rows.add(new Templates(resul));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static ObservableList<String> getLName(MySQL db) {
		ResultSet resul = db.getSelect(Templates.getSql(0, "", false, db));
		ObservableList<String> row = FXCollections.observableArrayList();
		try {
			while (resul.next()) {
				row.add(new Templates(resul).getName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	public static String getSql(int rId, String name, boolean img, MySQL db) {
		String table = getTable();
		String sql = "SELECT " + StringUtils.join(new Templates().getFields(img).toArray(new String[0]), ",") + " FROM " + table;
		if (rId > 0 || name.length() > 0)
			sql += " WHERE ";
		if (rId > 0)
			sql += table + ".id = " + rId;
		if (rId > 0 && name.length() > 0)
			sql += " AND ";
		if (name.length() > 0)
			sql += table + ".name = \"" + name + "\"";
		sql += " ORDER BY " + table + ".name";
		return sql;
	}

	public int save(MySQL db) {
		String table = getTable();
		Templates isNew = Templates.get(0, name, false, db);
		String[] fields = getFields(true).toArray(new String[0]);
		if (isNew != null) {
			db.update(table, fields, getValues(true), new String[] { table + ".id = '" + id + "'" });
		} else {
			db.insert(table, fields, getValues(true));
		}
		Templates tmp = Templates.get(0, name, false,db);
		id = tmp!=null?tmp.getId():-1;
		return id;
	}

	public boolean updateId(int id, MySQL db) {
		boolean result = false;
		String table = getTable();
		if (Templates.get(id, "", false, db) == null) {
			db.update(table, new String[] { (table + ".id") },
					PackingDBValue.get(new String[] { int.class.getTypeName() }, new Object[] { id }),
					new String[] { table + ".id = " + this.id });
			if (Templates.get(id, "", false, db) != null) {
				this.setId(id);
				result = true;
			}
		}
		return result;
	}

	public boolean updateName(String name, MySQL db) {
		boolean result = false;
		String table = getTable();
		if (Templates.get(0, name, false, db) == null) {
			db.update(table, new String[] { (table + ".name") },
					PackingDBValue.get(new String[] { "String" }, new Object[] { name }),
					new String[] { table + ".name = '" + this.name + "'" });
			if (Templates.get(0, name, false, db) != null) {
				this.setId(id);
				result = true;
			}
		}
		return result;
	}

	public boolean delete(MySQL db) {
		String table = getTable();
		if (Templates.get(id, "", false, db) != null) {
			db.delete(table, new String[] { table + ".id ='" + id + "'" });
		}
		return Templates.get(id, "", false, db) == null;
	}

	public PaneObj readObjBlob(TemplatePanelCtrl panel, TemplateCtrl edit) {
		try {
			InputStream fin = this.data.getBinaryStream();
			ObjectInputStream ois = new ObjectInputStream(fin);
			SaveObj label = (SaveObj) ois.readObject();
			label.setId(this.getId());
			label.setName(this.getName());
			AnchorPane pane = (edit == null) ? panel.getTemplate() : edit.getTemplate();
			pane.setPrefSize(label.getHeightContent(), label.getWidthContent());
			pane.setMinSize(label.getHeightContent(), label.getWidthContent());
			pane.setStyle(label.getColor());
			if (edit == null)
				pane.getStyleClass().add("edit_pane");
			try {
				pane.setBackground(new Background(new BackgroundImage(this.getImage(pane, 1),
						BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
						new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true,
								false, true, false))));
			} catch (Exception e) {
				pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			}
			AnchorPane.setTopAnchor(pane, 0.0);
			AnchorPane.setLeftAnchor(pane, 0.0);
			PaneObj open = new PaneObj(pane, label.getHeightContent(), label.getWidthContent());
			open.setId(this.getId());
			open.setName(this.getName());
			label.getItems().forEach((k, v) -> {
				System.out.println("Key: " + k + " Value: " + v);
				switch (v.getType()) {
				case "barcode": {
					Pane obj = (edit == null) ? TemplatePanel.createViewBarcode(v.getOptions(), open)
							: edit.createViewBarcode(v.getOptions(), pane);
					if (obj != null) {
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						pane.getChildren().add(obj);
						Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
					break;
				}
				case "qrcode": {
					Pane obj;
					try {
						obj = (edit == null) ? TemplatePanel.createViewQRCode(v.getOptions(), open)
								: edit.createViewQRCode(v.getOptions(), pane);
						if (obj != null) {
							obj.setTranslateX(v.getPosition().x);
							obj.setTranslateY(v.getPosition().y);
							pane.getChildren().add(obj);
							Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
							i.setOptions(v.getOptions());
							open.setItem(i);
						}
					} catch (WriterException e) {
						System.out.println("Qrcode: " + e.getMessage());
					}
					break;
				}
				case "rectangle": {
					Rectangle obj = (edit == null) ? TemplatePanel.createViewRectangle(v.getOptions(), open)
							: edit.createViewRectangle(v.getOptions(), pane);
					if (obj != null) {
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						pane.getChildren().add(obj);
						Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
					break;
				}
				case "line": {
					Line obj = (edit == null) ? TemplatePanel.createViewLine(v.getOptions(), open)
							: edit.createViewLine(v.getOptions(), pane);
					if (obj != null) {
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						pane.getChildren().add(obj);
						Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
					break;
				}
				default: {
					Label obj = (edit == null) ? new Label((String) v.getItem())
							: edit.createViewLabel(v.getOptions().getFont(), (String) v.getItem(), pane);
					if (obj != null) {
						obj.setLayoutX(0);
						obj.setLayoutY(0);
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						obj.setFont(Font.font(v.getOptions().getFont().name,
								FontWeight.findByName(v.getOptions().getFont().fontWeight),
								v.getOptions().getFont().size));
						obj.setPrefWidth(v.getOptions().getWidth());
						obj.setPrefHeight(v.getOptions().getHeight());
						obj.setAlignment(Pos.valueOf(v.getOptions().getAlignment()));
						obj.setWrapText((v.getOptions().getWrapText().compareToIgnoreCase("true") == 0) ? true : false);
						obj.setTextAlignment(TextAlignment.CENTER);
						obj.setLineSpacing(v.getOptions().getLineSpacing());
						Double[] val = v.getOptions().getIndent();
						obj.setPadding(new Insets(val[0], val[1], val[2], val[3]));
						pane.getChildren().add(obj);
						Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
				}
					System.out.println(v.getType() + " :[ " + v.getPosition().x + ";" + v.getPosition().y + "]");
				}
			});
			ois.close();
			return open;
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			if (edit != null)
				TextBox.alertOpenDialog(AlertType.WARNING, "saveGoodsNo");
		} catch (InvalidClassException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int writeObjBlob(PaneObj label, MySQL db) {
		boolean[] item = new boolean[TemplateInfo.ItemsTemplate.size()];
		System.out.println(label.getId());
		this.setId(label.getId());
		this.setName(label.getName());
		System.out.println(this.getName());
		System.out.println(this.getId());
		try {
			WritableImage img = label.getPane().snapshot(new SnapshotParameters(), null);
			BufferedImage image = SwingFXUtils.fromFXImage(img, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "png", baos);
				baos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] img_data = baos.toByteArray();
			this.img_data = new SerialBlob(img_data);
			SaveObj save = new SaveObj(this.getId(), this.getName(), label.getHeightContent(), label.getWidthContent());
			save.setColor(label.getPane().getStyle());
			label.getItems().forEach((v) -> {
				System.out.println("Value: " + v);
				OptionsItem options = new OptionsItem();
				String t = null;
				switch (v.getType()) {
				case "barcode": {
					t = "barcode";
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setHeight(v.getOptions().getHeight());
					options.setHumanReadablePlacement(v.getOptions().getHumanReadablePlacement());
					options.setQuietZone(v.getOptions().getQuietZone());
					options.setWidth(v.getOptions().getWidth());
					options.setWidthModule(v.getOptions().getWidthModule());
					break;
				}
				case "qrcode": {
					t = "qrcode";
					options.setWidth(v.getOptions().getWidth());
					break;
				}
				case "rectangle": {
					t = "rectangle";
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setHeight(v.getOptions().getHeight());
					options.setHumanReadablePlacement(v.getOptions().getHumanReadablePlacement());
					options.setQuietZone(v.getOptions().getQuietZone());
					options.setWidth(v.getOptions().getWidth());
					options.setWidthModule(v.getOptions().getWidthModule());
					options.setRotate(v.getOptions().getRotate());
					options.setBorderWidth(v.getOptions().getBorderWidth());
					break;
				}
				case "line": {
					t = "line";
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setHeight(v.getOptions().getHeight());
					options.setHumanReadablePlacement(v.getOptions().getHumanReadablePlacement());
					options.setQuietZone(v.getOptions().getQuietZone());
					options.setWidth(v.getOptions().getWidth());
					options.setWidthModule(v.getOptions().getWidthModule());
					options.setRotate(v.getOptions().getRotate());
					options.setBorderWidth(v.getOptions().getBorderWidth());
					break;
				}
				default: {
					t = ((Label) v.getItem()).getText();
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setWidth(v.getOptions().getWidth());
					options.setHeight(v.getOptions().getHeight());
					options.setAlignment(v.getOptions().getAlignment());
					options.setWrapText(v.getOptions().getWrapText());
					options.setLineSpacing(v.getOptions().getLineSpacing());
					options.setIndent(v.getOptions().getIndent());
				}
				};
				if (t != null && v.getPosition() != null) {
					System.out.println(v.getType() + ":[ " + v.getPosition().x + ";" + v.getPosition().y + "]");
					Item i = new Item(v.getId(), v.getType(), t, new Point(v.getPosition().x, v.getPosition().y));
					if (v.getOptions().getFont() != null) {
						i.setOptions(options);
					} else {
						i.setOptions(null);
					}
					if (!item[v.getId()] || v.getId() == 7 || v.getId() == 20 || v.getId() == 21)
						save.setItem(i);
					item[v.getId()] = true;
				}
			});
			byte[] data = SerializationUtils.serialize(save);
			this.data = new SerialBlob(data);
			this.description = "Image: .png; Template: SaveObj";
			return this.save(db);
		} catch (SerialException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int readObjFile(TemplatePanelCtrl panel, MySQL db, File file) {
		this.setData(file, 0);
		String name = file.getName().replace(".", " ");
		if (name.split(" ").length > 0) {
			String[] typ = name.split(" ");
			name = typ[0];
		}
		File imgBackground = new File(file.getParent() + "/" + name + ".png");
		if (imgBackground.isFile())
			this.setData(imgBackground, 1);
		InputStream fi;
		try {
			fi = this.data.getBinaryStream();
			ObjectInputStream ois = new ObjectInputStream(fi);
			SaveObj label = (SaveObj) ois.readObject();
			this.id = label.getId();
			this.name = label.getName();
			AnchorPane pane = panel.getTemplate();
			pane.setPrefSize(label.getHeightContent(), label.getWidthContent());
			pane.setMinSize(label.getHeightContent(), label.getWidthContent());
			pane.setStyle(label.getColor());
			try {
				pane.setBackground(new Background(new BackgroundImage(this.getImage(pane, 1),
						BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
						new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true,
								false, true, false))));
			} catch (Exception e) {
				pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			}
			AnchorPane.setTopAnchor(pane, 0.0);
			AnchorPane.setLeftAnchor(pane, 0.0);
			PaneObj open = new PaneObj(pane, label.getHeightContent(), label.getWidthContent());
			open.setId(label.getId());
			open.setName(label.getName());
			label.getItems().forEach((k, v) -> {
				System.out.println("Key: " + k + " Value: " + v);
				switch (v.getType()) {
				case "barcode": {
					Pane obj = TemplatePanel.createViewBarcode(v.getOptions(), open);
					if (obj != null) {
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						pane.getChildren().add(obj);
						int index = pane.getChildren().indexOf(obj);
						Item i = new Item(index, v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
					break;
				}
				case "qrcode": {
					try {
						Pane obj = TemplatePanel.createViewQRCode(v.getOptions(), open);
						if (obj != null) {
							obj.setTranslateX(v.getPosition().x);
							obj.setTranslateY(v.getPosition().y);
							pane.getChildren().add(obj);
							Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
							i.setOptions(v.getOptions());
							open.setItem(i);
						}
					} catch (WriterException e) {
						System.out.println("Key: " + k + " error: " + e.getMessage());
					}
					break;
				}
				case "rectangle": {
					Rectangle obj = TemplatePanel.createViewRectangle(v.getOptions(), open);
					if (obj != null) {
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						pane.getChildren().add(obj);
						Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
					break;
				}
				case "line": {
					Line obj = TemplatePanel.createViewLine(v.getOptions(), open);
					if (obj != null) {
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						pane.getChildren().add(obj);
						Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
					break;
				}
				default: {
					Label obj = new Label((String) v.getItem());
					if (obj != null) {
						obj.setLayoutX(0);
						obj.setLayoutY(0);
						obj.setTranslateX(v.getPosition().x);
						obj.setTranslateY(v.getPosition().y);
						obj.setFont(Font.font(v.getOptions().getFont().name,
								FontWeight.findByName(v.getOptions().getFont().fontWeight),
								v.getOptions().getFont().size));
						obj.setPrefWidth(v.getOptions().getWidth());
						obj.setPrefHeight(v.getOptions().getHeight());
						obj.setAlignment(Pos.valueOf(v.getOptions().getAlignment()));
						obj.setWrapText((v.getOptions().getWrapText().compareToIgnoreCase("true") == 0) ? true : false);
						obj.setTextAlignment(TextAlignment.CENTER);
						obj.setLineSpacing(v.getOptions().getLineSpacing());
						Double[] val = v.getOptions().getIndent();
						obj.setPadding(new Insets(val[0], val[1], val[2], val[3]));
						pane.getChildren().add(obj);
						Item i = new Item(v.getId(), v.getType(), obj, null, v.getPosition());
						i.setOptions(v.getOptions());
						open.setItem(i);
					}
				}
				System.out.println(v.getType() + " :[ " + v.getPosition().x + ";" + v.getPosition().y + "]");
				};
			});
			WritableImage img = open.getPane().snapshot(new SnapshotParameters(), null);
			BufferedImage image = SwingFXUtils.fromFXImage(img, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "png", baos);
				baos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			byte[] img_data = baos.toByteArray();
			this.img_data = new SerialBlob(img_data);
			ois.close();
			fi.close();
			return this.save(db);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidClassException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SerialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public int writeObjFile(Stage stage, File folder) {
		String type = this.getClass().getName().replace(".", " ");
		if (type.split(" ").length > 0) {
			String[] typ = type.split(" ");
			type = typ[typ.length - 1];
		}
		String name = this.name.replace("*", "_");
		File fileTemplate = new File(folder.getPath() + "/" + type + "/" + this.id + "-" + name + ".templ");
		fileTemplate.getParentFile().mkdirs();
		try {
			if (background_data != null) {
				File imgBackground = new File(folder.getPath() + "/" + type + "/" + this.id + "-" + name + ".png");
				imgBackground.getParentFile().mkdirs();
				imgBackground.createNewFile();
				BufferedImage image = ImageIO.read(background_data.getBinaryStream());
				ImageIO.write(image, "png", imgBackground);
			}
			fileTemplate.createNewFile();
			FileOutputStream f = new FileOutputStream(fileTemplate);
			ObjectOutputStream osTemplate = new ObjectOutputStream(f);
			InputStream fin = this.data.getBinaryStream();
			ObjectInputStream ois = new ObjectInputStream(fin);
			osTemplate.writeObject(ois.readObject());
			osTemplate.close();
			f.close();
			return 1;
		} catch (SerialException e) {
			TextBox.alertOpenDialog(AlertType.WARNING, "error: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int setTemplate(PaneObj label) {
		boolean[] item = new boolean[TemplateInfo.ItemsTemplate.size()];
		System.out.println(label.getId());
		this.setId(label.getId());
		this.setName(label.getName());
		System.out.println(this.getName());
		System.out.println(this.getId());
		try {
			WritableImage img = label.getPane().snapshot(new SnapshotParameters(), null);
			BufferedImage image = SwingFXUtils.fromFXImage(img, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "png", baos);
				baos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] img_data = baos.toByteArray();
			this.img_data = new SerialBlob(img_data);
			SaveObj save = new SaveObj(this.getId(), this.getName(), label.getHeightContent(), label.getWidthContent());
			save.setColor(label.getPane().getStyle());
			label.getItems().forEach((v) -> {
				System.out.println("Value: " + v);
				OptionsItem options = new OptionsItem();
				String t = null;
				switch (v.getType()) {
				case "barcode": {
					t = "barcode";
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setHeight(v.getOptions().getHeight());
					options.setHumanReadablePlacement(v.getOptions().getHumanReadablePlacement());
					options.setQuietZone(v.getOptions().getQuietZone());
					options.setWidth(v.getOptions().getWidth());
					options.setWidthModule(v.getOptions().getWidthModule());
					break;
				}
				case "qrcode": {
					t = "qrcode";
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setHeight(v.getOptions().getHeight());
					options.setHumanReadablePlacement(v.getOptions().getHumanReadablePlacement());
					options.setQuietZone(v.getOptions().getQuietZone());
					options.setWidth(v.getOptions().getWidth());
					options.setWidthModule(v.getOptions().getWidthModule());
					break;
				}
				case "rectangle": {
					t = "rectangle";
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setHeight(v.getOptions().getHeight());
					options.setHumanReadablePlacement(v.getOptions().getHumanReadablePlacement());
					options.setQuietZone(v.getOptions().getQuietZone());
					options.setWidth(v.getOptions().getWidth());
					options.setWidthModule(v.getOptions().getWidthModule());
					options.setRotate(v.getOptions().getRotate());
					options.setBorderWidth(v.getOptions().getBorderWidth());
					break;
				}
				case "line": {
					t = "line";
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setHeight(v.getOptions().getHeight());
					options.setHumanReadablePlacement(v.getOptions().getHumanReadablePlacement());
					options.setQuietZone(v.getOptions().getQuietZone());
					options.setWidth(v.getOptions().getWidth());
					options.setWidthModule(v.getOptions().getWidthModule());
					options.setRotate(v.getOptions().getRotate());
					options.setBorderWidth(v.getOptions().getBorderWidth());
					break;
				}
				default: {
					t = ((Label) v.getItem()).getText();
					options.setFont(new FontItem(v.getOptions().getFont().name, v.getOptions().getFont().fontWeight,
							v.getOptions().getFont().size));
					options.setWidth(v.getOptions().getWidth());
					options.setHeight(v.getOptions().getHeight());
					options.setAlignment(v.getOptions().getAlignment());
					options.setWrapText(v.getOptions().getWrapText());
					options.setLineSpacing(v.getOptions().getLineSpacing());
					options.setIndent(v.getOptions().getIndent());
				}
				}
				;
				if (t != null && v.getPosition() != null) {
					System.out.println(v.getType() + ":[ " + v.getPosition().x + ";" + v.getPosition().y + "]");
					Item i = new Item(v.getId(), v.getType(), t, new Point(v.getPosition().x, v.getPosition().y));
					if (v.getOptions().getFont() != null) {
						i.setOptions(options);
					} else {
						i.setOptions(null);
					}
					if (!item[v.getId()] || v.getId() == 7)
						save.setItem(i);
					item[v.getId()] = true;
				}
			});
			byte[] data = SerializationUtils.serialize(save);
			this.data = new SerialBlob(data);
			this.description = "Image: .png; Template: SaveObj";
		} catch (SerialException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Image getImage(AnchorPane panel, int i) {
		InputStream is = null;
		Image image = null;
		try {
			switch (i) {
			case 0: {
				is = this.img_data.getBinaryStream();
				break;
			}
			case 1: {
				is = this.background_data.getBinaryStream();
				break;
			}
			}
			image = new Image(is, panel.getWidth(), panel.getHeight(), true, true);
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

	public Image getImage(int i) {
		InputStream is = null;
		Image image = null;
		try {
			switch (i) {
			case 0: {
				is = this.img_data.getBinaryStream();
				break;
			}
			case 1: {
				is = this.background_data.getBinaryStream();
				break;
			}
			}
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

	public void setData(File file, int i) {
		try {
			Path filePath = Paths.get(file.getAbsolutePath());
			byte[] fileContent = Files.readAllBytes(filePath);
			switch (i) {
			case 0: {
				this.data = new SerialBlob(fileContent);
				break;
			}
			case 1: {
				this.background_data = new SerialBlob(fileContent);
				break;
			}
			}
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

	public void clear() {
		try {
			if (img_data != null)
				img_data.free();
			if (data != null)
				data.free();
			if (background_data != null)
				background_data.free();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return name;
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

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
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

	public Blob getBackground_data() {
		return background_data;
	}

	public void setBackground_data(Blob background_data) {
		this.background_data = background_data;
	}

}