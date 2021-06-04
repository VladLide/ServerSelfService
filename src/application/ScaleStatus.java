package application;

/**
 * Contains statuses which scale can have
 */
public enum ScaleStatus {
	INCORRECT_SCALE_ADDRESS,
	NO_CONNECTION,
	CONNECTION_WAS_LOST_WITHOUT_UPDATE,
	PRODUCTS_AT_DATABASE_ARE_UP_TO_DATE,
	UPDATING_DONT_SHUTDOWN,
	NO_PRODUCTS_AT_SCALES;
}
