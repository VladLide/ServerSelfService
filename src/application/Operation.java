package application;

/**
 * Contains operations which can be done with db items
 */
public enum Operation {
	SEND("відправка"),
	CREATE("створення"),
	UPDATE("оновлення"),
	DELETE("видалення");

	private final String name;

	Operation(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
