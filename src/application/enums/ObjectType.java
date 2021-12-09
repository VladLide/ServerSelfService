package application.enums;

public enum ObjectType {
	PRODUCTS("goods", "code"), SECTIONS("sections", "id"), TEMPLATES("templates", "id"),
	TEMPLATES_CODES("barcodes", "id"), SETTINGS("scales", "id");

	private final String tableName;
	private final String orderByColumn;
	// private final NullPointerException throwable;

	ObjectType(String tableName, String orderByColumn) {
		this.tableName = tableName;
		this.orderByColumn = orderByColumn;
		// this.throwable = new NullPointerException(String.format("Was not able to get
		// any %s from db", tableName));
	}

	public String getTableName() {
		return tableName;
	}

	public String getOrderByColumn() {
		return orderByColumn;
	}

	public NullPointerException getNullPointerException() {
		return new NullPointerException(String.format("Was not able to get any %s from db", tableName));
	}
}
