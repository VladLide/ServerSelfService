package application.enums;

/**
 * Contains statuses which scale can have
 */
public enum ScaleStatus {
	INCORRECT_SCALE_ADDRESS("Не вірно введений адрес вагів"),
	NO_CONNECTION("Не має зв'язку"),
	CONNECTION_WAS_LOST_WITHOUT_UPDATE("Зв'язок був втрачений"),
	PRODUCTS_AT_DATABASE_ARE_UP_TO_DATE("Товари на вагах актуальні"),
	UPDATING_DONT_SHUTDOWN("Йде обновлення, не вимикати"),
	NO_PRODUCTS_AT_SCALES("Немає товарів на вагах");

	private String message;

	ScaleStatus(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
