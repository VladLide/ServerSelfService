package application.models.net;

import application.Helper;
import application.models.Utils;
import application.models.net.mysql.tables.Distribute;
import application.models.net.mysql.tables.Goods;
import application.models.net.mysql.tables.Scales;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

public class PackingDBValue {
	public String type = "";
	public String column = "";
	public Object data = null;
	private static final Logger logger = LogManager.getLogger(PackingDBValue.class);

	public PackingDBValue(String column, String type, Object object) {
		this.type = type;
		this.column = column;
		this.data = object;
	}

	public static PackingDBValue[] get(String[] type, Object[] object) {
		PackingDBValue[] values = new PackingDBValue[object.length];
		for (int i = 0; i < object.length; i++) {
			String typet = type[i].replace(".", " ");
			if (typet.split(" ").length > 0) {
				String[] typ = typet.split(" ");
				typet = typ[typ.length - 1];
			}
			switch (typet) {
				case "int":
					values[i] = new PackingDBValue("", "I", object[i]);
					break;
				case "float":
					values[i] = new PackingDBValue("", "F", object[i]);
					break;
				case "double":
					values[i] = new PackingDBValue("", "D", object[i]);
					break;
				case "String":
					values[i] = new PackingDBValue("", "S", object[i]);
					break;
				case "Blob":
					values[i] = new PackingDBValue("", "B", object[i]);
					break;
				default:
					System.out.println(
							"PackingDBValue: type was not found " + object[i].getClass().getTypeName() + ":" + type);
			}
		}
		return values;
	}

	public static Optional<PackingDBValue[]> get(Object object) {
		try {
			String fullClassName = Utils.getFullTypeName(object);
			Class<?> clazz = Helper.getClassByFullName(fullClassName)
					.orElseThrow(() -> new NullPointerException("No class for name " + fullClassName + " was found"));
			Field[] fields = clazz.cast(object).getClass().getDeclaredFields();

			int size;
			if (Class.forName(fullClassName).equals(Scales.class) || Class.forName(fullClassName).equals(Goods.class)) {
				size = fields.length - 1;
			} else {
				size = fields.length;
			}

			PackingDBValue[] values = new PackingDBValue[size];
			int i = 0;

			for (Field field : fields) {
				String[] split = field.getType().getTypeName().replace(".", " ").split(" ");
				String type = split[split.length - 1];
				String fieldName = field.getName();
				if (fieldName.equalsIgnoreCase("inst")
						|| fieldName.equalsIgnoreCase("config")
						|| fieldName.equalsIgnoreCase("db")
						|| (fullClassName.equals(Goods.class.getTypeName()) && fieldName.equalsIgnoreCase("id"))) {
					continue;
				}

				field.setAccessible(true);

				switch (type) {
					case "Integer":
					case "int":
						values[i++] = new PackingDBValue(field.getName(), "I", field.get(clazz.cast(object)));
						break;
					case "float":
						values[i++] = new PackingDBValue(field.getName(), "F", field.get(clazz.cast(object)));
						break;
					case "double":
						values[i++] = new PackingDBValue(field.getName(), "D", field.get(clazz.cast(object)));
						break;
					case "String":
						values[i++] = new PackingDBValue(field.getName(), "S", field.get(clazz.cast(object)));
						break;
					case "Blob":
						values[i++] = new PackingDBValue(field.getName(), "B", field.get(clazz.cast(object)));
						break;
					case "LocalDateTime":
						values[i++] = new PackingDBValue(field.getName(), "DT", field.get(clazz.cast(object)));
						break;
					case "boolean":
						values[i++] = new PackingDBValue(field.getName(), "BL", field.get(clazz.cast(object)));
						break;
				}

				field.setAccessible(false);
			}

			return Optional.of(values);
		} catch (IllegalArgumentException | NullPointerException | ClassNotFoundException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		}

		return Optional.empty();
	}

	@Override
	public String toString() {
		return "PackingDBValue{" +
				"type='" + type + '\'' +
				", column='" + column + '\'' +
				", data=" + data +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		PackingDBValue that = (PackingDBValue) o;

		return new EqualsBuilder().append(type, that.type).append(column, that.column).append(data, that.data).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(type).append(column).append(data).toHashCode();
	}
}
