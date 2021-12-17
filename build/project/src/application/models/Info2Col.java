package application.models;

public class Info2Col {
	private String name;
	private String value;
	private int type = 0;

	public Info2Col(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Info2Col(String name, String value, int type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
