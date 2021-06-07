package application;

/**
 * Contains places at which operation can happen
 */
public enum PlaceType {
	SCALE("ваги"),
	SERVER("сервер");

	private final String name;

	PlaceType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
