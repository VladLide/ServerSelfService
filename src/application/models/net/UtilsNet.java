package application.models.net;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import application.models.Utils;

public class UtilsNet {

	public UtilsNet() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getFields(String table, Field[] fieldsObj) {
		List<String> fields = new ArrayList<String>();
		try {
			for (Field f : fieldsObj) {
				System.out.println(table + "." + f.getName());
				fields.add(table + "." + f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fields;
	}

	public PackingDBValue[] getValues(Field[] fields, Object obj) {
		PackingDBValue[] values = new PackingDBValue[fields.length];
		int i = 0;
		for (Field f : fields) {
			try {
				String type = f.getType().getTypeName().replace(".", " ");
				if (type.split(" ").length > 0) {
					String[] typ = type.split(" ");
					type = typ[typ.length - 1];
				}
				switch (type) {
				case "Integer":
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
					System.out.println(Utils.getTypeObj(obj) + ": type was not found " + f.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}

}
