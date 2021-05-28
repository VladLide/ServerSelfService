package application.models.net;

public class PackingDBValue {
	public String type = "";
	public String column = "";
	public Object data = null;
	public PackingDBValue(String column, String type, Object object) {
		this.type = type;
		this.column = column;
		this.data = object;
	}
	public static PackingDBValue[] get(String[] type, Object[] object) {
		PackingDBValue[] values = new PackingDBValue[object.length];
		for (int i=0;i<object.length;i++) {
			String typet = type[i].replace(".", " ");
			if(typet.split(" ").length>0) {
				String[] typ = typet.split(" ");
				typet = typ[typ.length-1];
			}
			switch(typet) {
				case "int":
					values[i] = new PackingDBValue("","I",object[i]);
				break;
				case "float":
					values[i] = new PackingDBValue("","F",object[i]);
				break;
				case "double": 
					values[i] = new PackingDBValue("","D",object[i]);
				break;
				case "String":
					values[i] = new PackingDBValue("","S",object[i]);
				break;
				case "Blob":
					values[i] = new PackingDBValue("","B",object[i]);
				break;
				default:
					System.out.println("PackingDBValue: type was not found " + object[i].getClass().getTypeName()+":"+type);
			}
		}
		return values;
	}
}
