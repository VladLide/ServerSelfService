package application.models.net;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import application.models.net.database.mysql.MySQL;

public class ControllerData {
	public static void setValue(ResultSet res, Object obj) {
		for (Field f : obj.getClass().getDeclaredFields()) {
			try {
				if (f.getName() == "inst")
					continue;
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				switch (type) {
				case "int":
					f.set(obj, res.getInt(res.findColumn(f.getName())));
					break;
				case "float":
					f.set(obj, res.getFloat(res.findColumn(f.getName())));
					break;
				case "double":
					f.set(obj, res.getDouble(res.findColumn(f.getName())));
					break;
				case "String":
					f.set(obj, res.getString(res.findColumn(f.getName())));
					break;
				case "Blob":
					f.set(obj, res.getBlob(res.findColumn(f.getName())));
					break;
				default:
					System.out.println(obj.getClass().getCanonicalName() + ": type was not found " + f.getName() + ":"
							+ f.getType());
				}
			} catch (Exception e) {
				System.out.println(obj.getClass().getCanonicalName() + ": " + e);
			}
		}
	}

	public List<String> getFields(String table, Object obj) {
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : obj.getClass().getDeclaredFields()) {
				if (f.getName() == "inst" || f.getName() == "id")
					continue;
				System.out.println(table + "." + f.getName());
				fields.add(table + "." + f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}

	public PackingDBValue[] getValues(List<String> Fields, Object obj) {
		PackingDBValue[] values = new PackingDBValue[Fields.size()];
		try {
			int i = 0;
			for (Field f : obj.getClass().getDeclaredFields()) {
				if (f.getName() == "inst" || f.getName() == "id" || f.getName() == "id_scales")
					continue;
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				switch (type) {
				case "int":
					values[i++] = new PackingDBValue(f.getName(), "I", (Object) f.get(obj));
					break;
				case "float":
					values[i++] = new PackingDBValue(f.getName(), "F", (Object) f.get(obj));
					break;
				case "double":
					values[i++] = new PackingDBValue(f.getName(), "D", (Object) f.get(obj));
					break;
				case "String":
					values[i++] = new PackingDBValue(f.getName(), "S", (Object) f.get(obj));
					break;
				case "Blob":
					values[i++] = new PackingDBValue(f.getName(), "B", (Object) f.get(obj));
					break;
				default:
					System.out.println(Fields.get(0).replace(".", " ").split(" ")[0] + ": type was not found "
							+ f.getName() + ":" + f.getType());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
}
